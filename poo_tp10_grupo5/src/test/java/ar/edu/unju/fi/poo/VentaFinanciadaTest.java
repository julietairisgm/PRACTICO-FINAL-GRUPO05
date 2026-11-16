package ar.edu.unju.fi.poo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.dto.CuotaResponseDTO;
import ar.edu.unju.fi.poo.dto.DetalleVentaRequestDTO;
import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.dto.VentaFinanciadaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.VentaFinanciadaInvalidaException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.service.CategoriaService;
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
    private CategoriaService categoriaService;

    private ClienteDto cliente;
    private CategoriaDto categoriaDto;
    private ProductoDto producto;

    @BeforeEach
    void setUp() {
        ClienteDto nuevoCliente = new ClienteDto();
        nuevoCliente.setNumeroIdentificacion("88990011");
        nuevoCliente.setNombre("Ramon");
        nuevoCliente.setApellido("Gomez");
        nuevoCliente.setRazonSocial(null);
        nuevoCliente.setTelefono("3884111222");
        nuevoCliente.setEmail("ramon@test.com");
        nuevoCliente.setDireccion("Av. Bolivia 123");
        nuevoCliente.setTipoCliente(TipoCliente.PARTICULAR);
        cliente = clienteService.guardarCliente(nuevoCliente);

        CategoriaDto nuevaCategoria = new CategoriaDto();
        nuevaCategoria.setNombre("Tecnologia");
        categoriaDto = categoriaService.crear(nuevaCategoria);

        ProductoDto nuevoProducto = new ProductoDto();
        nuevoProducto.setNombre("Smart TV 55");
        nuevoProducto.setDescripcion("4K UHD");
        nuevoProducto.setMarca("Samsung");
        nuevoProducto.setStock(10);
        nuevoProducto.setPrecioBase(100000.0);
        nuevoProducto.setCategoriaDto(categoriaDto);
        producto = productoService.guardarProducto(nuevoProducto);
    }

    @Test
    @DisplayName("Debe registrar una venta financiada correctamente con recargo y 6 cuotas consecutivas")
    void testRegistrarVentaFinanciadaExitoso() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        VentaResponseDTO guardada = ventaService.registrarVentaFinanciada(venta);

        assertNotNull(guardada.getIdVenta());
        assertEquals(110000.0, guardada.getTotal());

        List<CuotaResponseDTO> cuotas = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());

        assertEquals(6, cuotas.size());
        assertEquals(110000.0 / 6, cuotas.get(0).getImporte());
        assertTrue(cuotas.stream().allMatch(c -> "ADEUDADA".equals(c.getEstado())));

        ProductoDto productoActualizado = productoService.buscarPorId(producto.getIdProducto());

        assertEquals(9, productoActualizado.getStock());
    }

    @Test
    @DisplayName("Debe consultar el estado de cuenta de cuotas pendientes de un cliente")
    void testConsultarEstadoCuentaCuotasPendientes() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(15.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        ventaService.registrarVentaFinanciada(venta);

        List<CuotaResponseDTO> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());

        assertFalse(cuotasPendientes.isEmpty());
        assertEquals(6, cuotasPendientes.size());
        assertTrue(cuotasPendientes.stream().allMatch(c -> "ADEUDADA".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe consultar cuotas pendientes por número de identificación (DNI/CUIT)")
    void testConsultarEstadoCuentaPorIdentificacion() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        ventaService.registrarVentaFinanciada(venta);

        List<CuotaResponseDTO> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(cliente.getNumeroIdentificacion());

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
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of());
        assertThrows(VentaFinanciadaInvalidaException.class, () -> ventaService.registrarVentaFinanciada(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta con porcentaje de recargo negativo")
    void testRegistrarVentaConRecargoNegativo() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(-5.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));

        assertThrows(VentaFinanciadaInvalidaException.class, () -> ventaService.registrarVentaFinanciada(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta financiada si el stock es insuficiente")
    void testRegistrarVentaFinanciadaStockInsuficiente() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 15)));

        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVentaFinanciada(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta con un producto inexistente")
    void testRegistrarVentaProductoInexistente() {
        VentaFinanciadaRequestDTO venta = new VentaFinanciadaRequestDTO();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(99999, 1)));

        assertThrows(ProductoNoEncontradoException.class, () -> ventaService.registrarVentaFinanciada(venta));
    }   

}