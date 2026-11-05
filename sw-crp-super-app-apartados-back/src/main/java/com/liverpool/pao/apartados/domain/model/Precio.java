package com.liverpool.pao.apartados.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Precio unitario de un SKU. Escala monetaria de 2 decimales; no negativo.
 */
public record Precio(BigDecimal value) {

    private static final int ESCALA = 2;

    public Precio {
        Objects.requireNonNull(value, "El precio es obligatorio.");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        value = value.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    public static Precio de(final BigDecimal value) {
        return new Precio(value);
    }

    public static Precio de(final String value) {
        return new Precio(new BigDecimal(value));
    }

    public Importe por(final Cantidad cantidad) {
        return Importe.de(value.multiply(BigDecimal.valueOf(cantidad.value())));
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
