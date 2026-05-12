package com.agie2e.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.agie2e.core.BaseTest;
import com.agie2e.data.JurosTestData;
import com.agie2e.pages.CalculadoraJurosCompostosPage;
import com.agie2e.utils.JurosCompostosCalc;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Epic("Ferramentas do blog")
@Feature("Calculadora de Juros Compostos")
public class CalculadoraJurosTest extends BaseTest {

    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");

    @ParameterizedTest(name = "C={0}, taxa={1}%/mes, t={2} meses")
    @MethodSource("com.agie2e.data.JurosTestData#casosInvestimentoPuro")
    @Story("Cálculo de juros compostos puros (sem aporte mensal)")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Montante exibido deve bater com a fórmula M = C × (1+i)^t (tolerância R$ 0,01)")
    @Description("Para cada caso, abre a calculadora, escolhe Investimento, zera aporte mensal, "
            + "preenche capital/taxa/período (mensal/meses) e clica Calcular. "
            + "Compara o montante exibido com o oracle Java (BigDecimal HALF_EVEN).")
    void montanteDeveBaterComFormulaJurosCompostosPuros(BigDecimal capital,
                                                        BigDecimal taxaPercentualMensal,
                                                        int meses) {
        BigDecimal taxaFracao = JurosCompostosCalc.taxaPercentualParaFracao(taxaPercentualMensal);
        BigDecimal esperado = JurosCompostosCalc.montante(
                capital, BigDecimal.ZERO, taxaFracao, meses);

        BigDecimal exibido = new CalculadoraJurosCompostosPage(driver)
                .acessar()
                .escolherInvestimento()
                .preencherInvestimento(capital, BigDecimal.ZERO, taxaPercentualMensal, meses,
                        "mensal", "meses")
                .calcular()
                .montanteFinal();

        assertThat(exibido)
                .as("montante exibido (C=%s, i=%s%%, t=%d) deve bater com a fórmula",
                        capital, taxaPercentualMensal, meses)
                .isCloseTo(esperado, within(TOLERANCIA));
    }
}
