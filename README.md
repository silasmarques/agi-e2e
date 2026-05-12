# agi-e2e

> Automação E2E do blog do Agibank (`blog.agibank.com.br`) com **Selenium 4 + Java 21 + JUnit 5**, organizada em **Page Object Model** profissional.

![CI](https://github.com/silasmarques/agi-e2e/actions/workflows/e2e.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Selenium](https://img.shields.io/badge/Selenium-4.33-green?logo=selenium)
![Tests](https://img.shields.io/badge/testes-17%20passing-brightgreen)

📊 **[Ver Allure Report](https://silasmarques.github.io/agi-e2e/)**

---

## Sobre o projeto

O [Blog do Agibank](https://blog.agibank.com.br) é um canal de educação financeira e marketing de conteúdo do banco. Sua função principal é gerar tráfego orgânico, converter visitantes em clientes e oferecer ferramentas úteis — como calculadoras financeiras.

Este projeto cobre os **cenários de maior valor de negócio**:

- **Funil de conversão:** os CTAs que levam o leitor ao app e ao site do banco.
- **Calculadoras:** validação de lógica matemática independente do front-end.

Os testes rodam em modo **headless** (sem interface gráfica) por padrão — o browser executa em background, ideal para pipelines de CI/CD.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal |
| Maven | 3.9.15 | Gerenciador de build |
| Selenium | 4.33.0 | Automação de browser |
| JUnit | 5.12.2 | Framework de testes |
| AssertJ | 3.27.3 | Assertions fluentes |
| REST Assured | 5.5.2 | Validações via HTTP (App Store / Google Play) |
| Allure Report | 2.29.1 | Relatório visual de execução |
| Logback / SLF4J | 1.5.18 / 2.0.17 | Logs de execução |

> O **Selenium Manager** (nativo no Selenium 4) baixa o ChromeDriver automaticamente na primeira execução — sem necessidade de instalação manual.

---

## Pré-requisitos

Antes de executar, verifique se você tem instalado:

| Requisito | Versão mínima | Como verificar |
|---|---|---|
| JDK | 21 | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| Google Chrome | Qualquer versão estável | `chrome --version` |

> **JAVA_HOME** precisa estar configurado corretamente nas variáveis de ambiente para o Maven funcionar.

---

## Configuração e execução

### 1. Clone o repositório

```bash
git clone git@github.com:silasmarques/agi-e2e.git
cd agi-e2e
```

### 2. Execute os testes

**Modo headless** (padrão recomendado — sem interface gráfica, equivalente ao CI):

```bash
mvn test -Dheadless=true
```

**Modo headed** (com browser visível — útil para debug):

```bash
mvn test
```

**Execução de uma classe de teste isolada:**

```bash
mvn test -Dtest=IrParaSiteTest -Dheadless=true
mvn test -Dtest=DownloadAppTest -Dheadless=true
mvn test -Dtest=CalculadoraJurosTest -Dheadless=true
mvn test -Dtest=CalculadoraDiasUteisTest -Dheadless=true
```

### 3. Gere e visualize o relatório Allure

```bash
# Abre o relatório no browser automaticamente
mvn allure:serve

# Gera o relatório em target/site/allure-maven-plugin/
mvn allure:report
```

> O relatório Allure também é gerado automaticamente a cada execução via GitHub Actions e disponibilizado como artifact na aba **Actions** do repositório.

---

## Cenários de teste

**Total: 17 testes** | Todos os testes executam em modo **headless** no CI.

---

### Módulo 1 — Funil de conversão: CTA "Ir para o site"

#### CT-01 — Home do blog deve carregar com sucesso

```
Dado  que o avaliador acessa blog.agibank.com.br
Quando a página é renderizada
Então o logo do blog deve estar visível
  E   o título da página não deve estar em branco
  E   a URL deve conter "blog.agibank.com.br"
```

#### CT-02 — CTA deve levar ao site principal do banco preservando o rastreio de campanha

```
Dado  que o usuário está na home do blog
Quando clica no link "Ir para o site" no menu principal
Então deve ser redirecionado para agibank.com.br
  E   a URL de destino deve conter utm_source=blog (rastreio do funil preservado)
  E   o título da página de destino deve mencionar "Agibank"
```

> **Por que esse teste importa:** se o UTM quebrar em um deploy, o time de marketing perde rastreabilidade das conversões vindas do blog sem nenhum aviso.

---

### Módulo 2 — Funil de conversão: Download do app

#### CT-03 — Badges de download devem estar visíveis e apontar para os apps oficiais

```
Dado  que o usuário acessa a home do blog
Quando rola até o rodapé da página
Então o badge da App Store deve estar visível
  E   o link deve conter "apps.apple.com" e o ID do app "id1173498435"
  E   o badge do Google Play deve estar visível
  E   o link deve conter "play.google.com" e o package "br.com.agipag.app"
```

#### CT-04 — Páginas das lojas devem responder e confirmar o Agibank como desenvolvedor

```
Dado  que os links dos badges foram extraídos do rodapé
Quando uma requisição HTTP é feita para cada loja (App Store e Google Play)
Então a App Store deve responder com status 200
  E   o conteúdo da página deve mencionar "Agibank"
  E   o Google Play deve responder com status 200
  E   o conteúdo da página deve mencionar "Agibank"
```

> **Decisão técnica:** as lojas são SPAs pesadas com lazy load e proteção anti-bot. A validação é feita via HTTP com User-Agent realista (sem abrir browser nas páginas das lojas), tornando o teste mais rápido e estável.

---

### Módulo 3 — Calculadora de Juros Compostos

> A calculadora está embutida em um **iframe externo** dentro do blog. A fórmula usada é `M = C × (1 + i)^t` (modo Investimento com aporte mensal zero). O resultado exibido é comparado com um **oracle Java independente** (`JurosCompostosCalc`) usando `BigDecimal` com arredondamento `HALF_EVEN` e tolerância de `R$ 0,01`.

#### CT-05 a CT-09 — Montante calculado deve estar correto (parametrizado)

```
Dado  que o usuário acessa a Calculadora de Juros Compostos
  E   seleciona o modo Investimento
Quando preenche os campos com os valores da tabela abaixo e clica em "Calcular Agora"
Então o montante exibido deve bater com a fórmula M = C × (1 + i)^t (tolerância ±R$ 0,01)
```

| CT | Capital | Taxa mensal | Período | Montante esperado |
|---|---|---|---|---|
| CT-05 | R$ 1.000,00 | 1,00% | 12 meses | R$ 1.126,83 |
| CT-06 | R$ 5.000,00 | 0,50% | 24 meses | R$ 5.635,80 |
| CT-07 | R$ 10.000,00 | 2,00% | 6 meses | R$ 11.261,62 |
| CT-08 | R$ 100,00 | 10,00% | 3 meses | R$ 133,10 |
| CT-09 | R$ 50.000,00 | 0,80% | 36 meses | R$ 66.654,55 |

---

### Módulo 4 — Calculadora de Dias Úteis

> A calculadora considera automaticamente os **10 feriados nacionais de 2026** e dois toggles opcionais (incluir sábado / incluir domingo). O oracle Java `DiasUteisCalc` replica a mesma lógica do JavaScript para validação independente.

> Todos os testes usam datas de 2026 validadas com `DayOfWeek` em Java antes da implementação.

#### CT-10 — Mesma data (domingo) sem toggles → zero dias úteis

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 07/06/2026 (domingo) e Data Final: 07/06/2026
  E   não marca nenhum toggle
  E   clica em "Calcular Agora"
Então o resultado deve ser 0 dias úteis
```

#### CT-11 — Fim de semana completo sem toggles → zero dias úteis

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 06/06/2026 (sábado) e Data Final: 07/06/2026 (domingo)
  E   não marca nenhum toggle
  E   clica em "Calcular Agora"
Então o resultado deve ser 0 dias úteis
```

#### CT-12 — Toggle "incluir sábado" não afeta período de apenas domingo

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 07/06/2026 (domingo) e Data Final: 07/06/2026
  E   marca o toggle "Incluir Sábado"
  E   clica em "Calcular Agora"
Então o resultado deve ser 0 dias úteis
  (pois incluir sábado não altera a contagem de um período que contém apenas domingo)
```

#### CT-13 — Toggle "incluir domingo" conta o próprio dia como útil

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 07/06/2026 (domingo) e Data Final: 07/06/2026
  E   marca o toggle "Incluir Domingo"
  E   clica em "Calcular Agora"
Então o resultado deve ser 1 dia útil
```

#### CT-14 — Happy path: semana de trabalho comum (quarta a quarta)

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 03/06/2026 (quarta) e Data Final: 10/06/2026 (quarta)
  E   não marca nenhum toggle
  E   clica em "Calcular Agora"
Então o resultado deve ser 6 dias úteis
  (qua 03, qui 04, sex 05, seg 08, ter 09, qua 10 — sáb e dom excluídos)
```

#### CT-15 — Período com feriado nacional (Natal)

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 24/12/2026 (quinta) e Data Final: 28/12/2026 (segunda)
  E   não marca nenhum toggle
  E   clica em "Calcular Agora"
Então o resultado deve ser 2 dias úteis
  (qui 24 + seg 28; sex 25/12 = Natal é feriado, sáb e dom excluídos)
```

#### CT-16 — Feriado em domingo prevalece sobre toggle "incluir domingo"

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 14/11/2026 (sábado) e Data Final: 16/11/2026 (segunda)
  E   marca os toggles "Incluir Sábado" e "Incluir Domingo"
  E   clica em "Calcular Agora"
Então o resultado deve ser 2 dias úteis
  (sáb 14 + seg 16; dom 15/11 = feriado "Proclamação da República" — NÃO conta mesmo com toggle marcado)
```

#### CT-17 — Mensagem de erro quando data inicial é posterior à data final

```
Dado  que o usuário acessa a Calculadora de Dias Úteis
Quando preenche Data Inicial: 08/06/2026 e Data Final: 06/06/2026
  E   clica em "Calcular Agora"
Então deve exibir a mensagem de erro:
  "A data inicial deve ser anterior à data final"
  E   o resultado NÃO deve ser exibido
```

---

## Arquitetura — Page Object Model

```
src/test/java/com/agie2e/
│
├── core/
│   ├── Config.java                     # Lê variáveis de ambiente e system properties
│   ├── DriverFactory.java              # Cria o WebDriver (Chrome, headed ou headless)
│   ├── WaitFactory.java                # Encapsula WebDriverWait com condições reutilizáveis
│   ├── BaseTest.java                   # @BeforeEach / @AfterEach com gestão do driver
│   └── FailureEvidenceExtension.java   # JUnit 5 TestWatcher — captura evidências só em falha
│
├── pages/
│   ├── HomePage.java                   # blog.agibank.com.br
│   ├── AgibankSitePage.java            # agibank.com.br (destino do CTA)
│   ├── CalculadoraJurosCompostosPage.java
│   └── CalculadoraDiasUteisPage.java
│
├── components/
│   ├── HeaderComponent.java            # Menu superior (CTA "Ir para o site")
│   └── FooterComponent.java           # Rodapé (badges App Store / Google Play)
│
├── tests/
│   ├── IrParaSiteTest.java
│   ├── DownloadAppTest.java
│   ├── CalculadoraJurosTest.java
│   └── CalculadoraDiasUteisTest.java
│
├── data/
│   ├── JurosTestData.java              # @MethodSource para os 5 casos de juros
│   ├── DiasUteisTestData.java          # @MethodSource para os 7 casos de dias úteis
│   └── AppStoreExpected.java           # IDs e regex das lojas
│
├── clients/
│   ├── AppStoreClient.java             # GET HTTP na App Store (REST Assured)
│   └── GooglePlayClient.java           # GET HTTP no Google Play (REST Assured)
│
└── utils/
    ├── JurosCompostosCalc.java         # Oracle: M = C × (1+i)^t em BigDecimal
    ├── DiasUteisCalc.java              # Oracle: contagem com feriados e toggles
    └── EvidenceCollector.java          # Captura screenshot, HTML e URL para o Allure
```

**Princípio central:** o teste expressa intenção de negócio, a Page expressa intenção de tela, o seletor é detalhe interno.

```java
// O teste lê como uma regra de negócio, não como um script de cliques
AgibankSitePage site = new HomePage(driver)
        .acessar()
        .header()
        .clicarIrParaSite()
        .aguardarCarregamento();

assertThat(site.urlContemUtmDoBlog()).isTrue();
```

---

## Evidências em falha

`FailureEvidenceExtension` implementa `TestWatcher` do JUnit 5 e anexa automaticamente ao Allure **apenas quando o teste falha**:

- 📸 Screenshot da tela no momento da falha
- 📄 HTML completo da página
- 🔗 URL atual do browser

---

## Relatório Allure

O Allure Report exibe cada cenário com seus passos, tempo de execução e evidências em caso de falha.

🔗 **Relatório online (atualizado a cada execução):** [silasmarques.github.io/agi-e2e](https://silasmarques.github.io/agi-e2e/)

Para visualizar localmente após executar os testes:

```bash
mvn test -Dheadless=true   # executa os testes
mvn allure:serve            # abre o relatório no browser
```

---

## CI/CD — GitHub Actions

Arquivo: `.github/workflows/e2e.yml`

| Trigger | Descrição |
|---|---|
| `workflow_dispatch` | Execução manual pela interface do GitHub |
| `schedule` (cron) | Diariamente às 08:00 BRT |

- Chrome stable instalado no runner Ubuntu.
- Relatório Allure publicado automaticamente em [silasmarques.github.io/agi-e2e](https://silasmarques.github.io/agi-e2e/) após cada execução.
- **Não dispara em pull requests** — o site testado é de terceiro (Agibank), não há código nosso pra validar em PR.
- **Atenção:** o GitHub desativa workflows agendados automaticamente após 60 dias sem atividade no repositório. Para reativar, acesse a aba Actions e habilite o workflow manualmente.

