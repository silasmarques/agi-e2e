package com.agie2e.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Oracle: fórmula de juros compostos para validar a calculadora do blog.
 * <p>Investimento com aporte único: <code>M = C × (1 + i)^t</code>.</p>
 * <p>Investimento com aportes mensais: <code>M = C × (1+i)^t + A × ((1+i)^t − 1) / i</code>.</p>
 * <p>Replicada do JS da própria calculadora (calcularInvestimento) para servir de oráculo
 * independente em Java/BigDecimal — não usamos o número exibido como sua própria validação.</p>
 */
public final class JurosCompostosCalc {

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);

    private JurosCompostosCalc() {}

    /**
     * @param capital      valor inicial (C)
     * @param aporteMensal aporte mensal adicional (A) — 0 para juros compostos puros
     * @param taxaMensal   taxa por período em fração (ex: 0,01 para 1% ao mês)
     * @param meses        número de períodos (t)
     * @return montante final arredondado para 2 casas em HALF_EVEN
     */
    public static BigDecimal montante(BigDecimal capital, BigDecimal aporteMensal,
                                       BigDecimal taxaMensal, int meses) {
        BigDecimal fator = BigDecimal.ONE.add(taxaMensal).pow(meses, MC);
        BigDecimal montanteCapital = capital.multiply(fator, MC);

        BigDecimal montanteAportes = BigDecimal.ZERO;
        if (aporteMensal.signum() > 0 && taxaMensal.signum() > 0) {
            BigDecimal fatorAportes = fator.subtract(BigDecimal.ONE, MC).divide(taxaMensal, MC);
            montanteAportes = aporteMensal.multiply(fatorAportes, MC);
        }

        return montanteCapital.add(montanteAportes, MC).setScale(2, RoundingMode.HALF_EVEN);
    }

    /** Converte taxa percentual (ex: 1.0 para 1%) em fração (0.01). */
    public static BigDecimal taxaPercentualParaFracao(BigDecimal percentual) {
        return percentual.divide(BigDecimal.valueOf(100), MC);
    }
}
