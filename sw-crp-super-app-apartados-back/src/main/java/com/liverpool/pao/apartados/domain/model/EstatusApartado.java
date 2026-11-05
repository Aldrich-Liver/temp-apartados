package com.liverpool.pao.apartados.domain.model;

/**
 * Estado funcional del apartado en PAO.
 * No incluye NUEVO/ACTIVO (estados técnicos de SAP); esos se mapean en integración.
 */
public enum EstatusApartado {
    ALMACENADO,
    ENTREGADO,
    CANCELADO
}
