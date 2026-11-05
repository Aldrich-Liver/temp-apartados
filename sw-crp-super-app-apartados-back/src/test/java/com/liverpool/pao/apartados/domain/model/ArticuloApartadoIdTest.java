package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArticuloApartadoIdTest {

    @Test
    @DisplayName("Debe crear ArticuloApartadoId con un valor válido")
    void creadoConValorValidoRetornaId() {
        // When
        var id = ArticuloApartadoId.de("ART-001");

        // Then
        assertEquals("ART-001", id.value());
    }

    @Test
    @DisplayName("nuevo debe generar ids distintos")
    void nuevoGeneraIdsUnicos() {
        // When
        var id1 = ArticuloApartadoId.nuevo();
        var id2 = ArticuloApartadoId.nuevo();

        // Then
        assertNotNull(id1.value());
        assertNotEquals(id1, id2);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Debe lanzar excepción cuando el id es nulo, vacío o en blanco")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> ArticuloApartadoId.de(valor));

        // Then
        assertEquals("El id del artículo de apartado es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe contener el valor del id")
    void toStringContieneValor() {
        // Given
        var id = ArticuloApartadoId.de("ART-001");

        // When
        var texto = id.toString();

        // Then
        assertTrue(texto.contains("ART-001"));
    }
}
