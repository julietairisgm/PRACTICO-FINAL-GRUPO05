package ar.edu.unju.fi.poo.dto;

public class DetalleVentaRequestDTO {
    private Integer idProducto;
    private Integer cantidad;

    public DetalleVentaRequestDTO() {}

    public DetalleVentaRequestDTO(Integer idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
