package ar.edu.unju.fi.poo.dto;

import java.util.List;

public class VentaContadoRequestDTO {
    private Integer idCliente;
    private String codigoCupon;
    private List<DetalleVentaRequestDTO> detalles;

    public VentaContadoRequestDTO() {}

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getCodigoCupon() { return codigoCupon; }
    public void setCodigoCupon(String codigoCupon) { this.codigoCupon = codigoCupon; }

    public List<DetalleVentaRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaRequestDTO> detalles) { this.detalles = detalles; }
}