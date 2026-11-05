package com.liverpool.pao.apartados.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Ventana de vigencia del apartado (inicio inclusive, fin inclusive para evaluación de vencimiento).
 */
public record PeriodoVigencia(LocalDateTime inicio, LocalDateTime fin) {

    public PeriodoVigencia {
        Objects.requireNonNull(inicio, "La fecha de inicio de vigencia es obligatoria.");
        Objects.requireNonNull(fin, "La fecha de fin de vigencia es obligatoria.");
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La vigencia no puede terminar antes de iniciar.");
        }
    }

    public static PeriodoVigencia de(final LocalDateTime inicio, final LocalDateTime fin) {
        return new PeriodoVigencia(inicio, fin);
    }

    public boolean contiene(final LocalDateTime momento) {
        Objects.requireNonNull(momento, "El momento a evaluar es obligatorio.");
        return !momento.isBefore(inicio) && !momento.isAfter(fin);
    }

    public boolean estaVencidoEn(final LocalDateTime momento) {
        Objects.requireNonNull(momento, "El momento a evaluar es obligatorio.");
        return momento.isAfter(fin);
    }
}
