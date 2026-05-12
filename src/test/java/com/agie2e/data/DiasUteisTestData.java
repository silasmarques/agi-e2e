package com.agie2e.data;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Fonte de dados parametrizados para CalculadoraDiasUteisTest.
 * <p>Tuplas: (rotulo, inicio, fim, incluirSabado, incluirDomingo, esperado).
 * O rótulo aparece no @DisplayName parametrizado para identificar o caso na execução.</p>
 */
public final class DiasUteisTestData {

    private DiasUteisTestData() {}

    public static Stream<Arguments> casos() {
        return Stream.of(
                // === Cenários originais do Silas ===
                Arguments.of(
                        "Mesma data num domingo, sem toggles → 0",
                        LocalDate.of(2026, 6, 7), LocalDate.of(2026, 6, 7),
                        false, false, 0L),
                Arguments.of(
                        "Sábado a domingo, sem toggles → 0",
                        LocalDate.of(2026, 6, 6), LocalDate.of(2026, 6, 7),
                        false, false, 0L),
                Arguments.of(
                        "Domingo com incluir-sábado (sem efeito) → 0",
                        LocalDate.of(2026, 6, 7), LocalDate.of(2026, 6, 7),
                        true, false, 0L),
                Arguments.of(
                        "Domingo com incluir-domingo → 1",
                        LocalDate.of(2026, 6, 7), LocalDate.of(2026, 6, 7),
                        false, true, 1L),

                // === Cenários A, B, C sugeridos ===
                Arguments.of(
                        "A — Semana de trabalho qua a qua, sem feriados → 6",
                        LocalDate.of(2026, 6, 3), LocalDate.of(2026, 6, 10),
                        false, false, 6L),
                Arguments.of(
                        "B — Período com Natal (sex 25/12) → 2 (qui + seg)",
                        LocalDate.of(2026, 12, 24), LocalDate.of(2026, 12, 28),
                        false, false, 2L),
                Arguments.of(
                        "C — Feriado num domingo (15/11) prevalece sobre incluir-dom → 2",
                        LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 16),
                        true, true, 2L)
        );
    }
}
