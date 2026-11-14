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
import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.dto.DetalleVentaRequestDTO;
import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.dto.VentaContadoRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaFinanciadaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.exceptions.*;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.service.CategoriaService;
import ar.edu.unju.fi.poo.service.ClienteService;
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
	private ClienteService clienteService;
	
	@Autowired
    private CategoriaService categoriaService;
	
	@Autowired
    private CuponRepository cuponRepository;
	
	private ClienteDto cliente;
	private CategoriaDto categoriaDto;
	private ProductoDto producto;
	private Cupon cupon;

	@BeforeEach
	void setUp() {
		ClienteDto nuevoCliente = new ClienteDto();
		nuevoCliente.setNumeroIdentificacion("22334455");
		nuevoCliente.setNombre("Ana");
		nuevoCliente.setApellido("Lopez");
		nuevoCliente.setRazonSocial(null);
		nuevoCliente.setTelefono("3884000000");
		nuevoCliente.setEmail("ana@test.com");
		nuevoCliente.setDireccion("San Salvador de Jujuy");
		nuevoCliente.setTipoCliente(TipoCliente.PARTICULAR);
		cliente = clienteService.guardarCliente(nuevoCliente);

	    // Crear categoría
	    CategoriaDto nuevaCategoria = new CategoriaDto();
	    nuevaCategoria.setNombre("Electrodomésticos");
	    categoriaDto = categoriaService.crear(nuevaCategoria);

	    // Crear producto
	    ProductoDto nuevoProducto = new ProductoDto();
	    nuevoProducto.setNombre("Cafetera");
	    nuevoProducto.setDescripcion("Expreso");
	    nuevoProducto.setMarca("Oster");
	    nuevoProducto.setStock(10);
	    nuevoProducto.setPrecioBase(25000.0);
	    nuevoProducto.setCategoriaDto(categoriaDto);
	    producto = productoService.guardarProducto(nuevoProducto);
	    
	    cupon = new Cupon("DESC10", 10.0, LocalDate.now().plusDays(10));
	    cuponRepository.save(cupon);
	}

	@Test
	@DisplayName("Debe registrar una venta de contado correctamente")
	void testRegistrarVentaContado() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);

	    venta.setDetalles(List.of(
	        new DetalleVentaRequestDTO(producto.getIdProducto(), 2)
	    ));

	    VentaResponseDTO ventaGuardada = ventaService.registrarVentaContado(venta);

	    assertNotNull(ventaGuardada.getIdVenta());
	    assertEquals(50000.0, ventaGuardada.getTotal());
	}
	
	@Test
	@DisplayName("Debe registrar una venta de contado aplicando el descuento del cupón")
	void testRegistrarVentaContadoConCuponValido() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon("DESC10");
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 2)));

	    VentaResponseDTO guardada = ventaService.registrarVentaContado(venta);

	    assertNotNull(guardada.getIdVenta());
	    assertEquals(50000.0, guardada.getSubtotal());
	    assertEquals(45000.0, guardada.getTotal());
	}
	
	@Test
	@DisplayName("Debe rechazar una venta con un cupón vencido")
	void testRegistrarVentaConCuponVencido() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon("DESCVENCIDO");
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 2)));

	    assertThrows(CuponInvalidoException.class,() -> ventaService.registrarVentaContado(venta));
	}
	
	@Test
	@DisplayName("Debe rechazar una venta con un cupón ya utilizado")
	void testRegistrarVentaConCuponUtilizado() {
		// AQUI HABRIA QUE USAR CUPON DTO, o alguna forma de no usar la identidad
		// Una opcion es crear dto y usar crearcupon en el servicio... 
	    Cupon cupon = new Cupon("DESCUENTOUSADO", 10.0, LocalDate.now().plusDays(10));
	    cupon.setUsado(true);
	    cupon = cuponRepository.save(cupon);
	    
	    VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon("DESCUENTOUSADO");
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 2)));

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
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);

	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 0)));

	    assertThrows(VentaInvalidaException.class, () ->
	        ventaService.registrarVentaContado(venta)
	    );
	}
	
	@Test
	@DisplayName("No debe registrar una venta con un producto inexistente")
	void testRegistrarVentaConProductoInexistente() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
		venta.setIdCliente(cliente.getIdCliente());
		venta.setCodigoCupon(null);

		venta.setDetalles(List.of(new DetalleVentaRequestDTO(999999L, 1)));

		assertThrows(ProductoNoEncontradoException.class, () ->
			ventaService.registrarVentaContado(venta))
		;
	}
	
	@Test
	@DisplayName("No debe registrar una venta con stock insuficiente")
	void testRegistrarVentaConStockInsuficiente() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 11)));

	    assertThrows(StockInsuficienteException.class, () ->
	        ventaService.registrarVentaContado(venta)
	    );
	}
	
	@Test
	@DisplayName("Debe buscar el historial de ventas de un cliente")
	void testBuscarHistorialPorCliente() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 2)));

	    VentaResponseDTO ventaGuardada = ventaService.registrarVentaContado(venta);

	    List<Venta> historial = ventaService.buscarHistorialPorCliente(cliente.getIdCliente());

	    assertFalse(historial.isEmpty());
	    assertTrue(historial.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	}
	
	@Test
	@DisplayName("Debe buscar ventas dentro de un rango de fechas")
	void testBuscarPorRangoFechas() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));

	    VentaResponseDTO ventaGuardada = ventaService.registrarVentaContado(venta);

	    List<Venta> ventas = ventaService.buscarPorRangoFechas(LocalDate.now().minusDays(1),LocalDate.now().plusDays(1));

	    assertTrue(ventas.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	}
	
	@Test
	@DisplayName("Debe rechazar un rango de fechas inválido")
	void testBuscarPorRangoFechasInvalido() {
	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.buscarPorRangoFechas(LocalDate.now(), LocalDate.now().minusDays(1));
	    });
	}
	
	@Test
	@DisplayName("Debe buscar ventas por modalidad CONTADO")
	void testBuscarPorModalidad() {
		VentaContadoRequestDTO venta = new VentaContadoRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setCodigoCupon(null);
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));

	    VentaResponseDTO ventaGuardada = ventaService.registrarVentaContado(venta);
	    List<Venta> ventas = ventaService.buscarPorModalidad("CONTADO");

	    assertTrue(ventas.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	    assertEquals("CONTADO", ventaGuardada.getModalidad());
	}
	

	@Test
	@DisplayName("Debe buscar ventas por modalidad FINANCIADO")
	void testBuscarPorModalidadFinanciado() {
		VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
	    venta.setIdCliente(cliente.getIdCliente());
	    venta.setPorcentajeRecargo(10.0);
	    venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));

	    VentaResponseDTO ventaGuardada = ventaService.registrarVentaFinanciada(venta);
	    List<Venta> ventas = ventaService.buscarPorModalidad("FINANCIADO");

	    assertTrue(ventas.stream().anyMatch(v -> v.getIdVenta().equals(ventaGuardada.getIdVenta())));
	    assertEquals("FINANCIADO", ventaGuardada.getModalidad());
	}

	
	@Test
	@DisplayName("Debe rechazar una modalidad inválida")
	void testBuscarPorModalidadInvalida() {
	    assertThrows(VentaInvalidaException.class, () -> {
	        ventaService.buscarPorModalidad("OTRA");
	    });
	}
}
