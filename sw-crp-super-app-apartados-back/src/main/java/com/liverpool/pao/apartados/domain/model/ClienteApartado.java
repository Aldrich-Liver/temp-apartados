package com.liverpool.pao.apartados.domain.model;


import java.util.Objects;
import java.util.Optional;

/**
 * Snapshot de cliente asociado al apartado. CDP conserva el maestro;
 * aquí solo se retienen los datos relevantes al folio.
 */
public final class ClienteApartado {

    private final String clienteId;
    private final DatosContactoCliente contacto;

    private ClienteApartado(final String clienteId, final DatosContactoCliente contacto) {
        this.contacto = Objects.requireNonNull(contacto, "Los datos de contacto del cliente son obligatorios.");
        this.clienteId = normalizarClienteId(clienteId);
    }

    public static ClienteApartado de(final String clienteId, final DatosContactoCliente contacto) {
        return new ClienteApartado(clienteId, contacto);
    }

    public static ClienteApartado soloContacto(final DatosContactoCliente contacto) {
        return new ClienteApartado(null, contacto);
    }

    public Optional<String> clienteId() {
        return Optional.ofNullable(clienteId);
    }

    public DatosContactoCliente contacto() {
        return contacto;
    }

    private static String normalizarClienteId(final String clienteId) {
        if (clienteId == null || clienteId.isBlank()) {
            return null;
        }
        return clienteId.strip();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClienteApartado that)) {
            return false;
        }
        return Objects.equals(clienteId, that.clienteId)
                && Objects.equals(contacto, that.contacto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId, contacto);
    }
}
