package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import ar.edu.unju.fi.poo.model.EstadoCuota;

public class CuotaEstadoCuentaDTO {
    private Integer idCuota;
    private Integer numeroCuota;
    private double importe;
    private LocalDate fechaVencimiento;
    private EstadoCuota estado;

    public CuotaEstadoCuentaDTO() {}

    public Integer getIdCuota() { return idCuota; }
    public void setIdCuota(Integer idCuota) { this.idCuota = idCuota; }

    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public EstadoCuota getEstado() { return estado; }
    public void setEstado(EstadoCuota estado) { this.estado = estado; }
}