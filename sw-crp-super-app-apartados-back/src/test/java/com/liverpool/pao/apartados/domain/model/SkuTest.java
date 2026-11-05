package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkuTest {

    @Test
    @DisplayName("Debe crear Sku canónico quitando ceros a la izquierda")
    void creadoConCerosALaIzquierdaRetornaValorCanonico() {
        // When
        var sku = Sku.de("000000001080618685");

        // Then
        assertEquals("1080618685", sku.value());
    }

    @Test
    @DisplayName("Dos Sku con y sin ceros a la izquierda deben ser iguales")
    void skusConYSinCerosSonIguales() {
        // Given
        var conCeros = Sku.de("000123");
        var sinCeros = Sku.de("123");

        // When
        boolean iguales = conCeros.equals(sinCeros);

        // Then
        assertTrue(iguales);
        assertEquals(conCeros.hashCode(), sinCeros.hashCode());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el SKU es nulo")
    void creadoConValorNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> Sku.de(null));

        // Then
        assertEquals("El SKU es obligatorio.", excepcion.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "000", "0000"})
    @DisplayName("Debe lanzar excepción cuando el SKU queda vacío tras normalizar")
    void creadoConValorInvalidoLanzaExcepcion(final String valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> Sku.de(valor));

        // Then
        assertEquals("El SKU es inválido (vacío tras quitar ceros).", excepcion.getMessage());
    }

    @Test
    @DisplayName("toString debe devolver el valor canónico")
    void toStringDevuelveValorCanonico() {
        // Given
        var sku = Sku.de("000456");

        // When
        var texto = sku.toString();

        // Then
        assertEquals("456", texto);
    }
}
