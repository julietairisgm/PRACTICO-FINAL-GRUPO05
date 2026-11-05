package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.transaction.Transactional;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClienteTest {

	@Autowired
    private ClienteService clienteService; 

    private Cliente clientePrueba;

    @BeforeEach
    void setUp() {
        // Cliente configurado con datos genéricos / Test
        clientePrueba = new Cliente("12345678", "Test", "TestApellido", null, "3884000000", "test@mail.com", "Direccion Test 123", TipoCliente.PARTICULAR);
    }

    @Test
    void guardarClienteExitoso() {

        Cliente resultado = clienteService.guardarCliente(clientePrueba);

        assertNotNull(resultado);
        assertNotNull(resultado.getIdCliente());
        assertEquals("12345678", resultado.getNumeroIdentificacion());
    }

    @Test
    void guardarClienteFallaPorDuplicado() {
    	clienteService.guardarCliente(clientePrueba);

        Cliente clienteDuplicado = new Cliente("12345678", "Laura", "Mamani", null, "3884111111", "lauratest@mail.com", "Calle Falsa 123", TipoCliente.PARTICULAR);

        assertThrows(ClienteDuplicadoException.class, () -> {
            clienteService.guardarCliente(clienteDuplicado);
        });
    }

    @Test
    void buscarPorNumeroIdentificacionExitoso() {
    	clienteService.guardarCliente(clientePrueba);

        Cliente resultado = clienteService.buscarPorNumeroIdentificacion(
            clientePrueba.getNumeroIdentificacion()
        );

        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
    }

    @Test
    void buscarPorNumeroIdentificacionNoEncontradoLanzaExcepcion() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNumeroIdentificacion("99999999");
        });
    }

    @Test
    void buscarPorNombreORazonSocialExitoso() {
    	clienteService.guardarCliente(clientePrueba);

        List<Cliente> resultados = clienteService.buscarPorNombreORazonSocial("Test");

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
    }

    @Test
    void buscarPorNombreORazonSocialVacioLanzaExcepcion() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNombreORazonSocial("Inexistente");
        });
    }
}