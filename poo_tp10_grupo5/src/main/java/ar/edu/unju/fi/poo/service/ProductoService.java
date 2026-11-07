package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import java.util.List;

public interface ProductoService {
	
	// Listar todos los productos
    List<ProductoDto> listarProductos();
	
    //Guardar / Actualizar producto
    ProductoDto guardarProducto(ProductoDto productoDto);
    
    // Eliminar producto
    void eliminarProducto(Integer id);

    //Búsqueda por ID
    ProductoDto buscarPorId(Integer id);

    //Filtros por categoría, marca y rango de precios
    List<ProductoDto> buscarPorCategoria(Integer idCategoria);
    List<ProductoDto> buscarPorMarca(String marca);
    List<ProductoDto> buscarPorRangoDePrecio(Double precioMin, Double precioMax);

    //Consultar productos con stock crítico o por debajo de un umbral
    List<ProductoDto> buscarProductosConStockCritico();
    List<ProductoDto> buscarProductosPorDebajoDeUmbral(Integer umbral);

    //Operaciones de disponibilidad y actualización de stock
    boolean consultarDisponibilidad(Integer idProducto, int cantidad);
    void actualizarStock(Integer idProducto, int cantidadAComprar);
}