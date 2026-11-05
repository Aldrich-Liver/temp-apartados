package com.liverpool.pao.apartados.domain.model;

/**
 * Identificador del empleado creador del apartado.
 */
public record EmpleadoId(String value) {

    public EmpleadoId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id de empleado es obligatorio.");
        }
        value = value.strip();
    }

    public static EmpleadoId de(final String value) {
        return new EmpleadoId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
