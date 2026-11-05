package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_base_aplicado", nullable = false)
    private Double precioBaseAplicado;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    public DetalleVenta() {}

    public DetalleVenta(Integer cantidad, Double precioBaseAplicado, Producto producto) {
        this.cantidad = cantidad;
        this.precioBaseAplicado = precioBaseAplicado;
        this.producto = producto;
        this.subtotal = calcularSubtotal();
    }

    public double calcularSubtotal() {
        if (this.cantidad != null && this.precioBaseAplicado != null) {
            this.subtotal = this.cantidad * this.precioBaseAplicado;
            return this.subtotal;
        }
        return 0.0;
    }

    // Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public Double getPrecioBaseAplicado() {
        return precioBaseAplicado;
    }

    public void setPrecioBaseAplicado(Double precioBaseAplicado) {
        if (precioBaseAplicado == null || precioBaseAplicado < 0) {
            throw new IllegalArgumentException("El precio base aplicado no puede ser negativo ni nulo.");
        }

        this.precioBaseAplicado = precioBaseAplicado;
        calcularSubtotal();
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
}