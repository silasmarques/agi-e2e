package com.agie2e.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.agie2e.core.BaseTest;
import com.agie2e.pages.AgibankSitePage;
import com.agie2e.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Funil de conversão")
@Feature("CTA Ir para o site")
public class IrParaSiteTest extends BaseTest {

    @Test
    @Story("Carregamento da home")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Home do blog deve carregar com sucesso")
    @Description("Valida que blog.agibank.com.br carrega, exibe o logo e tem título preenchido.")
    void homeDeveCarregarComSucesso() {
        HomePage home = new HomePage(driver).acessar();

        assertThat(home.carregouComSucesso())
                .as("home deveria carregar com logo visível e título preenchido")
                .isTrue();
        assertThat(home.titulo())
                .as("título da home")
                .isNotBlank();
        assertThat(home.urlAtual())
                .as("URL atual deve apontar para o blog")
                .contains("blog.agibank.com.br");
    }

    @Test
    @Story("CTA Ir para o site")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CTA do header deve levar ao agibank.com.br preservando o UTM do blog")
    @Description("Critério de conversão: ao clicar em 'Ir para o site' no header, o usuário deve cair "
            + "no agibank.com.br com utm_source=blog (rastreio de funil) e título contendo 'Agibank'.")
    void ctaIrParaSiteDeveLevarAoSitePrincipalComUtmDoBlog() {
        HomePage home = new HomePage(driver).acessar();

        assertThat(home.header().exibeLinkIrParaSite())
                .as("link 'Ir para o site' deve estar visível no header")
                .isTrue();
        assertThat(home.header().hrefIrParaSite())
                .as("href do CTA deve apontar para agibank.com.br com UTM do blog")
                .contains("agibank.com.br")
                .contains("utm_source=blog");

        AgibankSitePage site = home.header().clicarIrParaSite().aguardarCarregamento();

        assertThat(site.urlAtual())
                .as("URL final após clicar deve ser do agibank.com.br")
                .contains("agibank.com.br");
        assertThat(site.urlContemUtmDoBlog())
                .as("UTM source do blog deve ser preservado no destino")
                .isTrue();
        assertThat(site.titleContemAgibank())
                .as("título da página destino deve mencionar Agibank")
                .isTrue();
    }
}
