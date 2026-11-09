package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {

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

    public Cliente actualizarCliente(Integer id, Cliente clienteActualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró el cliente con ID: " + id));
        
        clienteExistente.setNumeroIdentificacion(clienteActualizado.getNumeroIdentificacion());
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setRazonSocial(clienteActualizado.getRazonSocial());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setTipoCliente(clienteActualizado.getTipoCliente());

        return clienteRepository.save(clienteExistente);
    }

}