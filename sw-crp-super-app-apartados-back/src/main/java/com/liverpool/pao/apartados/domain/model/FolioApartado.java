package com.liverpool.pao.apartados.domain.model;

/**
 * Folio comercial del apartado. La autoridad del consecutivo (p. ej. SAP) se confirma
 * fuera del dominio; aquí solo se protege la forma del valor.
 */
public record FolioApartado(String value) {

    public FolioApartado {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El folio del apartado es obligatorio.");
        }
        value = value.strip();
    }

    public static FolioApartado de(final String value) {
        return new FolioApartado(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
