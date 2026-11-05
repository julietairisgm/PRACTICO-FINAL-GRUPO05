package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clientePrueba;

    @BeforeEach
    void setUp() {
        // Cliente configurado con datos genéricos / Test
        clientePrueba = new Cliente("12345678", "Test", "TestApellido", null, "3884000000", "test@mail.com", "Direccion Test 123", TipoCliente.PARTICULAR);
        clientePrueba.setIdCliente(1L);
    }

    @Test
    void guardarClienteExitoso() {
        when(clienteRepository.existsByNumeroIdentificacion(any())).thenReturn(false);
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePrueba);

        Cliente resultado = clienteService.guardarCliente(clientePrueba);

        assertNotNull(resultado);
        assertEquals("12345678", resultado.getNumeroIdentificacion());
        verify(clienteRepository, times(1)).save(clientePrueba);
    }

    @Test
    void guardarClienteFallaPorDuplicado() {
        when(clienteRepository.existsByNumeroIdentificacion("12345678")).thenReturn(true);

        assertThrows(ClienteService.ClienteDuplicadoException.class, () -> {
            clienteService.guardarCliente(clientePrueba);
        });

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void buscarPorNumeroIdentificacionExitoso() {
        when(clienteRepository.findByNumeroIdentificacion("12345678")).thenReturn(Optional.of(clientePrueba));

        Cliente resultado = clienteService.buscarPorNumeroIdentificacion("12345678");

        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
    }

    @Test
    void buscarPorNumeroIdentificacionNoEncontradoLanzaExcepcion() {
        when(clienteRepository.findByNumeroIdentificacion("99999999")).thenReturn(Optional.empty());

        assertThrows(ClienteService.ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNumeroIdentificacion("99999999");
        });
    }

    @Test
    void buscarPorNombreORazonSocialExitoso() {
        when(clienteRepository.buscarPorNombreORazonSocial("Test")).thenReturn(List.of(clientePrueba));

        List<Cliente> resultados = clienteService.buscarPorNombreORazonSocial("Test");

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
    }

    @Test
    void buscarPorNombreORazonSocialVacioLanzaExcepcion() {
        when(clienteRepository.buscarPorNombreORazonSocial("Inexistente")).thenReturn(Collections.emptyList());

        assertThrows(ClienteService.ClienteNoEncontradoException.class, () -> {
            clienteService.buscarPorNombreORazonSocial("Inexistente");
        });
    }
}