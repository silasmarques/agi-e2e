package com.agie2e.data;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Fonte de dados parametrizados para o teste de juros compostos.
 * Cobre cenários típicos do plano de teste (smoke + casos limite).
 * <p>Os valores esperados NÃO são hardcoded — o teste usa JurosCompostosCalc como oracle.</p>
 */
public final class JurosTestData {

    private JurosTestData() {}

    /**
     * (capital, taxaPercentualMensal, meses)
     */
    public static Stream<Arguments> casosInvestimentoPuro() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000.00"), new BigDecimal("1.00"), 12),
                Arguments.of(new BigDecimal("5000.00"), new BigDecimal("0.50"), 24),
                Arguments.of(new BigDecimal("10000.00"), new BigDecimal("2.00"), 6),
                Arguments.of(new BigDecimal("100.00"), new BigDecimal("10.00"), 3),
                Arguments.of(new BigDecimal("50000.00"), new BigDecimal("0.80"), 36)
        );
    }
}
