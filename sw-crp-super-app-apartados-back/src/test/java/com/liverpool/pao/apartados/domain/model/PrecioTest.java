package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecioTest {

    @Test
    @DisplayName("Debe crear Precio con escala de 2 decimales")
    void creadoConValorValidoNormalizaEscala() {
        // When
        var precio = Precio.de(new BigDecimal("10.5"));

        // Then
        assertEquals(new BigDecimal("10.50"), precio.value());
    }

    @Test
    @DisplayName("Debe crear Precio desde String")
    void creadoDesdeStringRetornaPrecio() {
        // When
        var precio = Precio.de("99.99");

        // Then
        assertEquals(new BigDecimal("99.99"), precio.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el precio es nulo")
    void creadoConValorNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () -> Precio.de((BigDecimal) null));

        // Then
        assertEquals("El precio es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el precio es negativo")
    void creadoConValorNegativoLanzaExcepcion() {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                Precio.de(new BigDecimal("-0.01")));

        // Then
        assertEquals("El precio no puede ser negativo.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe calcular Importe multiplicando por Cantidad")
    void porCantidadRetornaImporte() {
        // Given
        var precio = Precio.de("25.50");
        var cantidad = Cantidad.de(2);

        // When
        var importe = precio.por(cantidad);

        // Then
        assertEquals(new BigDecimal("51.00"), importe.value());
    }
}
