package com.liverpool.pao.apartados.domain.model;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApartadoTest {

    private static final LocalDateTime AHORA = LocalDateTime.now().withNano(0);
    private static final LocalDateTime INICIO = AHORA.toLocalDate().minusDays(9).atStartOfDay();
    private static final LocalDateTime FIN = AHORA.toLocalDate().plusDays(20).atTime(23, 59);
    private static final PeriodoVigencia VIGENCIA = PeriodoVigencia.de(INICIO, FIN);
    private static final ClienteApartado CLIENTE = ClienteApartado.de(
            "CDP-001",
            DatosContactoCliente.de("Ana Pérez", "5512345678", "ana@mail.com"));
    private static final EmpleadoId EMPLEADO = EmpleadoId.de("16618035");
    private static final TiendaId TIENDA = TiendaId.de("0042");
    private static final MonederoApartado MONEDERO = MonederoApartado.de("MON-123");
    private static final Sku SKU_A = Sku.de("1080618685");
    private static final Sku SKU_B = Sku.de("1153466102");
    private static final Precio PRECIO = Precio.de("100.00");

    private Apartado apartadoNuevo() {
        return Apartado.crear(CLIENTE, EMPLEADO, TIENDA, MONEDERO, VIGENCIA, AHORA);
    }

    private Apartado apartadoConArticulo() {
        final Apartado apartado = apartadoNuevo();
        apartado.agregarArticulo(SKU_A, Cantidad.de(2), PRECIO, AHORA);
        return apartado;
    }

    private Apartado apartadoAlmacenado() {
        final Apartado apartado = apartadoConArticulo();
        apartado.almacenar(FolioApartado.de("FOL-001"), AHORA);
        return apartado;
    }

    // --- crear ---

    @Test
    @DisplayName("Debe crear Apartado en ALMACENADO sin folio y con version 0")
    void creadoConValoresValidosRetornaApartado() {
        // When
        var apartado = apartadoNuevo();

        // Then
        assertNotNull(apartado.id());
        assertTrue(apartado.folio().isEmpty());
        assertEquals(CLIENTE, apartado.cliente());
        assertEquals(EMPLEADO, apartado.empleado());
        assertEquals(TIENDA, apartado.tienda());
        assertEquals(MONEDERO, apartado.monedero().orElseThrow());
        assertEquals(VIGENCIA, apartado.vigencia());
        assertEquals(EstatusApartado.ALMACENADO, apartado.estatus());
        assertEquals(AHORA, apartado.creadoEn());
        assertEquals(0, apartado.version());
        assertTrue(apartado.mercancias().isEmpty());
        assertEquals(new BigDecimal("0.00"), apartado.importeTotal().value());
    }

    @Test
    @DisplayName("Dos llamadas a crear deben generar ids distintos")
    void crearGeneraIdsUnicos() {
        // When
        var a = apartadoNuevo();
        var b = apartadoNuevo();

        // Then
        assertNotEquals(a.id(), b.id());
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la fecha de creación es nula")
    void creadoConFechaNulaLanzaExcepcion() {
        // When
        var excepcion = assertThrows(NullPointerException.class, () ->
                Apartado.crear(CLIENTE, EMPLEADO, TIENDA, MONEDERO, VIGENCIA, null));

        // Then
        assertEquals("La fecha/hora de creación es obligatoria.", excepcion.getMessage());
    }

    // --- fromMongoDb ---

    @Test
    @DisplayName("fromMongoDb debe reconstruir el apartado con los valores persistidos")
    void fromMongoDbReconstruyeApartado() {
        // Given
        var id = new ObjectId();
        var folio = FolioApartado.de("FOL-9");
        var articulo = ArticuloApartado.de(
                ArticuloApartadoId.de("ART-1"), SKU_A, Cantidad.de(1), PRECIO);

        // When
        var apartado = Apartado.fromMongoDb(
                id, folio, CLIENTE, EMPLEADO, TIENDA, List.of(articulo), MONEDERO, VIGENCIA,
                EstatusApartado.CANCELADO, AHORA, AHORA, null, AHORA, 3);

        // Then
        assertEquals(id, apartado.id());
        assertEquals(folio, apartado.folio().orElseThrow());
        assertEquals(EstatusApartado.CANCELADO, apartado.estatus());
        assertEquals(1, apartado.mercancias().size());
        assertEquals(3, apartado.version());
        assertEquals(AHORA, apartado.canceladoEn().orElseThrow());
    }

    // --- agregarArticulo ---

    @Test
    @DisplayName("Debe agregar un artículo nuevo al apartado")
    void agregarArticuloNuevoLoIncluyeEnMercancias() {
        // Given
        var apartado = apartadoNuevo();

        // When
        var articulo = apartado.agregarArticulo(SKU_A, Cantidad.de(2), PRECIO, AHORA);

        // Then
        assertEquals(1, apartado.mercancias().size());
        assertEquals(articulo, apartado.mercancias().getFirst());
        assertEquals(new BigDecimal("200.00"), apartado.importeTotal().value());
        assertEquals(1, apartado.version());
    }

    @Test
    @DisplayName("Debe acumular cantidad cuando se agrega el mismo SKU")
    void agregarArticuloExistenteSumaCantidad() {
        // Given
        var apartado = apartadoConArticulo();

        // When
        apartado.agregarArticulo(SKU_A, Cantidad.de(3), PRECIO, AHORA);

        // Then
        assertEquals(1, apartado.mercancias().size());
        assertEquals(5, apartado.mercancias().getFirst().cantidad().value());
        assertEquals(new BigDecimal("500.00"), apartado.importeTotal().value());
    }

    // --- eliminarArticulo ---

    @Test
    @DisplayName("Debe eliminar un artículo cuando queda al menos uno")
    void eliminarArticuloConservandoOtro() {
        // Given
        var apartado = apartadoConArticulo();
        var segundo = apartado.agregarArticulo(SKU_B, Cantidad.de(1), PRECIO, AHORA);
        var idAEliminar = segundo.id();

        // When
        apartado.eliminarArticulo(idAEliminar, AHORA);

        // Then
        assertEquals(1, apartado.mercancias().size());
        assertEquals(SKU_A, apartado.mercancias().getFirst().sku());
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar el único artículo")
    void eliminarUnicoArticuloLanzaExcepcion() {
        // Given
        var apartado = apartadoConArticulo();
        var id = apartado.mercancias().getFirst().id();

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.eliminarArticulo(id, AHORA));

        // Then
        assertEquals("El apartado debe conservar al menos un artículo.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un artículo inexistente")
    void eliminarArticuloInexistenteLanzaExcepcion() {
        // Given
        var apartado = apartadoConArticulo();
        var idInexistente = ArticuloApartadoId.de("NO-EXISTE");

        // When
        var excepcion = assertThrows(IllegalArgumentException.class, () ->
                apartado.eliminarArticulo(idInexistente, AHORA));

        // Then
        assertTrue(excepcion.getMessage().contains("NO-EXISTE"));
    }

    // --- almacenar ---

    @Test
    @DisplayName("Debe almacenar asignando folio")
    void almacenarAsignaFolioYMantieneAlmacenado() {
        // Given
        var apartado = apartadoConArticulo();
        var folio = FolioApartado.de("FOL-001");

        // When
        apartado.almacenar(folio, AHORA);

        // Then
        assertEquals(folio, apartado.folio().orElseThrow());
        assertEquals(EstatusApartado.ALMACENADO, apartado.estatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción al almacenar sin mercancías")
    void almacenarSinMercanciasLanzaExcepcion() {
        // Given
        var apartado = apartadoNuevo();

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.almacenar(FolioApartado.de("FOL-001"), AHORA));

        // Then
        assertEquals("No se puede almacenar un apartado sin mercancías.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al almacenar un apartado cancelado")
    void almacenarCanceladoLanzaExcepcion() {
        // Given
        var apartado = apartadoAlmacenado();
        apartado.cancelar(AHORA);

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.almacenar(FolioApartado.de("FOL-002"), AHORA));

        // Then
        assertTrue(excepcion.getMessage().contains("CANCELADO"));
    }

    // --- cancelar / entregar ---

    @Test
    @DisplayName("Debe cancelar un apartado almacenado")
    void cancelarApartadoAlmacenado() {
        // Given
        var apartado = apartadoAlmacenado();

        // When
        apartado.cancelar(AHORA);

        // Then
        assertEquals(EstatusApartado.CANCELADO, apartado.estatus());
        assertEquals(AHORA, apartado.canceladoEn().orElseThrow());
        assertTrue(apartado.entregadoEn().isEmpty());
    }

    @Test
    @DisplayName("Debe entregar un apartado almacenado vigente")
    void entregarApartadoAlmacenado() {
        // Given
        var apartado = apartadoAlmacenado();

        // When
        apartado.entregar(AHORA);

        // Then
        assertEquals(EstatusApartado.ENTREGADO, apartado.estatus());
        assertEquals(AHORA, apartado.entregadoEn().orElseThrow());
    }

    @Test
    @DisplayName("Debe lanzar excepción al entregar un apartado vencido")
    void entregarApartadoVencidoLanzaExcepcion() {
        // Given
        var apartado = apartadoAlmacenado();
        var despuesDeVigencia = FIN.plusDays(1);

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.entregar(despuesDeVigencia));

        // Then
        assertEquals("No se puede entregar un apartado vencido.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar un apartado ya entregado")
    void cancelarEntregadoLanzaExcepcion() {
        // Given
        var apartado = apartadoAlmacenado();
        apartado.entregar(AHORA);

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.cancelar(AHORA));

        // Then
        assertTrue(excepcion.getMessage().contains("cancelar"));
        assertTrue(excepcion.getMessage().contains("ENTREGADO"));
    }

    // --- validarEdicion / marcarVencido ---

    @Test
    @DisplayName("validarEdicion no debe fallar en apartado almacenado vigente")
    void validarEdicionEnAlmacenadoVigenteNoLanza() {
        // Given
        var apartado = apartadoAlmacenado();

        // When / Then
        apartado.validarEdicion(AHORA);
    }

    @Test
    @DisplayName("Debe lanzar excepción al editar un apartado vencido")
    void validarEdicionVencidoLanzaExcepcion() {
        // Given
        var apartado = apartadoAlmacenado();

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.validarEdicion(FIN.plusDays(1)));

        // Then
        assertEquals("No se puede editar un apartado vencido.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe marcar vencido cancelando el apartado almacenado")
    void marcarVencidoCancelaApartado() {
        // Given
        var apartado = apartadoAlmacenado();
        var despuesDeVigencia = FIN.plusDays(1);

        // When
        apartado.marcarVencido(despuesDeVigencia);

        // Then
        assertEquals(EstatusApartado.CANCELADO, apartado.estatus());
        assertEquals(despuesDeVigencia, apartado.canceladoEn().orElseThrow());
    }

    @Test
    @DisplayName("Debe lanzar excepción al marcar vencido dentro de la vigencia")
    void marcarVencidoDentroDeVigenciaLanzaExcepcion() {
        // Given
        var apartado = apartadoAlmacenado();

        // When
        var excepcion = assertThrows(IllegalStateException.class, () ->
                apartado.marcarVencido(AHORA));

        // Then
        assertEquals("El apartado aún está dentro de su vigencia.", excepcion.getMessage());
    }

    // --- actualizarCliente / monedero ---

    @Test
    @DisplayName("Debe actualizar el cliente del apartado")
    void actualizarClienteCambiaSnapshot() {
        // Given
        var apartado = apartadoAlmacenado();
        var nuevoCliente = ClienteApartado.de(
                "CDP-002",
                DatosContactoCliente.de("Luis Gómez", "5599999999", null));

        // When
        apartado.actualizarCliente(nuevoCliente, AHORA);

        // Then
        assertEquals(nuevoCliente, apartado.cliente());
    }

    @Test
    @DisplayName("Debe actualizar el monedero del apartado")
    void actualizarMonederoCambiaReferencia() {
        // Given
        var apartado = apartadoAlmacenado();
        var nuevoMonedero = MonederoApartado.de("MON-999");

        // When
        apartado.actualizarMonedero(nuevoMonedero, AHORA);

        // Then
        assertEquals(nuevoMonedero, apartado.monedero().orElseThrow());
    }

    @Test
    @DisplayName("Debe permitir limpiar monedero con null")
    void actualizarMonederoNuloLoLimpia() {
        // Given
        var apartado = apartadoAlmacenado();

        // When
        apartado.actualizarMonedero(null, AHORA);

        // Then
        assertTrue(apartado.monedero().isEmpty());
    }

    @Test
    @DisplayName("mercancias debe ser una vista no modificable")
    void mercanciasEsInmodificable() {
        // Given
        var apartado = apartadoConArticulo();

        // When
        var excepcion = assertThrows(UnsupportedOperationException.class, () ->
                apartado.mercancias().clear());

        // Then
        assertNotNull(excepcion);
        assertFalse(apartado.mercancias().isEmpty());
    }
}
