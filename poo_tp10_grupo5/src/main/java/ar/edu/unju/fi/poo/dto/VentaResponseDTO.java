package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import java.util.List;

public class VentaResponseDTO {
    private Long idVenta;
    private LocalDate fecha;
    private Double total;
    private String modalidad;
    private Long idCliente;
    private String nombreCliente;
    private List<DetalleVentaResponseDTO> detalles;

    public VentaResponseDTO() {}

    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public List<DetalleVentaResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaResponseDTO> detalles) { this.detalles = detalles; }
}