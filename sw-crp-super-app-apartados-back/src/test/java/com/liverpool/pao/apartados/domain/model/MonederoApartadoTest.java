package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonederoApartadoTest {

    @Test
    @DisplayName("Debe crear MonederoApartado con un valor válido")
    void creadoConValorValidoRetornaMonedero() {
        // When
        var monedero = MonederoApartado.de("MON-123");

        // Then
        assertEquals("MON-123", monedero.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Debe lanzar excepción cuando la referencia es nula, vacía o en blanco")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> MonederoApartado.de(valor));

        // Then
        assertEquals("La referencia del monedero es obligatoria.", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe contener la referencia del monedero")
    void toStringContieneValor() {
        // Given
        var monedero = MonederoApartado.de("MON-123");

        // When
        var texto = monedero.toString();

        // Then
        assertTrue(texto.contains("MON-123"));
    }
}
