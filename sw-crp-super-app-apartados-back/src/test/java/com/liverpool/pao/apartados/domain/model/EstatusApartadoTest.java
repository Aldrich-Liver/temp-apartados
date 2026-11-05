package com.liverpool.pao.apartados.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstatusApartadoTest {

    @Test
    @DisplayName("Debe exponer únicamente los estados funcionales PAO")
    void valoresFuncionalesPao() {
        // When
        var valores = EstatusApartado.values();

        // Then
        assertEquals(3, valores.length);
        assertEquals(EstatusApartado.ALMACENADO, valores[0]);
        assertEquals(EstatusApartado.ENTREGADO, valores[1]);
        assertEquals(EstatusApartado.CANCELADO, valores[2]);
    }
}
