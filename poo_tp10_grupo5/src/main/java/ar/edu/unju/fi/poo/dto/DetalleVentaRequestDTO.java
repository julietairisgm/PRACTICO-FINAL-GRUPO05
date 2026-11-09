package ar.edu.unju.fi.poo.dto;

public class DetalleVentaRequestDTO {
    private Long idProducto;
    private Integer cantidad;

    public DetalleVentaRequestDTO() {}

    public DetalleVentaRequestDTO(Long idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
