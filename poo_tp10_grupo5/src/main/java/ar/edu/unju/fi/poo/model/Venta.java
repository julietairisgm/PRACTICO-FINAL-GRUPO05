package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Venta {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVenta;
	
	@Column(nullable = false, unique = true)
	private String codigoComprobante;
	
	@Column(nullable = false)
	private LocalDate fechaVenta;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;
	
	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleVenta> detalles = new ArrayList<>();
	
	@Column(nullable = false)
	private double subtotal;
	
	@Column(nullable = false)
	private double total;
	
	public Venta() {}
	
	public Venta(String codigoComprobante, LocalDate fechaVenta, Cliente cliente) {
	    this.codigoComprobante = codigoComprobante;
	    this.fechaVenta = fechaVenta;
	    this.cliente = cliente;
	}
	
	public void agregarDetalle(DetalleVenta detalle) {
		if (detalle == null) {
	        throw new IllegalArgumentException("El detalle no puede ser nulo.");
	    }
		
	    detalles.add(detalle);
	    detalle.setVenta(this);
	}
	
	public void calcularSubtotal() {
	    this.subtotal = detalles.stream()
	            .mapToDouble(DetalleVenta::getSubtotal)
	            .sum();
	}
	
	public abstract void calcularTotal();
	
	 // Getters y Setters

    public Integer getIdVenta() {
        return idVenta;
    }

    public String getCodigoComprobante() {
        return codigoComprobante;
    }

    public void setCodigoComprobante(String codigoComprobante) {
        this.codigoComprobante = codigoComprobante;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
}
