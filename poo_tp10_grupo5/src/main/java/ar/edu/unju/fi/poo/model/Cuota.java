package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Integer idCuota;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "importe", nullable = false)
    private double importe;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCuota estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private VentaFinanciada ventaFinanciada;

    public Cuota() {}

    public Cuota(Integer numeroCuota, double importe, LocalDate fechaVencimiento, VentaFinanciada ventaFinanciada) {
        this.numeroCuota = numeroCuota;
        this.importe = importe;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = EstadoCuota.ADEUDADA;
        this.ventaFinanciada = ventaFinanciada;
    }

    public void marcarPagoCuota() {
        this.estado = EstadoCuota.PAGADA;
    }

    // Getters y Setters

    public Integer getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Integer idCuota) {
        this.idCuota = idCuota;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoCuota getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuota estado) {
        this.estado = estado;
    }

    public VentaFinanciada getVentaFinanciada() {
        return ventaFinanciada;
    }

    public void setVentaFinanciada(VentaFinanciada ventaFinanciada) {
        this.ventaFinanciada = ventaFinanciada;
    }
}