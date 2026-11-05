package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServiceImp implements ProductoService {

    private final ProductoRepository productoRepository;

    ProductoServiceImp(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró el producto con ID: " + id));
    }

    // Servicio de filtros por categoría, marca y rango de precios
    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(Integer idCategoria) {
        return productoRepository.findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorMarca(String marca) {
        return productoRepository.findByMarcaContainingIgnoreCase(marca);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRangoDePrecio(Double precioMin, Double precioMax) {
        return productoRepository.findByPrecioBaseBetween(precioMin, precioMax);
    }

    // Consulta de productos con stock crítico o por debajo de un umbral
    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarProductosConStockCritico() {
        return productoRepository.findByStockLessThanEqual(Producto.STOCK_CRITICO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorDebajoDeUmbral(Integer umbral) {
        return productoRepository.findByStockLessThanEqual(umbral);
    }

    // Operaciones para consultar disponibilidad y actualizar stock
    @Override
    @Transactional(readOnly = true)
    public boolean consultarDisponibilidad(Integer idProducto, int cantidad) {
        Producto producto = buscarPorId(idProducto);
        return producto.tieneStockDisponible(cantidad);
    }

    @Override
    @Transactional
    public void actualizarStock(Integer idProducto, int cantidadAComprar) {
        Producto producto = buscarPorId(idProducto);

        if (!producto.tieneStockDisponible(cantidadAComprar)) {
            throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.reducirStock(cantidadAComprar);
        //productoRepository.save(producto);
    }
}