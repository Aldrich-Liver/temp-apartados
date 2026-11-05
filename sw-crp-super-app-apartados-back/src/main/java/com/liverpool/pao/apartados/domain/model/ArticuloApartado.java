package com.liverpool.pao.apartados.domain.model;


import java.util.Objects;

/**
 * Línea de mercancía del apartado. Tiene identidad propia para operar por línea
 * (agregar/eliminar/ajustar) dentro del agregado.
 */
public final class ArticuloApartado {

    private final ArticuloApartadoId id;
    private final Sku sku;
    private Cantidad cantidad;
    private final Precio precio;
    private Importe importe;

    private ArticuloApartado(final ArticuloApartadoId id,
                             final Sku sku,
                             final Cantidad cantidad,
                             final Precio precio) {
        this.id = Objects.requireNonNull(id, "El id del artículo es obligatorio.");
        this.sku = Objects.requireNonNull(sku, "El SKU es obligatorio.");
        this.cantidad = Objects.requireNonNull(cantidad, "La cantidad es obligatoria.");
        this.precio = Objects.requireNonNull(precio, "El precio es obligatorio.");
        this.importe = precio.por(cantidad);
    }

    public static ArticuloApartado crear(final Sku sku, final Cantidad cantidad, final Precio precio) {
        return new ArticuloApartado(ArticuloApartadoId.nuevo(), sku, cantidad, precio);
    }

    public static ArticuloApartado de(final ArticuloApartadoId id,
                                               final Sku sku,
                                               final Cantidad cantidad,
                                               final Precio precio) {
        return new ArticuloApartado(id, sku, cantidad, precio);
    }

    public void actualizarCantidad(final Cantidad nuevaCantidad) {
        this.cantidad = Objects.requireNonNull(nuevaCantidad, "La cantidad es obligatoria.");
        this.importe = precio.por(this.cantidad);
    }

    public ArticuloApartadoId id() {
        return id;
    }

    public Sku sku() {
        return sku;
    }

    public Cantidad cantidad() {
        return cantidad;
    }

    public Precio precio() {
        return precio;
    }

    public Importe importe() {
        return importe;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticuloApartado that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
