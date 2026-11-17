package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

public class EstadoCuentaResponseDTO {
    private ClienteDto cliente;
    private List<VentaEstadoCuentaDTO> ventas = new ArrayList<>();

    public EstadoCuentaResponseDTO() {}

    public ClienteDto getCliente() { return cliente; }
    public void setCliente(ClienteDto cliente) { this.cliente = cliente; }

    public List<VentaEstadoCuentaDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaEstadoCuentaDTO> ventas) { this.ventas = ventas; }
}