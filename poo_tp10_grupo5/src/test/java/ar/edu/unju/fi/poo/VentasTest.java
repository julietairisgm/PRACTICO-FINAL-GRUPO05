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
import ar.edu.unju.fi.poo.exceptions.*;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.repository.CuponRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.service.ProductoService;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.transaction.Transactional;
import ar.edu.unju.fi.poo.repository.ClienteRepository;



@SpringBootTest
@Transactional
public class VentasTest {
	@Autowired
	private VentaService ventaService;

	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
    private CategoriaRepository categoriaRepository;
	
	@Autowired
    private CuponRepository cuponRepository;
	
	private Cliente cliente;
	private Categoria categoria;
	private ProductoDto producto;
	//Provisorio mientras se implementa DTOS
	private Producto productoEntidad;
	private Cupon cupon;

	@BeforeEach
	void setUp() {
	    Cliente nuevoCliente = new Cliente("22334455", "Ana", "Lopez", null,"3884000000", "ana@test.com","San Salvador de Jujuy", TipoCliente.PARTICULAR);
	    cliente = clienteRepository.save(nuevoCliente);


	    // Crear categoría
	    categoria = new Categoria();
	    categoria.setNombre("Electrodomésticos");
	    categoria = categoriaRepository.save(categoria);

	    CategoriaDto categoriaDto = new CategoriaDto();
	    categoriaDto.setIdCategoria(categoria.getIdCategoria());

	    // Crear producto
	    ProductoDto nuevoProducto = new ProductoDto();
	    nuevoProducto.setNombre("Cafetera");
	    nuevoProducto.setDescripcion("Expreso");
	    nuevoProducto.setMarca("Oster");
	    nuevoProducto.setStock(10);
	    nuevoProducto.setPrecioBase(25000.0);
	    nuevoProducto.setCategoriaDto(categoriaDto);

	    producto = productoService.guardarProducto(nuevoProducto);

	    // Recuperar entidad para construir DetalleVenta
	    productoEntidad = productoRepository.findById(producto.getIdProducto())
	            .orElseThrow();
	    
	    cupon = new Cupon("DESC10", 10.0, LocalDate.now().plusDays(10));
	    cuponRepository.save(cupon);
	}

	@Test
	@DisplayName("Debe registrar una venta de contado correctamente")
	void testRegistrarVentaContado() {
	    DetalleVenta detalle = new DetalleVenta(2, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-001");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.agregarDetalle(detalle);

	    VentaContado ventaGuardada = ventaService.registrarVentaContado(venta);

	    assertNotNull(ventaGuardada.getIdVenta());
	    assertEquals(50000.0, ventaGuardada.getSubtotal());
	    assertEquals(50000.0, ventaGuardada.getTotal());
	}
	
	@Test
	@DisplayName("Debe registrar una venta de contado aplicando el descuento del cupón")
	void testRegistrarVentaContadoConCuponValido() {
	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-CUP-001");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.setCupon(cupon);

	    DetalleVenta detalle = new DetalleVenta(2, productoEntidad.getPrecioBase(), productoEntidad);
	    venta.agregarDetalle(detalle);
	    VentaContado guardada = ventaService.registrarVentaContado(venta);

	    assertEquals(50000.0, guardada.getSubtotal());
	    assertEquals(45000.0, guardada.getTotal());
	    assertTrue(cuponRepository.findById(cupon.getIdCupon()).get().isUsado());
	}
	
	@Test
	@DisplayName("Debe rechazar una venta con un cupón vencido")
	void testRegistrarVentaConCuponVencido() {
	    Cupon cupon = new Cupon("DESCVENCIDO", 10.0, LocalDate.now().minusDays(1));
	    DetalleVenta detalle = new DetalleVenta(2, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-CUP-003");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.setCupon(cupon);
	    venta.agregarDetalle(detalle);

	    assertThrows(CuponInvalidoException.class, () -> ventaService.registrarVentaContado(venta));
	}
	
	@Test
	@DisplayName("Debe rechazar una venta con un cupón ya utilizado")
	void testRegistrarVentaConCuponUtilizado() {
	    Cupon cupon = new Cupon("DESCUENTOUSADO", 10.0, LocalDate.now().plusDays(10));
	    cupon.setUsado(true);
	    cupon = cuponRepository.save(cupon);
	    
	    DetalleVenta detalle = new DetalleVenta(2, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-CUP-004");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.setCupon(cupon);
	    venta.agregarDetalle(detalle);

	    assertThrows(CuponInvalidoException.class, () -> ventaService.registrarVentaContado(venta));
	}
	
	@Test
	@DisplayName("No debe registrar una venta nula")
	void testRegistrarVentaNula() {
	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.registrarVentaContado(null);
	    });
	}
	
	@Test
	@DisplayName("No debe registrar una venta sin detalles")
	void testRegistrarVentaSinDetalles() {
	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-002");
	    venta.setFechaVenta(LocalDate.now());

	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.registrarVentaContado(venta);
	    });
	}
	
	@Test
	@DisplayName("No debe registrar una venta con cantidad inválida")
	void testRegistrarVentaConCantidadInvalida() {
	    DetalleVenta detalle = new DetalleVenta(0, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-003");
	    venta.setFechaVenta(LocalDate.now());
	    venta.agregarDetalle(detalle);

	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.registrarVentaContado(venta);
	    });
	}
	
	@Test
	@DisplayName("No debe registrar una venta con un producto inexistente")
	void testRegistrarVentaConProductoInexistente() {
	    Producto productoInexistente = new Producto();
	    productoInexistente.setIdProducto(999999);
	    DetalleVenta detalle = new DetalleVenta(1,25000.0,productoInexistente);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-004");
	    venta.setFechaVenta(LocalDate.now());
	    venta.agregarDetalle(detalle);

	    assertThrows(ProductoNoEncontradoException.class, () -> {
	        ventaService.registrarVentaContado(venta);
	    });
	}
	
	@Test
	@DisplayName("No debe registrar una venta con stock insuficiente")
	void testRegistrarVentaConStockInsuficiente() {
	    DetalleVenta detalle = new DetalleVenta(11, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-005");
	    venta.setFechaVenta(LocalDate.now());
	    venta.agregarDetalle(detalle);

	    assertThrows(StockInsuficienteException.class, () -> {
	        ventaService.registrarVentaContado(venta);
	    });
	}
	
	@Test
	@DisplayName("Debe buscar el historial de ventas de un cliente")
	void testBuscarHistorialPorCliente() {
	    DetalleVenta detalle = new DetalleVenta(2, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-HIST-001");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.agregarDetalle(detalle);

	    VentaContado ventaGuardada = ventaService.registrarVentaContado(venta);

	    List<Venta> historial = ventaService.buscarHistorialPorCliente(cliente.getIdCliente());

	    assertFalse(historial.isEmpty());
	    assertTrue(historial.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	}
	
	@Test
	@DisplayName("Debe buscar ventas dentro de un rango de fechas")
	void testBuscarPorRangoFechas() {
	    DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-FECHA-001");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.agregarDetalle(detalle);

	    VentaContado ventaGuardada = ventaService.registrarVentaContado(venta);

	    List<Venta> ventas = ventaService.buscarPorRangoFechas(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

	    assertTrue(ventas.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	}
	
	@Test
	@DisplayName("Debe rechazar un rango de fechas inválido")
	void testBuscarPorRangoFechasInvalido() {
	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.buscarPorRangoFechas(
	            LocalDate.now(),
	            LocalDate.now().minusDays(1)
	        );
	    });
	}
	
	@Test
	@DisplayName("Debe buscar ventas por modalidad")
	void testBuscarPorModalidad() {
	    DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);

	    VentaContado venta = new VentaContado();
	    venta.setCodigoComprobante("V-MOD-001");
	    venta.setFechaVenta(LocalDate.now());
	    venta.setCliente(cliente);
	    venta.agregarDetalle(detalle);

	    VentaContado ventaGuardada = ventaService.registrarVentaContado(venta);

	    List<Venta> ventas = ventaService.buscarPorModalidad("CONTADO");

	    assertTrue(ventas.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	    assertTrue(ventaGuardada instanceof VentaContado);
	}
	
	@Test
	@DisplayName("Debe rechazar una modalidad inválida")
	void testBuscarPorModalidadInvalida() {
	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.buscarPorModalidad("OTRA");
	    });
	}
}
