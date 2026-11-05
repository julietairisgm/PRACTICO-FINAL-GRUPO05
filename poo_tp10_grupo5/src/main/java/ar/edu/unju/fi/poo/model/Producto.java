package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Transient
    public static final int STOCK_CRITICO = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "marca", length = 100)
    private String marca;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "precio_base", nullable = false)
    private Double precioBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    public Producto() {}

    public Producto(String nombre, String descripcion, String marca, Integer stock, Double precioBase, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.stock = stock;
        this.precioBase = precioBase;
        this.categoria = categoria;
    }

    // Métodos de lógica del UML
    public boolean tieneStockDisponible(int cantidad) {
        return this.stock != null && this.stock >= cantidad;
    }

    public void reducirStock(int cantidad) {
        if (!tieneStockDisponible(cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente para realizar la venta.");
        }
        this.stock -= cantidad;
    }

    public boolean tieneStockCritico(int umbral) {
        return this.stock != null && this.stock <= umbral;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}