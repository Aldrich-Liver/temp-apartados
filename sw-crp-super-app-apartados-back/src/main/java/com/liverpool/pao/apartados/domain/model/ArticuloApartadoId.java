package com.liverpool.pao.apartados.domain.model;

import java.util.UUID;

/**
 * Identidad de una línea de mercancía dentro del agregado Apartado.
 */
public record ArticuloApartadoId(String value) {

    public ArticuloApartadoId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id del artículo de apartado es obligatorio.");
        }
        value = value.strip();
    }

    public static ArticuloApartadoId nuevo() {
        return new ArticuloApartadoId(UUID.randomUUID().toString());
    }

    public static ArticuloApartadoId de(final String value) {
        return new ArticuloApartadoId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
