package com.liverpool.pao.apartados.domain.model;

/**
 * Cantidad de piezas de un SKU en una línea de apartado.
 * El tope de negocio (ERF: 10 vs 20) se resuelve en política de aplicación/dominio;
 * este VO solo garantiza un entero positivo.
 */
public record Cantidad(int value) {

    public Cantidad {
        if (value <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser un entero positivo.");
        }
    }

    public static Cantidad de(final int value) {
        return new Cantidad(value);
    }

    public Cantidad sumar(final Cantidad otra) {
        return new Cantidad(Math.addExact(this.value, otra.value));
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
