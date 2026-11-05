package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatosContactoClienteTest {

    @Test
    @DisplayName("Debe crear DatosContactoCliente con nombre y teléfono")
    void creadoConTelefonoRetornaDatos() {
        // When
        var contacto = DatosContactoCliente.de("Ana Pérez", "5512345678", null);

        // Then
        assertEquals("Ana Pérez", contacto.nombreCompleto());
        assertTrue(contacto.telefonoOpcional().isPresent());
        assertEquals("5512345678", contacto.telefonoOpcional().orElseThrow());
        assertTrue(contacto.correoElectronicoOpcional().isEmpty());
    }

    @Test
    @DisplayName("Debe crear DatosContactoCliente con nombre y correo")
    void creadoConCorreoRetornaDatos() {
        // When
        var contacto = DatosContactoCliente.de("Ana Pérez", null, "ana@mail.com");

        // Then
        assertEquals("ana@mail.com", contacto.correoElectronicoOpcional().orElseThrow());
        assertTrue(contacto.telefonoOpcional().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo, vacío o en blanco")
    void creadoConNombreInvalidoLanzaExcepcion(final String nombre) {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                DatosContactoCliente.de(nombre, "5512345678", null));

        // Then
        assertEquals("El nombre del cliente es obligatorio.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay teléfono ni correo")
    void creadoSinTelefonoNiCorreoLanzaExcepcion() {
        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                DatosContactoCliente.de("Ana Pérez", null, "  "));

        // Then
        assertEquals("Se requiere al menos teléfono o correo del cliente.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Dos DatosContactoCliente con los mismos valores deben ser iguales")
    void datosConMismosValoresSonIguales() {
        // Given
        var a = DatosContactoCliente.de("Ana Pérez", "5512345678", "ana@mail.com");
        var b = DatosContactoCliente.de("Ana Pérez", "5512345678", "ana@mail.com");

        // When
        boolean iguales = a.equals(b);

        // Then
        assertTrue(iguales);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
