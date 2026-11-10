package ar.edu.unju.fi.poo.dto;

import java.util.List;

public class VentaFinanciadaRequestDTO {
    private Long idCliente;
    private Double porcentajeRecargo;
    private List<DetalleVentaRequestDTO> detalles;

    public VentaFinanciadaRequestDTO() {}

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Double getPorcentajeRecargo() { return porcentajeRecargo; }
    public void setPorcentajeRecargo(Double porcentajeRecargo) { this.porcentajeRecargo = porcentajeRecargo; }

    public List<DetalleVentaRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaRequestDTO> detalles) { this.detalles = detalles; }
}