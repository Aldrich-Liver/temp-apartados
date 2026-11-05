package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FolioApartadoTest {

    @Test
    @DisplayName("Debe crear FolioApartado con un valor válido")
    void creadoConValorValidoRetornaFolio() {
        // When
        var folio = FolioApartado.de("FOL-001");

        // Then
        assertEquals("FOL-001", folio.value());
    }

    @Test
    @DisplayName("Debe recortar espacios al crear el folio")
    void creadoConEspaciosRetornaValorRecortado() {
        // When
        var folio = FolioApartado.de("  FOL-002  ");

        // Then
        assertEquals("FOL-002", folio.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Debe lanzar excepción cuando el folio es nulo, vacío o en blanco")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> FolioApartado.de(valor));

        // Then
        assertEquals("El folio del apartado es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe contener el valor del folio")
    void toStringContieneValor() {
        // Given
        var folio = FolioApartado.de("FOL-001");

        // When
        var texto = folio.toString();

        // Then
        assertTrue(texto.contains("FOL-001"));
    }
}
