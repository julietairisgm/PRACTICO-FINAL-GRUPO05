package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDto {
	private Integer idCategoria;
    private String nombre;
    private List<ProductoDto> productosDto = new ArrayList<>();
    
    
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<ProductoDto> getProductosDto() {
		return productosDto;
	}
	public void setProductosDto(List<ProductoDto> productosDto) {
		this.productosDto = productosDto;
	}
}
