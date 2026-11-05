package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    // Excepciones integradas en la misma clase
    public static class ClienteNoEncontradoException extends RuntimeException {
        
		private static final long serialVersionUID = 1L;

		public ClienteNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ClienteDuplicadoException extends RuntimeException {
        
		private static final long serialVersionUID = 1L;

		public ClienteDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente guardarCliente(Cliente cliente) {
        if (clienteRepository.existsByNumeroIdentificacion(cliente.getNumeroIdentificacion())) {
            throw new ClienteDuplicadoException("Ya existe un cliente con el número de identificación: " + cliente.getNumeroIdentificacion());
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ClienteDuplicadoException("Ya existe un cliente registrado con el email: " + cliente.getEmail());
        }
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorNumeroIdentificacion(String numeroIdentificacion) {
        return clienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró ningún cliente con identificación: " + numeroIdentificacion));
    }

    public List<Cliente> buscarPorNombreORazonSocial(String termino) {
        List<Cliente> clientes = clienteRepository.buscarPorNombreORazonSocial(termino);
        if (clientes.isEmpty()) {
            throw new ClienteNoEncontradoException("No se encontraron clientes que coincidan con: " + termino);
        }
        return clientes;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public List<Cliente> listarTodos1() {
        return clienteRepository.findAll();
    }
}