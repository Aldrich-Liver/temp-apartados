package com.liverpool.pao.apartados.domain.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Datos de contacto del cliente capturados/reflejados en el apartado.
 * CDP conserva el maestro; este VO es un snapshot inmutable.
 */
public record DatosContactoCliente(
        String nombreCompleto,
        String telefono,
        String correoElectronico
) {

    public DatosContactoCliente {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        nombreCompleto = nombreCompleto.strip();
        telefono = normalizarOpcional(telefono);
        correoElectronico = normalizarOpcional(correoElectronico);
        if (telefono == null && correoElectronico == null) {
            throw new IllegalArgumentException("Se requiere al menos teléfono o correo del cliente.");
        }
    }

    public static DatosContactoCliente de(final String nombreCompleto,
                                          final String telefono,
                                          final String correoElectronico) {
        return new DatosContactoCliente(nombreCompleto, telefono, correoElectronico);
    }

    public Optional<String> telefonoOpcional() {
        return Optional.ofNullable(telefono);
    }

    public Optional<String> correoElectronicoOpcional() {
        return Optional.ofNullable(correoElectronico);
    }

    private static String normalizarOpcional(final String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.strip();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatosContactoCliente that)) {
            return false;
        }
        return Objects.equals(nombreCompleto, that.nombreCompleto)
                && Objects.equals(telefono, that.telefono)
                && Objects.equals(correoElectronico, that.correoElectronico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreCompleto, telefono, correoElectronico);
    }
}
