package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.transaction.Transactional;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private ClienteDto clientePrueba;

    @BeforeEach
    void setUp() {
        clientePrueba = new ClienteDto();
        clientePrueba.setNumeroIdentificacion("12345678");
        clientePrueba.setNombre("Test");
        clientePrueba.setApellido("TestApellido");
        clientePrueba.setRazonSocial(null);
        clientePrueba.setTelefono("3884000000");
        clientePrueba.setEmail("test@mail.com");
        clientePrueba.setDireccion("Direccion Test 123");
        clientePrueba.setTipoCliente(TipoCliente.PARTICULAR);
    }

    @Test
    @DisplayName("Debe guardar un cliente correctamente")
    void guardarClienteExitoso() {
        ClienteDto resultado = clienteService.guardarCliente(clientePrueba);

        assertNotNull(resultado);
        assertNotNull(resultado.getIdCliente());
        assertEquals("12345678", resultado.getNumeroIdentificacion());
    }

    @Test
    @DisplayName("Debe lanzar una excepción al guardar un cliente duplicado")
    void guardarClienteFallaPorDuplicado() {
        clienteService.guardarCliente(clientePrueba);

        ClienteDto clienteDuplicado = new ClienteDto();
        clienteDuplicado.setNumeroIdentificacion("12345678");
        clienteDuplicado.setNombre("Laura");
        clienteDuplicado.setApellido("Mamani");
        clienteDuplicado.setRazonSocial(null);
        clienteDuplicado.setTelefono("3884111111");
        clienteDuplicado.setEmail("lauratest@mail.com");
        clienteDuplicado.setDireccion("Calle Falsa 123");
        clienteDuplicado.setTipoCliente(TipoCliente.PARTICULAR);

        assertThrows(ClienteDuplicadoException.class, () -> {
            clienteService.guardarCliente(clienteDuplicado);
        });
    }

    @Test
    @DisplayName("Debe buscar un cliente por su número de identificación")
    void buscarPorNumeroIdentificacionExitoso() {
        clienteService.guardarCliente(clientePrueba);

        ClienteDto resultado = clienteService.buscarPorNumeroIdentificacion(
            clientePrueba.getNumeroIdentificacion()
        );

        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar una excepción si no encuentra el cliente por identificación")
    void buscarPorNumeroIdentificacionNoEncontradoLanzaExcepcion() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNumeroIdentificacion("99999999");
        });
    }

    @Test
    @DisplayName("Debe buscar clientes por nombre o razón social")
    void buscarPorNombreORazonSocialExitoso() {
        clienteService.guardarCliente(clientePrueba);

        List<ClienteDto> resultados = clienteService.buscarPorNombreORazonSocial("Test");

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
    }

    @Test
    @DisplayName("Debe lanzar una excepción cuando no encuentra clientes por nombre o razón social")
    void buscarPorNombreORazonSocialVacioLanzaExcepcion() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNombreORazonSocial("Inexistente");
        });
    }
}
