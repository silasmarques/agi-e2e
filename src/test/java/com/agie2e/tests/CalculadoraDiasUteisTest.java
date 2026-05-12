package com.agie2e.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.agie2e.core.BaseTest;
import com.agie2e.data.DiasUteisTestData;
import com.agie2e.pages.CalculadoraDiasUteisPage;
import com.agie2e.utils.DiasUteisCalc;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Epic("Ferramentas do blog")
@Feature("Calculadora de Dias Úteis")
public class CalculadoraDiasUteisTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.agie2e.data.DiasUteisTestData#casos")
    @Story("Contagem de dias úteis com toggles de sábado/domingo e feriados nacionais")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Contagem exibida deve bater com o oracle Java em diferentes cenários")
    @Description("Para cada caso: abre a calculadora, preenche datas e toggles, clica Calcular "
            + "e compara o número exibido com DiasUteisCalc.contar() (oracle Java independente).")
    void contagemDeveBaterComOracle(String rotulo,
                                    LocalDate inicio, LocalDate fim,
                                    boolean incluirSabado, boolean incluirDomingo,
                                    long esperado) {
        long calculadoPeloOracle = DiasUteisCalc.contar(inicio, fim, incluirSabado, incluirDomingo);
        assertThat(calculadoPeloOracle)
                .as("sanity check: oracle Java deve bater com o esperado declarado em DiasUteisTestData")
                .isEqualTo(esperado);

        long exibido = new CalculadoraDiasUteisPage(driver)
                .acessar()
                .preencherDataInicio(inicio)
                .preencherDataFim(fim)
                .marcarIncluirSabado(incluirSabado)
                .marcarIncluirDomingo(incluirDomingo)
                .calcular()
                .diasUteis();

        assertThat(exibido)
                .as("dias úteis exibidos pela calculadora (%s)", rotulo)
                .isEqualTo(esperado);
    }

    @Test
    @Story("Validação: data inicial posterior à final")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Quando data inicial > data final, deve mostrar mensagem de erro e não calcular")
    @Description("Caso #1 do plano: 08/06/2026 → 06/06/2026. "
            + "Espera mensagem 'A data inicial deve ser anterior à data final'.")
    void dataInicialPosteriorAFinalDeveExibirMensagemDeErro() {
        String mensagem = new CalculadoraDiasUteisPage(driver)
                .acessar()
                .preencherDataInicio(LocalDate.of(2026, 6, 8))
                .preencherDataFim(LocalDate.of(2026, 6, 6))
                .clicarCalcularEsperandoErro()
                .mensagemDeErro();

        assertThat(mensagem)
                .as("mensagem de erro quando data inicial > final")
                .contains("data inicial")
                .contains("anterior")
                .contains("final");
    }
}
