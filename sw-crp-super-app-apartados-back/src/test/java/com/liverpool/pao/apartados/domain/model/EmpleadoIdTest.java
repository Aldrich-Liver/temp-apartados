package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpleadoIdTest {

    @Test
    @DisplayName("Debe crear EmpleadoId con un valor válido")
    void creadoConValorValidoRetornaId() {
        // When
        var id = EmpleadoId.de("16618035");

        // Then
        assertEquals("16618035", id.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Debe lanzar excepción cuando el id es nulo, vacío o en blanco")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> EmpleadoId.de(valor));

        // Then
        assertEquals("El id de empleado es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe contener el valor del id")
    void toStringContieneValor() {
        // Given
        var id = EmpleadoId.de("16618035");

        // When
        var texto = id.toString();

        // Then
        assertTrue(texto.contains("16618035"));
    }
}
