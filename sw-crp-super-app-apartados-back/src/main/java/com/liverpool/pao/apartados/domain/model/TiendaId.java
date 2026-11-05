package com.liverpool.pao.apartados.domain.model;

/**
 * Identificador de la tienda de origen del apartado.
 */
public record TiendaId(String value) {

    public TiendaId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id de tienda es obligatorio.");
        }
        value = value.strip();
    }

    public static TiendaId de(final String value) {
        return new TiendaId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
