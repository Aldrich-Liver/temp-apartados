package com.liverpool.pao.apartados.domain.model;

/**
 * SKU canónico de mercancía. Se normaliza quitando ceros a la izquierda.
 */
public record Sku(String value) {

    public Sku {
        if (value == null) {
            throw new IllegalArgumentException("El SKU es obligatorio.");
        }
        value = value.strip().replaceFirst("^0+", "");
        if (value.isBlank()) {
            throw new IllegalArgumentException("El SKU es inválido (vacío tras quitar ceros).");
        }
    }

    public static Sku de(final String crudo) {
        return new Sku(crudo);
    }

    @Override
    public String toString() {
        return value;
    }
}
