package ar.edu.unju.fi.poo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteDto guardarCliente(ClienteDto dto) {
        if (clienteRepository.existsByNumeroIdentificacion(dto.getNumeroIdentificacion())) {
            throw new ClienteDuplicadoException("Ya existe un cliente con la identificación: " + dto.getNumeroIdentificacion());
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ClienteDuplicadoException("Ya existe un cliente registrado con el email: " + dto.getEmail());
        }

        Cliente cliente = convertirAEntidad(dto);
        Cliente guardado = clienteRepository.save(cliente);
        return convertirADto(guardado);
    }

    @Transactional(readOnly = true)
    public ClienteDto buscarPorNumeroIdentificacion(String numeroIdentificacion) {
        Cliente cliente = clienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró ningún cliente con identificación: " + numeroIdentificacion));
        return convertirADto(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> buscarPorNombreORazonSocial(String termino) {
        List<Cliente> clientes = clienteRepository.buscarPorNombreORazonSocial(termino);
        if (clientes.isEmpty()) {
            throw new ClienteNoEncontradoException("No se encontraron clientes que coincidan con: " + termino);
        }
        return convertirListaADto(clientes);
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> listarTodos() {
        return convertirListaADto(clienteRepository.findAll());
    }

    @Transactional
    public ClienteDto actualizarCliente(Integer id, ClienteDto dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró el cliente con ID: " + id));

        clienteExistente.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        clienteExistente.setNombre(dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        clienteExistente.setRazonSocial(dto.getRazonSocial());
        clienteExistente.setTelefono(dto.getTelefono());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setDireccion(dto.getDireccion());
        clienteExistente.setTipoCliente(dto.getTipoCliente());

        Cliente actualizado = clienteRepository.save(clienteExistente);
        return convertirADto(actualizado);
    }

    // --- MÉTODOS AUXILIARES DE CONVERSIÓN ---

    private ClienteDto convertirADto(Cliente cliente) {
        if (cliente == null) return null;
        ClienteDto dto = new ClienteDto();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNumeroIdentificacion(cliente.getNumeroIdentificacion());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setRazonSocial(cliente.getRazonSocial());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setDireccion(cliente.getDireccion());
        dto.setTipoCliente(cliente.getTipoCliente());
        return dto;
    }

    private Cliente convertirAEntidad(ClienteDto dto) {
        if (dto == null) return null;
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        cliente.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setRazonSocial(dto.getRazonSocial());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTipoCliente(dto.getTipoCliente());
        return cliente;
    }

    private List<ClienteDto> convertirListaADto(List<Cliente> clientes) {
        List<ClienteDto> dtos = new ArrayList<>();
        if (clientes != null) {
            for (Cliente c : clientes) {
                dtos.add(convertirADto(c));
            }
        }
        return dtos;
    }
}