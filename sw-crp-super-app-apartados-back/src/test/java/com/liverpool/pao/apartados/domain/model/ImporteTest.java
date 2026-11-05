package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImporteTest {

    @Test
    @DisplayName("Debe crear Importe con escala de 2 decimales")
    void creadoConValorValidoNormalizaEscala() {
        // When
        var importe = Importe.de(new BigDecimal("1.2"));

        // Then
        assertEquals(new BigDecimal("1.20"), importe.value());
    }

    @Test
    @DisplayName("cero debe retornar Importe en 0.00")
    void ceroRetornaImporteCero() {
        // When
        var importe = Importe.cero();

        // Then
        assertEquals(new BigDecimal("0.00"), importe.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el importe es nulo")
    void creadoConValorNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () -> Importe.de(null));

        // Then
        assertEquals("El importe es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el importe es negativo")
    void creadoConValorNegativoLanzaExcepcion() {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                Importe.de(new BigDecimal("-1")));

        // Then
        assertEquals("El importe no puede ser negativo.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe sumar dos importes")
    void sumarRetornaNuevoImporte() {
        // Given
        var a = Importe.de(new BigDecimal("10.25"));
        var b = Importe.de(new BigDecimal("5.75"));

        // When
        var resultado = a.sumar(b);

        // Then
        assertEquals(new BigDecimal("16.00"), resultado.value());
    }
}
