package com.liverpool.pao.apartados.domain.model;

/**
 * Referencia del monedero asociado al apartado (captura Zapa02 / PAO).
 */
public record MonederoApartado(String value) {

    public MonederoApartado {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La referencia del monedero es obligatoria.");
        }
        value = value.strip();
    }

    public static MonederoApartado de(final String value) {
        return new MonederoApartado(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
