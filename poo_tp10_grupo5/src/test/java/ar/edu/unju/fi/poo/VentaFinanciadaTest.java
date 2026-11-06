package ar.edu.unju.fi.poo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.VentaFinanciadaInvalidaException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.EstadoCuota;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import ar.edu.unju.fi.poo.service.ProductoService;
import ar.edu.unju.fi.poo.service.VentaFinanciadaService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class VentaFinanciadaTest {

    @Autowired
    private VentaFinanciadaService ventaFinanciadaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Cliente cliente;
    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        Cliente nuevoCliente = new Cliente("88990011", "Ramon", "Gomez", null, "3884111222", "ramon@test.com", "Av. Bolivia 123", TipoCliente.PARTICULAR);
        cliente = clienteService.guardarCliente(nuevoCliente);

        categoria = new Categoria();
        categoria.setNombre("Tecnologia");
        categoria = categoriaRepository.save(categoria);

        Producto nuevoProducto = new Producto("Smart TV 55", "4K UHD", "Samsung", 10, 100000.0, categoria);
        producto = productoService.guardarProducto(nuevoProducto);
    }

    @Test
    @DisplayName("Debe registrar una venta financiada correctamente con recargo y 6 cuotas consecutivas")
    void testRegistrarVentaFinanciadaExitoso() {
        DetalleVenta detalle = new DetalleVenta(1, producto.getPrecioBase(), producto);

        VentaFinanciada venta = new VentaFinanciada("VF-001", LocalDate.now(), cliente, 10.0); // 10% de recargo
        venta.agregarDetalle(detalle);

        VentaFinanciada guardada = ventaFinanciadaService.registrarVentaFinanciada(venta);

        assertNotNull(guardada.getIdVenta());
        assertEquals(100000.0, guardada.getSubtotal());
        assertEquals(10000.0, guardada.aplicarRecargo());
        assertEquals(110000.0, guardada.getTotal());

        // Verificacion de las 6 cuotas fijas generadas en estado ADEUDADA
        List<Cuota> cuotas = guardada.getCuotas();
        assertEquals(6, cuotas.size());
        assertEquals(110000.0 / 6, cuotas.get(0).getImporte());
        assertEquals(EstadoCuota.ADEUDADA, cuotas.get(0).getEstado());

        // Verificacion de actualización de stock
        Producto productoActualizado = productoService.buscarPorId(producto.getIdProducto());
        assertEquals(9, productoActualizado.getStock());
    }

    @Test
    @DisplayName("Debe consultar el estado de cuenta de cuotas pendientes de un cliente")
    void testConsultarEstadoCuentaCuotasPendientes() {
        DetalleVenta detalle = new DetalleVenta(1, producto.getPrecioBase(), producto);
        VentaFinanciada venta = new VentaFinanciada("VF-002", LocalDate.now(), cliente, 15.0);
        venta.agregarDetalle(detalle);
        ventaFinanciadaService.registrarVentaFinanciada(venta);

        List<Cuota> cuotasPendientes = ventaFinanciadaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());

        assertFalse(cuotasPendientes.isEmpty());
        assertEquals(6, cuotasPendientes.size());
        assertTrue(cuotasPendientes.stream().allMatch(c -> c.getEstado() == EstadoCuota.ADEUDADA));
    }

    @Test
    @DisplayName("Debe consultar cuotas pendientes por numero de identificación (DNI/CUIT)")
    void testConsultarEstadoCuentaPorIdentificacion() {
        DetalleVenta detalle = new DetalleVenta(1, producto.getPrecioBase(), producto);
        VentaFinanciada venta = new VentaFinanciada("VF-003", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);
        ventaFinanciadaService.registrarVentaFinanciada(venta);

        List<Cuota> cuotasPendientes = ventaFinanciadaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(cliente.getNumeroIdentificacion());

        assertEquals(6, cuotasPendientes.size());
    }

    @Test
    @DisplayName("No debe registrar una venta financiada nula")
    void testRegistrarVentaFinanciadaNula() {
        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaFinanciadaService.registrarVentaFinanciada(null);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta financiada sin detalles de venta")
    void testRegistrarVentaFinanciadaSinDetalles() {
        VentaFinanciada venta = new VentaFinanciada("VF-004", LocalDate.now(), cliente, 10.0);

        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaFinanciadaService.registrarVentaFinanciada(venta);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta con porcentaje de recargo negativo")
    void testRegistrarVentaConRecargoNegativo() {
        DetalleVenta detalle = new DetalleVenta(1, producto.getPrecioBase(), producto);
        VentaFinanciada venta = new VentaFinanciada("VF-005", LocalDate.now(), cliente, -5.0);
        venta.agregarDetalle(detalle);

        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaFinanciadaService.registrarVentaFinanciada(venta);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta financiada si el stock es insuficiente")
    void testRegistrarVentaFinanciadaStockInsuficiente() {
        DetalleVenta detalle = new DetalleVenta(15, producto.getPrecioBase(), producto); // solo hay 10
        VentaFinanciada venta = new VentaFinanciada("VF-006", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);

        assertThrows(StockInsuficienteException.class, () -> {
            ventaFinanciadaService.registrarVentaFinanciada(venta);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta con un producto inexistente")
    void testRegistrarVentaProductoInexistente() {
        Producto productoInexistente = new Producto();
        productoInexistente.setIdProducto(99999);
        DetalleVenta detalle = new DetalleVenta(1, 1000.0, productoInexistente);

        VentaFinanciada venta = new VentaFinanciada("VF-007", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);

        assertThrows(ProductoNoEncontradoException.class, () -> {
            ventaFinanciadaService.registrarVentaFinanciada(venta);
        });
    }
}