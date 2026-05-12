package com.agie2e.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * Oracle: cálculo de dias úteis entre duas datas (inclusive nas pontas).
 * <p>Replica a lógica do JS da calculadora do blog (calcular() em
 * calculadora_dias_uteis_final_v4.html) para validação independente.</p>
 * <p>Regras (na ordem em que o JS avalia):</p>
 * <ol>
 *   <li>Se for feriado nacional → NÃO conta (independe dos toggles).</li>
 *   <li>Sábado sem incluir-sábado → NÃO conta.</li>
 *   <li>Domingo sem incluir-domingo → NÃO conta.</li>
 *   <li>Caso contrário → conta.</li>
 * </ol>
 */
public final class DiasUteisCalc {

    public static final Set<LocalDate> FERIADOS_2026 = Set.of(
            LocalDate.of(2026, 1, 1),    // Confraternização Universal
            LocalDate.of(2026, 4, 3),    // Paixão de Cristo
            LocalDate.of(2026, 4, 21),   // Tiradentes
            LocalDate.of(2026, 5, 1),    // Dia do Trabalho
            LocalDate.of(2026, 9, 7),    // Independência
            LocalDate.of(2026, 10, 12),  // Nossa Senhora Aparecida
            LocalDate.of(2026, 11, 2),   // Finados
            LocalDate.of(2026, 11, 15),  // Proclamação da República
            LocalDate.of(2026, 11, 20),  // Consciência Negra
            LocalDate.of(2026, 12, 25)   // Natal
    );

    private DiasUteisCalc() {}

    public static long contar(LocalDate inicio, LocalDate fim,
                              boolean incluirSabado, boolean incluirDomingo) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à final");
        }

        long count = 0;
        LocalDate atual = inicio;
        while (!atual.isAfter(fim)) {
            DayOfWeek dia = atual.getDayOfWeek();
            boolean ehSabado = dia == DayOfWeek.SATURDAY;
            boolean ehDomingo = dia == DayOfWeek.SUNDAY;
            boolean ehFeriado = FERIADOS_2026.contains(atual);

            if (ehFeriado) {
                // não conta — feriado prevalece sobre toggles
            } else if ((ehSabado && !incluirSabado) || (ehDomingo && !incluirDomingo)) {
                // não conta — fim de semana sem toggle correspondente
            } else {
                count++;
            }
            atual = atual.plusDays(1);
        }
        return count;
    }
}
