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
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.VentaFinanciadaInvalidaException;
import ar.edu.unju.fi.poo.exceptions.VentaInvalidaException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.service.CategoriaService;
import ar.edu.unju.fi.poo.service.ClienteService;
import ar.edu.unju.fi.poo.service.ProductoService;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.transaction.Transactional;

import ar.edu.unju.fi.poo.exceptions.VentaYaSaldadaException;
import ar.edu.unju.fi.poo.model.EstadoVenta;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.repository.VentaFinanciadaRepository;

@SpringBootTest
@Transactional
public class VentaFinanciadaTest {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private VentaFinanciadaRepository ventaFinanciadaRepository;

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
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        VentaResponseDTO guardada = ventaService.registrarVenta(venta);

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
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(15.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        ventaService.registrarVenta(venta);

        List<CuotaResponseDTO> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());

        assertFalse(cuotasPendientes.isEmpty());
        assertEquals(6, cuotasPendientes.size());
        assertTrue(cuotasPendientes.stream().allMatch(c -> "ADEUDADA".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe consultar cuotas pendientes por número de identificación (DNI/CUIT)")
    void testConsultarEstadoCuentaPorIdentificacion() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        ventaService.registrarVenta(venta);

        List<CuotaResponseDTO> cuotasPendientes = ventaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(cliente.getNumeroIdentificacion());

        assertEquals(6, cuotasPendientes.size());
    }

    @Test
    @DisplayName("No debe registrar una venta financiada nula")
    void testRegistrarVentaFinanciadaNula() {
        assertThrows(VentaInvalidaException.class, () -> {
            ventaService.registrarVenta(null);
        });
    }

    @Test
    @DisplayName("No debe registrar una venta financiada sin detalles de venta")
    void testRegistrarVentaFinanciadaSinDetalles() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of());
        assertThrows(VentaFinanciadaInvalidaException.class, () -> ventaService.registrarVenta(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta con porcentaje de recargo negativo")
    void testRegistrarVentaConRecargoNegativo() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(-5.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));

        assertThrows(VentaFinanciadaInvalidaException.class, () -> ventaService.registrarVenta(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta financiada si el stock es insuficiente")
    void testRegistrarVentaFinanciadaStockInsuficiente() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 15)));

        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVenta(venta));
    }

    @Test
    @DisplayName("No debe registrar una venta con un producto inexistente")
    void testRegistrarVentaProductoInexistente() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(99999, 1)));

        assertThrows(ProductoNoEncontradoException.class, () -> ventaService.registrarVenta(venta));
    }   
    
    @Test
    @DisplayName("Debe cobrar la primera cuota impaga antes de su vencimiento por su monto original")
    void testCobrarPrimeraCuotaExitoso() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(20.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        VentaResponseDTO guardada = ventaService.registrarVenta(venta);

        CuotaResponseDTO cuotaPagada = ventaService.cobrarCuota(guardada.getIdVenta());

        assertNotNull(cuotaPagada);
        assertEquals(1, cuotaPagada.getNumeroCuota());
        assertEquals(120000.0 / 6, cuotaPagada.getImporte());
        assertEquals("PAGADA", cuotaPagada.getEstado());

        // Verificar que quedan 5 pendientes y la venta sigue PENDIENTE
        List<CuotaResponseDTO> pendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());
        assertEquals(5, pendientes.size());

        VentaFinanciada ventaEntidad = ventaFinanciadaRepository.findById(guardada.getIdVenta()).orElseThrow();
        assertEquals(EstadoVenta.PENDIENTE, ventaEntidad.getEstado());
    }

    @Test
    @DisplayName("Debe cobrar cuotas respetando estrictamente el orden correlativo sin saltear")
    void testCobrarCuotasEnOrdenCorrelativo() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(0.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        VentaResponseDTO guardada = ventaService.registrarVenta(venta);

        CuotaResponseDTO c1 = ventaService.cobrarCuota(guardada.getIdVenta());
        CuotaResponseDTO c2 = ventaService.cobrarCuota(guardada.getIdVenta());
        CuotaResponseDTO c3 = ventaService.cobrarCuota(guardada.getIdVenta());

        assertEquals(1, c1.getNumeroCuota());
        assertEquals(2, c2.getNumeroCuota());
        assertEquals(3, c3.getNumeroCuota());

        // La próxima pendiente en el estado de cuenta debe ser la cuota 4
        List<CuotaResponseDTO> pendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());
        assertEquals(3, pendientes.size());
        assertEquals(4, pendientes.get(0).getNumeroCuota());
    }

    @Test
    @DisplayName("Debe marcar la venta como SALDADA al pagar la última cuota e impedir cobros posteriores")
    void testSaldarVentaYRechazarCobroExtra() {
    	VentaRequestDTO venta = new VentaRequestDTO();
    	venta.setModalidad("FINANCIADA");
        venta.setIdCliente(cliente.getIdCliente());
        venta.setPorcentajeRecargo(10.0);
        venta.setDetalles(List.of(new DetalleVentaRequestDTO(producto.getIdProducto(), 1)));
        VentaResponseDTO guardada = ventaService.registrarVenta(venta);

        // Pagar las 6 cuotas correlativamente
        for (int i = 1; i <= 6; i++) {
            CuotaResponseDTO cuota = ventaService.cobrarCuota(guardada.getIdVenta());
            assertEquals(i, cuota.getNumeroCuota());
            assertEquals("PAGADA", cuota.getEstado());
        }

        // No deben quedar cuotas pendientes
        List<CuotaResponseDTO> pendientes = ventaService.consultarEstadoCuentaCuotasPendientes(cliente.getIdCliente());
        assertTrue(pendientes.isEmpty());

        // El estado general de la venta debe haber cambiado automáticamente a SALDADA
        VentaFinanciada ventaEntidad = ventaFinanciadaRepository.findById(guardada.getIdVenta()).orElseThrow();
        assertEquals(EstadoVenta.SALDADA, ventaEntidad.getEstado());

        // Intentar cobrar otra cuota sobre la venta saldada debe lanzar VentaYaSaldadaException
        assertThrows(VentaYaSaldadaException.class, () -> {
            ventaService.cobrarCuota(guardada.getIdVenta());
        });
    }


}