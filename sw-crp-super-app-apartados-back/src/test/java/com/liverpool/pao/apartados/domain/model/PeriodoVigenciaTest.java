package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeriodoVigenciaTest {

    private static final LocalDateTime INICIO = LocalDateTime.now().toLocalDate().atStartOfDay();
    private static final LocalDateTime FIN = INICIO.plusDays(30).withHour(23).withMinute(59);
    private static final LocalDateTime MITAD = INICIO.plusDays(15).withHour(12);

    @Test
    @DisplayName("Debe crear PeriodoVigencia con inicio y fin válidos")
    void creadoConRangoValidoRetornaPeriodo() {
        // When
        var vigencia = PeriodoVigencia.de(INICIO, FIN);

        // Then
        assertEquals(INICIO, vigencia.inicio());
        assertEquals(FIN, vigencia.fin());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el fin es anterior al inicio")
    void creadoConFinAnteriorAlInicioLanzaExcepcion() {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                PeriodoVigencia.de(FIN, INICIO));

        // Then
        assertEquals("La vigencia no puede terminar antes de iniciar.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando inicio es nulo")
    void creadoConInicioNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () ->
                PeriodoVigencia.de(null, FIN));

        // Then
        assertEquals("La fecha de inicio de vigencia es obligatoria.", excepcion.getMessage());
    }

    @Test
    @DisplayName("contiene debe ser true para un momento dentro del periodo inclusive")
    void contieneMomentoDentroDelPeriodo() {
        // Given
        var vigencia = PeriodoVigencia.de(INICIO, FIN);

        // When / Then
        assertTrue(vigencia.contiene(INICIO));
        assertTrue(vigencia.contiene(FIN));
        assertTrue(vigencia.contiene(MITAD));
        assertFalse(vigencia.contiene(FIN.plusSeconds(1)));
    }

    @Test
    @DisplayName("estaVencidoEn debe ser true solo después del fin")
    void estaVencidoEnDespuesDelFin() {
        // Given
        var vigencia = PeriodoVigencia.de(INICIO, FIN);

        // When / Then
        assertFalse(vigencia.estaVencidoEn(FIN));
        assertTrue(vigencia.estaVencidoEn(FIN.plusSeconds(1)));
    }
}
