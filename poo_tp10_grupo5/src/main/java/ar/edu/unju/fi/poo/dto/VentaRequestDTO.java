package ar.edu.unju.fi.poo.dto;

import java.util.List;

public class VentaRequestDTO {
	private String modalidad;
    private Integer idCliente;
    private String codigoCupon;
    private Double porcentajeRecargo;
    private List<DetalleVentaRequestDTO> detalles;

    public VentaRequestDTO() {}

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoCupon() {
        return codigoCupon;
    }

    public void setCodigoCupon(String codigoCupon) {
        this.codigoCupon = codigoCupon;
    }

    public Double getPorcentajeRecargo() {
        return porcentajeRecargo;
    }

    public void setPorcentajeRecargo(Double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }

    public List<DetalleVentaRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequestDTO> detalles) {
        this.detalles = detalles;
    }

}
