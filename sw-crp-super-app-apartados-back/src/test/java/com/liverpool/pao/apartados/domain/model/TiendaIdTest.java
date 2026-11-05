package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TiendaIdTest {

    @Test
    @DisplayName("Debe crear TiendaId con un valor válido")
    void creadoConValorValidoRetornaId() {
        // When
        var id = TiendaId.de("0042");

        // Then
        assertEquals("0042", id.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Debe lanzar excepción cuando el id es nulo, vacío o en blanco")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> TiendaId.de(valor));

        // Then
        assertEquals("El id de tienda es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe contener el valor del id")
    void toStringContieneValor() {
        // Given
        var id = TiendaId.de("0042");

        // When
        var texto = id.toString();

        // Then
        assertTrue(texto.contains("0042"));
    }
}
