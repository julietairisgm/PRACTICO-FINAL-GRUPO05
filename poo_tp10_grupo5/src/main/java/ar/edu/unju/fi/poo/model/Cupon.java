package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cupones")
public class Cupon {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCupon;
	
	@Column(nullable = false, unique = true)
	private String codigo;
	
	@Column(nullable = false)
	private double porcentajeDescuento;
	
	@Column(nullable = false)
	private LocalDate fechaVencimiento;
	
	@Column(nullable = false)
	private boolean usado;
	
	public Cupon() {}

    public Cupon(String codigo, double porcentajeDescuento, LocalDate fechaVencimiento) {
        this.codigo = codigo;
        this.porcentajeDescuento = porcentajeDescuento;
        this.fechaVencimiento = fechaVencimiento;
        this.usado = false;
    }
	
	public boolean esValido () {
		return !usado
                && fechaVencimiento != null
                && !fechaVencimiento.isBefore(LocalDate.now())
                && porcentajeDescuento > 0
                && porcentajeDescuento <= 100;
	}
	
    public Integer getIdCupon() {
        return idCupon;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
