package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "ventas_financiadas")
public class VentaFinanciada extends Venta {

    @Transient
    public static final int CANTIDAD_CUOTAS = 6;

    @Column(name = "porcentaje_recargo", nullable = false)
    private double porcentajeRecargo;

    @OneToMany(mappedBy = "ventaFinanciada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    public VentaFinanciada() {
        super();
    }

    public VentaFinanciada(String codigoComprobante, LocalDate fechaVenta, Cliente cliente, double porcentajeRecargo) {
        super(codigoComprobante, fechaVenta, cliente);
        this.porcentajeRecargo = porcentajeRecargo;
    }

    public double aplicarRecargo() {
        return getSubtotal() * (porcentajeRecargo / 100.0);
    }

    @Override
    public void calcularTotal() {
        calcularSubtotal();
        setTotal(getSubtotal() + aplicarRecargo());
    }

    public void generarCuotas() {
        this.cuotas.clear();
        calcularTotal();
        
        double importeCuota = getTotal() / CANTIDAD_CUOTAS;
        LocalDate fechaBase = getFechaVenta() != null ? getFechaVenta() : LocalDate.now();

        for (int i = 1; i <= CANTIDAD_CUOTAS; i++) {
            LocalDate vencimiento = fechaBase.plusMonths(i);
            Cuota cuota = new Cuota(i, importeCuota, vencimiento, this);
            this.cuotas.add(cuota);
        }
    }

    // Getters y Setters

    public double getPorcentajeRecargo() {
        return porcentajeRecargo;
    }

    public void setPorcentajeRecargo(double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }

    public List<Cuota> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<Cuota> cuotas) {
        this.cuotas = cuotas;
    }
}