package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaEstadoCuentaDTO {
    private Integer idVenta;
    private String codigoComprobante;
    private LocalDate fechaVenta;
    private String modalidad;
    private double total;
    private List<CuotaEstadoCuentaDTO> cuotas = new ArrayList<>();

    public VentaEstadoCuentaDTO() {}

    public Integer getIdVenta() { return idVenta; }
    public void setIdVenta(Integer idVenta) { this.idVenta = idVenta; }

    public String getCodigoComprobante() { return codigoComprobante; }
    public void setCodigoComprobante(String codigoComprobante) { this.codigoComprobante = codigoComprobante; }

    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<CuotaEstadoCuentaDTO> getCuotas() { return cuotas; }
    public void setCuotas(List<CuotaEstadoCuentaDTO> cuotas) { this.cuotas = cuotas; }
}