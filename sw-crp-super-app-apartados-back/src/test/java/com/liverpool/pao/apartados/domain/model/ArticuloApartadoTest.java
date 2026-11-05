package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArticuloApartadoTest {

    private static final Sku SKU = Sku.de("1080618685");
    private static final Cantidad CANTIDAD = Cantidad.de(2);
    private static final Precio PRECIO = Precio.de("100.00");

    @Test
    @DisplayName("Debe crear ArticuloApartado con id e importe calculado")
    void creadoConValoresValidosRetornaArticulo() {
        // When
        var articulo = ArticuloApartado.crear(SKU, CANTIDAD, PRECIO);

        // Then
        assertNotNull(articulo.id());
        assertEquals(SKU, articulo.sku());
        assertEquals(CANTIDAD, articulo.cantidad());
        assertEquals(PRECIO, articulo.precio());
        assertEquals(new BigDecimal("200.00"), articulo.importe().value());
    }

    @Test
    @DisplayName("Dos llamadas a crear deben generar ids distintos")
    void crearGeneraIdsUnicos() {
        // When
        var a = ArticuloApartado.crear(SKU, CANTIDAD, PRECIO);
        var b = ArticuloApartado.crear(SKU, CANTIDAD, PRECIO);

        // Then
        assertNotEquals(a.id(), b.id());
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Debe reconstruir ArticuloApartado con id explícito")
    void deConIdRetornaArticulo() {
        // Given
        var id = ArticuloApartadoId.de("ART-001");

        // When
        var articulo = ArticuloApartado.de(id, SKU, CANTIDAD, PRECIO);

        // Then
        assertEquals(id, articulo.id());
    }

    @Test
    @DisplayName("actualizarCantidad debe recalcular el importe")
    void actualizarCantidadRecalculaImporte() {
        // Given
        var articulo = ArticuloApartado.crear(SKU, CANTIDAD, PRECIO);

        // When
        articulo.actualizarCantidad(Cantidad.de(5));

        // Then
        assertEquals(5, articulo.cantidad().value());
        assertEquals(new BigDecimal("500.00"), articulo.importe().value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando sku es nulo")
    void creadoConSkuNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () ->
                ArticuloApartado.crear(null, CANTIDAD, PRECIO));

        // Then
        assertEquals("El SKU es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar cantidad nula")
    void actualizarCantidadNulaLanzaExcepcion() {
        // Given
        var articulo = ArticuloApartado.crear(SKU, CANTIDAD, PRECIO);

        // When
        var excepcion = assertThrows(NullPointerException.class, () ->
                articulo.actualizarCantidad(null));

        // Then
        assertEquals("La cantidad es obligatoria.", excepcion.getMessage());
    }
}
