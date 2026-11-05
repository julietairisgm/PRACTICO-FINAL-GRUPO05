package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Producto;
import java.util.List;

public interface ProductoService {

    //Guardar / Actualizar producto
    Producto guardarProducto(Producto producto);

    //Búsqueda por ID
    Producto buscarPorId(Integer id);

    //Filtros por categoría, marca y rango de precios
    List<Producto> buscarPorCategoria(Integer idCategoria);
    List<Producto> buscarPorMarca(String marca);
    List<Producto> buscarPorRangoDePrecio(Double precioMin, Double precioMax);

    //Consultar productos con stock crítico o por debajo de un umbral
    List<Producto> buscarProductosConStockCritico();
    List<Producto> buscarProductosPorDebajoDeUmbral(Integer umbral);

    //Operaciones de disponibilidad y actualización de stock
    boolean consultarDisponibilidad(Integer idProducto, int cantidad);
    void actualizarStock(Integer idProducto, int cantidadAComprar);
}