package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.dto.ClienteDto;

public interface ClienteService {

    ClienteDto guardarCliente(ClienteDto dto);

    ClienteDto buscarPorNumeroIdentificacion(String numeroIdentificacion);

    List<ClienteDto> buscarPorNombreORazonSocial(String termino);

    List<ClienteDto> listarTodos();

    ClienteDto actualizarCliente(Integer id, ClienteDto dto);
}
