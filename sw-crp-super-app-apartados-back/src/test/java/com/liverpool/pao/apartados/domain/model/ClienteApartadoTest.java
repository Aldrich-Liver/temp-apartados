package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteApartadoTest {

    private static final DatosContactoCliente CONTACTO =
            DatosContactoCliente.de("Ana Pérez", "5512345678", "ana@mail.com");

    @Test
    @DisplayName("Debe crear ClienteApartado con id de CDP y contacto")
    void deConClienteIdRetornaCliente() {
        // When
        var cliente = ClienteApartado.de("CDP-001", CONTACTO);

        // Then
        assertEquals("CDP-001", cliente.clienteId().orElseThrow());
        assertEquals(CONTACTO, cliente.contacto());
    }

    @Test
    @DisplayName("Debe crear ClienteApartado solo con contacto")
    void soloContactoRetornaClienteSinId() {
        // When
        var cliente = ClienteApartado.soloContacto(CONTACTO);

        // Then
        assertTrue(cliente.clienteId().isEmpty());
        assertEquals(CONTACTO, cliente.contacto());
    }

    @Test
    @DisplayName("Debe normalizar clienteId en blanco a vacío")
    void deConClienteIdEnBlancoOmiteId() {
        // When
        var cliente = ClienteApartado.de("   ", CONTACTO);

        // Then
        assertTrue(cliente.clienteId().isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el contacto es nulo")
    void deConContactoNuloLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () ->
                ClienteApartado.de("CDP-001", null));

        // Then
        assertEquals("Los datos de contacto del cliente son obligatorios.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Dos ClienteApartado con mismos datos deben ser iguales")
    void clientesConMismosDatosSonIguales() {
        // Given
        var a = ClienteApartado.de("CDP-001", CONTACTO);
        var b = ClienteApartado.de("CDP-001", CONTACTO);

        // When
        boolean iguales = a.equals(b);

        // Then
        assertTrue(iguales);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
