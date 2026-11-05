package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CantidadTest {

    @Test
    @DisplayName("Debe crear Cantidad con un valor positivo")
    void creadaConValorPositivoRetornaCantidad() {
        // When
        var cantidad = Cantidad.de(5);

        // Then
        assertEquals(5, cantidad.value());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Debe lanzar excepción cuando el valor no es positivo")
    void creadaConValorNoPositivoLanzaExcepcion(final int valor) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () -> Cantidad.de(valor));

        // Then
        assertEquals("La cantidad debe ser un entero positivo.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe sumar dos cantidades")
    void sumarRetornaNuevaCantidad() {
        // Given
        var a = Cantidad.de(3);
        var b = Cantidad.de(7);

        // When
        var resultado = a.sumar(b);

        // Then
        assertEquals(10, resultado.value());
    }

    @Test
    @DisplayName("toString debe contener el valor numérico")
    void toStringContieneValor() {
        // Given
        var cantidad = Cantidad.de(12);

        // When
        var texto = cantidad.toString();

        // Then
        assertEquals("12", texto);
    }
}
