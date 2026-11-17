package ar.edu.unju.fi.poo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ar.edu.unju.fi.poo.controller.ClienteController;
import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDTO;
import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.model.*;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.ProductoService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class EstadoCuentaTest {

    @Autowired
    private ClienteController clienteController;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("30111222", "Laura", "Gomez", null, "3884123456", "laura@test.com", "Jujuy", TipoCliente.PARTICULAR);
        cliente = clienteRepository.save(cliente);

        Categoria cat = new Categoria();
        cat.setNombre("Tecnología");
        cat = categoriaRepository.save(cat);

        CategoriaDto catDto = new CategoriaDto();
        catDto.setIdCategoria(cat.getIdCategoria());

        ProductoDto prodDto = new ProductoDto();
        prodDto.setNombre("Notebook");
        prodDto.setDescripcion("Core i5");
        prodDto.setMarca("Lenovo");
        prodDto.setStock(5);
        prodDto.setPrecioBase(600000.0);
        prodDto.setCategoriaDto(catDto);
        ProductoDto prodGuardado = productoService.guardarProducto(prodDto);

        Producto productoEntidad = productoRepository.findById(prodGuardado.getIdProducto()).orElseThrow();
        DetalleVenta detalle = new DetalleVenta(1, productoEntidad.getPrecioBase(), productoEntidad);

        VentaFinanciada venta = new VentaFinanciada("V-FIN-100", LocalDate.now(), cliente, 10.0);
        venta.agregarDetalle(detalle);
        venta.generarCuotas();
        ventaRepository.save(venta);
    }

    @Test
    @DisplayName("GET /api/clientes/{id}/estado-cuenta debe devolver 200 OK con ventas y cuotas del cliente")
    void testObtenerEstadoCuentaExitoso() {
        ResponseEntity<EstadoCuentaResponseDTO> respuesta = clienteController.obtenerEstadoCuenta(cliente.getIdCliente());

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(cliente.getIdCliente(), respuesta.getBody().getCliente().getIdCliente());
        assertEquals("Laura", respuesta.getBody().getCliente().getNombre());
        assertEquals(1, respuesta.getBody().getVentas().size());
        assertEquals("FINANCIADA", respuesta.getBody().getVentas().get(0).getModalidad());
        assertEquals(6, respuesta.getBody().getVentas().get(0).getCuotas().size());
        assertEquals(EstadoCuota.ADEUDADA, respuesta.getBody().getVentas().get(0).getCuotas().get(0).getEstado());
    }

    @Test
    @DisplayName("GET /api/clientes/{id}/estado-cuenta debe lanzar ClienteNoEncontradoException (404) si el cliente no existe")
    void testObtenerEstadoCuentaClienteInexistente() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteController.obtenerEstadoCuenta(999999);
        });
    }
}