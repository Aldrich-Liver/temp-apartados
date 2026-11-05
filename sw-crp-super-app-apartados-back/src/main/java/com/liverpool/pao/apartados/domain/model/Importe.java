package com.liverpool.pao.apartados.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Importe monetario de una línea o del apartado. Escala de 2 decimales; no negativo.
 */
public record Importe(BigDecimal value) {

    private static final int ESCALA = 2;

    public Importe {
        Objects.requireNonNull(value, "El importe es obligatorio.");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo.");
        }
        value = value.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    public static Importe de(final BigDecimal value) {
        return new Importe(value);
    }

    public static Importe cero() {
        return new Importe(BigDecimal.ZERO);
    }

    public Importe sumar(final Importe otro) {
        return new Importe(this.value.add(otro.value));
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
