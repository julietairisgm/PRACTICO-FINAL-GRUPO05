package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Servicio de filtros por categoría, marca y rango de precios
    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);

    List<Producto> findByMarcaContainingIgnoreCase(String marca);

    List<Producto> findByPrecioBaseBetween(Double precioMin, Double precioMax);

    // Consulta de productos con stock crítico o por debajo de un umbral
    List<Producto> findByStockLessThanEqual(Integer umbral);
}