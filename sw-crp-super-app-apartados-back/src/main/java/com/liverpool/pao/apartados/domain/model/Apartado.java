package com.liverpool.pao.apartados.domain.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.annotation.Nonnull;

/**
 * Aggregate Root del ciclo de vida del apartado. Protege las invariantes del folio:
 * cambios de estatus y mercancías solo a través de sus comportamientos.
 */
@MongoEntity(collection = "apartado")
public final class Apartado extends PanacheMongoEntityBase {

    @BsonId
    @Nonnull
    private final ObjectId id;
    private FolioApartado folio;
    private ClienteApartado cliente;
    private final EmpleadoId empleado;
    private final TiendaId tienda;
    private final List<ArticuloApartado> mercancias;
    private MonederoApartado monedero;
    private PeriodoVigencia vigencia;
    private EstatusApartado estatus;
    private final LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime entregadoEn;
    private LocalDateTime canceladoEn;
    private int version;

    private Apartado(final ObjectId id,
                     final FolioApartado folio,
                     final ClienteApartado cliente,
                     final EmpleadoId empleado,
                     final TiendaId tienda,
                     final List<ArticuloApartado> mercancias,
                     final MonederoApartado monedero,
                     final PeriodoVigencia vigencia,
                     final EstatusApartado estatus,
                     final LocalDateTime creadoEn,
                     final LocalDateTime actualizadoEn,
                     final LocalDateTime entregadoEn,
                     final LocalDateTime canceladoEn,
                     final int version) {
        this.id = Objects.requireNonNull(id, "El id del apartado es obligatorio.");
        this.folio = folio;
        this.cliente = Objects.requireNonNull(cliente, "El cliente del apartado es obligatorio.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado creador es obligatorio.");
        this.tienda = Objects.requireNonNull(tienda, "La tienda de origen es obligatoria.");
        this.mercancias = new ArrayList<>(Objects.requireNonNull(mercancias, "La lista de mercancías es obligatoria."));
        this.monedero = monedero;
        this.vigencia = Objects.requireNonNull(vigencia, "La vigencia es obligatoria.");
        this.estatus = Objects.requireNonNull(estatus, "El estatus es obligatorio.");
        this.creadoEn = Objects.requireNonNull(creadoEn, "La fecha de creación es obligatoria.");
        this.actualizadoEn = actualizadoEn;
        this.entregadoEn = entregadoEn;
        this.canceladoEn = canceladoEn;
        this.version = version;
    }

    /**
     * Crea un apartado en construcción (aún sin folio SAP). El estatus funcional PAO
     * se confirma con {@link #almacenar(FolioApartado, LocalDateTime)}.
     */
    public static Apartado crear(final ClienteApartado cliente,
                                 final EmpleadoId empleado,
                                 final TiendaId tienda,
                                 final MonederoApartado monedero,
                                 final PeriodoVigencia vigencia,
                                 final LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha/hora de creación es obligatoria.");
        return new Apartado(
                new ObjectId(),
                null,
                cliente,
                empleado,
                tienda,
                new ArrayList<>(),
                monedero,
                vigencia,
                EstatusApartado.ALMACENADO,
                ahora,
                ahora,
                null,
                null,
                0
        );
    }

    /**
     * Reconstrucción desde Mongo.
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    @BsonCreator
    public static Apartado fromMongoDb(@BsonId @Nonnull final ObjectId id,
                                       @BsonProperty("folio") final FolioApartado folio,
                                       @BsonProperty("cliente") final ClienteApartado cliente,
                                       @BsonProperty("empleado") final EmpleadoId empleado,
                                       @BsonProperty("tienda") final TiendaId tienda,
                                       @BsonProperty("mercancias") final List<ArticuloApartado> mercancias,
                                       @BsonProperty("monedero") final MonederoApartado monedero,
                                       @BsonProperty("vigencia") final PeriodoVigencia vigencia,
                                       @BsonProperty("estatus") final EstatusApartado estatus,
                                       @BsonProperty("creadoEn") final LocalDateTime creadoEn,
                                       @BsonProperty("actualizadoEn") final LocalDateTime actualizadoEn,
                                       @BsonProperty("entregadoEn") final LocalDateTime entregadoEn,
                                       @BsonProperty("canceladoEn") final LocalDateTime canceladoEn,
                                       @BsonProperty("version") final int version) {
        return new Apartado(
                id, folio, cliente, empleado, tienda, mercancias, monedero, vigencia, estatus,
                creadoEn, actualizadoEn, entregadoEn, canceladoEn, version
        );
    }

    public ArticuloApartado agregarArticulo(final Sku sku,
                                            final Cantidad cantidad,
                                            final Precio precio,
                                            final LocalDateTime ahora) {
        validarEdicion(ahora);
        Objects.requireNonNull(sku, "El SKU es obligatorio.");
        final Optional<ArticuloApartado> existente = mercancias.stream()
                .filter(a -> a.sku().equals(sku))
                .findFirst();
        if (existente.isPresent()) {
            final ArticuloApartado articulo = existente.get();
            articulo.actualizarCantidad(articulo.cantidad().sumar(cantidad));
            tocar(ahora);
            return articulo;
        }
        final ArticuloApartado nuevo = ArticuloApartado.crear(sku, cantidad, precio);
        mercancias.add(nuevo);
        tocar(ahora);
        return nuevo;
    }

    public void eliminarArticulo(final ArticuloApartadoId articuloId, final LocalDateTime ahora) {
        validarEdicion(ahora);
        Objects.requireNonNull(articuloId, "El id del artículo es obligatorio.");
        final boolean eliminado = mercancias.removeIf(a -> a.id().equals(articuloId));
        if (!eliminado) {
            throw new IllegalArgumentException("No existe el artículo '" + articuloId + "' en el apartado.");
        }
        if (mercancias.isEmpty()) {
            throw new IllegalStateException("El apartado debe conservar al menos un artículo.");
        }
        tocar(ahora);
    }

    /**
     * Confirma el almacenamiento funcional: asigna folio y deja el apartado en {@code ALMACENADO}.
     */
    public void almacenar(final FolioApartado folioAsignado, final LocalDateTime ahora) {
        Objects.requireNonNull(folioAsignado, "El folio es obligatorio para almacenar.");
        Objects.requireNonNull(ahora, "La fecha/hora es obligatoria.");
        if (mercancias.isEmpty()) {
            throw new IllegalStateException("No se puede almacenar un apartado sin mercancías.");
        }
        if (estatus == EstatusApartado.ENTREGADO || estatus == EstatusApartado.CANCELADO) {
            throw new IllegalStateException("No se puede almacenar un apartado en estatus " + estatus + ".");
        }
        this.folio = folioAsignado;
        this.estatus = EstatusApartado.ALMACENADO;
        tocar(ahora);
    }

    public void cancelar(final LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha/hora es obligatoria.");
        asegurarAlmacenado("cancelar");
        this.estatus = EstatusApartado.CANCELADO;
        this.canceladoEn = ahora;
        tocar(ahora);
    }

    public void entregar(final LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha/hora es obligatoria.");
        asegurarAlmacenado("entregar");
        if (vigencia.estaVencidoEn(ahora)) {
            throw new IllegalStateException("No se puede entregar un apartado vencido.");
        }
        this.estatus = EstatusApartado.ENTREGADO;
        this.entregadoEn = ahora;
        tocar(ahora);
    }

    /**
     * Valida que el apartado admita edición de mercancías/datos en el momento indicado.
     * Regla base: solo {@code ALMACENADO} y dentro de vigencia.
     * (La restricción del día de liquidación se afinará con negocio.)
     */
    public void validarEdicion(final LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha/hora es obligatoria.");
        asegurarAlmacenado("editar");
        if (vigencia.estaVencidoEn(ahora)) {
            throw new IllegalStateException("No se puede editar un apartado vencido.");
        }
    }

    /**
     * Marca el apartado como vencido cancelándolo cuando la vigencia ya expiró.
     */
    public void marcarVencido(final LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha/hora es obligatoria.");
        if (!vigencia.estaVencidoEn(ahora)) {
            throw new IllegalStateException("El apartado aún está dentro de su vigencia.");
        }
        if (estatus != EstatusApartado.ALMACENADO) {
            throw new IllegalStateException(
                    "Solo un apartado ALMACENADO puede marcarse vencido; actual=" + estatus + ".");
        }
        this.estatus = EstatusApartado.CANCELADO;
        this.canceladoEn = ahora;
        tocar(ahora);
    }

    public void actualizarCliente(final ClienteApartado nuevoCliente, final LocalDateTime ahora) {
        validarEdicion(ahora);
        this.cliente = Objects.requireNonNull(nuevoCliente, "El cliente es obligatorio.");
        tocar(ahora);
    }

    public void actualizarMonedero(final MonederoApartado nuevoMonedero, final LocalDateTime ahora) {
        validarEdicion(ahora);
        this.monedero = nuevoMonedero;
        tocar(ahora);
    }

    public Importe importeTotal() {
        return mercancias.stream()
                .map(ArticuloApartado::importe)
                .reduce(Importe.cero(), Importe::sumar);
    }

    private void asegurarAlmacenado(final String operacion) {
        if (estatus != EstatusApartado.ALMACENADO) {
            throw new IllegalStateException(
                    "No se puede " + operacion + " un apartado en estatus " + estatus + ".");
        }
    }

    private void tocar(final LocalDateTime ahora) {
        this.actualizadoEn = ahora;
        this.version++;
    }

    public ObjectId id() {
        return id;
    }

    public Optional<FolioApartado> folio() {
        return Optional.ofNullable(folio);
    }

    public ClienteApartado cliente() {
        return cliente;
    }

    public EmpleadoId empleado() {
        return empleado;
    }

    public TiendaId tienda() {
        return tienda;
    }

    public List<ArticuloApartado> mercancias() {
        return Collections.unmodifiableList(mercancias);
    }

    public Optional<MonederoApartado> monedero() {
        return Optional.ofNullable(monedero);
    }

    public PeriodoVigencia vigencia() {
        return vigencia;
    }

    public EstatusApartado estatus() {
        return estatus;
    }

    public LocalDateTime creadoEn() {
        return creadoEn;
    }

    public Optional<LocalDateTime> actualizadoEn() {
        return Optional.ofNullable(actualizadoEn);
    }

    public Optional<LocalDateTime> entregadoEn() {
        return Optional.ofNullable(entregadoEn);
    }

    public Optional<LocalDateTime> canceladoEn() {
        return Optional.ofNullable(canceladoEn);
    }

    public int version() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Apartado that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
