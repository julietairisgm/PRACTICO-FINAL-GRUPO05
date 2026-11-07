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

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.dto.ProductoDto;
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
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import ar.edu.unju.fi.poo.service.ProductoService;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class VentaFinanciadaTest {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository; //PROVISORIO MIENTRAS SE TRABAJA CON DTOS

    private Cliente cliente;
    private Categoria categoria;
    private ProductoDto producto;
    private Producto productoEntidad;

    @BeforeEach
    void setUp() {
        Cliente nuevoCliente = new Cliente("88990011", "Ramon", "Gomez", null, "3884111222", "ramon@test.com", "Av. Bolivia 123", TipoCliente.PARTICULAR);
        cliente = clienteService.guardarCliente(nuevoCliente);

        categoria = new Categoria();
        categoria.setNombre("Tecnologia");
        categoria = categoriaRepository.save(categoria);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        ProductoDto nuevoProducto = new ProductoDto();
        nuevoProducto.setNombre("Smart TV 55");
        nuevoProducto.setDescripcion("4K UHD");
        nuevoProducto.setMarca("Samsung");
        nuevoProducto.setStock(10);
        nuevoProducto.setPrecioBase(100000.0);
        nuevoProducto.setCategoriaDto(categoriaDto);

        producto = productoService.guardarProducto(nuevoProducto);

        productoEntidad = productoRepository.findById(producto.getIdProducto())
                .orElseThrow();
    }

    @Test
    @DisplayName("Debe registrar una venta financiada correctamente con recargo y 6 cuotas consecutivas")
    void testRegistrarVentaFinanciadaExitoso() {
        DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);

        VentaFinanciada venta = new VentaFinanciada("VF-001", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);

        VentaFinanciada guardada = ventaService.registrarVentaFinanciada(venta);

        assertNotNull(guardada.getIdVenta());
        assertEquals(100000.0, guardada.getSubtotal());
        assertEquals(10000.0, guardada.aplicarRecargo());
        assertEquals(110000.0, guardada.getTotal());

        List<Cuota> cuotas = guardada.getCuotas();
        assertEquals(6, cuotas.size());
        assertEquals(110000.0 / 6, cuotas.get(0).getImporte());
        assertEquals(EstadoCuota.ADEUDADA, cuotas.get(0).getEstado());

        ProductoDto productoActualizado = productoService.buscarPorId(producto.getIdProducto());
        assertEquals(9, productoActualizado.getStock());
    }

    @Test
    @DisplayName("Debe consultar el estado de cuenta de cuotas pendientes de un cliente")
    void testConsultarEstadoCuentaCuotasPendientes() {
        DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);
        VentaFinanciada venta = new VentaFinanciada("VF-002", LocalDate.now(), cliente, 15.0);
        venta.agregarDetalle(detalle);
        ventaService.registrarVentaFinanciada(venta);

        List<Cuota> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());

        assertFalse(cuotasPendientes.isEmpty());
        assertEquals(6, cuotasPendientes.size());
        assertTrue(cuotasPendientes.stream().allMatch(c -> c.getEstado() == EstadoCuota.ADEUDADA));
    }

    @Test
    @DisplayName("Debe consultar cuotas pendientes por número de identificación (DNI/CUIT)")
    void testConsultarEstadoCuentaPorIdentificacion() {
        DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);
        VentaFinanciada venta = new VentaFinanciada("VF-003", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);
        ventaService.registrarVentaFinanciada(venta);

        List<Cuota> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(cliente.getNumeroIdentificacion());

        assertEquals(6, cuotasPendientes.size());
    }

    @Test
    @DisplayName("No debe registrar una venta financiada nula")
    void testRegistrarVentaFinanciadaNula() {
        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaService.registrarVentaFinanciada(null);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta financiada sin detalles de venta")
    void testRegistrarVentaFinanciadaSinDetalles() {
        VentaFinanciada venta = new VentaFinanciada("VF-004", LocalDate.now(), cliente, 10.0);

        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaService.registrarVentaFinanciada(venta);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta con porcentaje de recargo negativo")
    void testRegistrarVentaConRecargoNegativo() {
        DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);
        VentaFinanciada venta = new VentaFinanciada("VF-005", LocalDate.now(), cliente, -5.0);
        venta.agregarDetalle(detalle);

        assertThrows(VentaFinanciadaInvalidaException.class, () -> {
            ventaService.registrarVentaFinanciada(venta);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta financiada si el stock es insuficiente")
    void testRegistrarVentaFinanciadaStockInsuficiente() {
        DetalleVenta detalle = new DetalleVenta(15, productoEntidad.getPrecioBase(), productoEntidad);
        VentaFinanciada venta = new VentaFinanciada("VF-006", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);

        assertThrows(StockInsuficienteException.class, () -> {
            ventaService.registrarVentaFinanciada(venta);
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
            ventaService.registrarVentaFinanciada(venta);
        });
    }
}