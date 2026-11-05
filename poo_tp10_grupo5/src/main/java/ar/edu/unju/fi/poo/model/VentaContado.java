package ar.edu.unju.fi.poo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas_contado")
public class VentaContado extends Venta {

    @OneToOne
    @JoinColumn(name = "id_cupon", unique = true)
    private Cupon cupon;

    public VentaContado() {
        super();
    }

    public Cupon getCupon() {
        return cupon;
    }
    
    public double calcularDescuento() {
    	if (cupon == null || !cupon.esValido()) {
            return 0.0;
        }

        return getSubtotal() * cupon.getPorcentajeDescuento() / 100;
    }

    public void aplicarCupon(Cupon cupon) {
    	if (cupon == null || !cupon.esValido()) {
            throw new IllegalArgumentException("El cupón no es válido.");
        }

        this.cupon = cupon;
    }

    @Override
    public void calcularTotal() {
    	calcularSubtotal();
    	setTotal(getSubtotal() - calcularDescuento());
    }
} 