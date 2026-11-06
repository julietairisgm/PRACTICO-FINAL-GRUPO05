package ar.edu.unju.fi.poo.dto;

public class ProductoDto {
	public static final int STOCK_CRITICO = 20;
	
	private Integer idProducto;
	private String nombre;
	private String descripcion;
	private String marca;
	private Integer stock;
	private Double precioBase;
	private CategoriaDto categoriaDto;
	
	//Getters and Setters
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
	public CategoriaDto getCategoriaDto() {
		return categoriaDto;
	}
	public void setCategoriaDto(CategoriaDto categoriaDto) {
		this.categoriaDto = categoriaDto;
	}
	public static int getStockCritico() {
		return STOCK_CRITICO;
	}
	
}
