package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.CategoriaNoEncontradaException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoServiceImp implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    ProductoServiceImp(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> listarProductos() {
        return convertirListaADto(productoRepository.findAll());
    }
    
    @Override
    @Transactional
    public ProductoDto guardarProducto(ProductoDto dto) {
    	 Producto producto = convertirAEntidad(dto);
    	 Categoria categoria = categoriaRepository.findById(dto.getCategoriaDto().getIdCategoria())
    			 .orElseThrow(() -> new CategoriaNoEncontradaException("No se encontró la categoría con ID: " + dto.getCategoriaDto().getIdCategoria()));
    	 
    	 producto.setCategoria(categoria);
    	 
    	 Producto productoGuardado = productoRepository.save(producto);
    	 
    	 return convertirADto(productoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto buscarPorId(Integer id) {
    	Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró el producto con ID: " + id));

        return convertirADto(producto);
    }

    // Servicio de filtros por categoría, marca y rango de precios
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorCategoria(Integer idCategoria) {
        return convertirListaADto(productoRepository.findByCategoriaIdCategoria(idCategoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorMarca(String marca) {
    	return convertirListaADto(productoRepository.findByMarcaContainingIgnoreCase(marca));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorRangoDePrecio(Double precioMin, Double precioMax) {
        return convertirListaADto(productoRepository.findByPrecioBaseBetween(precioMin, precioMax));
    }

    // Consulta de productos con stock crítico o por debajo de un umbral
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarProductosConStockCritico() {
    	return convertirListaADto(
                productoRepository.findByStockLessThanEqual(Producto.STOCK_CRITICO));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarProductosPorDebajoDeUmbral(Integer umbral) {
    	return convertirListaADto(productoRepository.findByStockLessThanEqual(umbral));
    }

    // Operaciones para consultar disponibilidad y actualizar stock
    @Override
    @Transactional(readOnly = true)
    public boolean consultarDisponibilidad(Integer idProducto, int cantidad) {
    	Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró el producto con ID: " + idProducto));

        return producto.tieneStockDisponible(cantidad);
    }

    @Override
    @Transactional
    public void actualizarStock(Integer idProducto, int cantidadAComprar) {
    	Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró el producto con ID: " + idProducto));

        if (!producto.tieneStockDisponible(cantidadAComprar)) {
            throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.reducirStock(cantidadAComprar);
    }
    
    @Override
    @Transactional
    public void eliminarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "No se encontró el producto con ID: " + id));

        productoRepository.delete(producto);
    }
    
    
    // Metodos auxiliares para la conversion a DTO y a ENTIDAD
    private ProductoDto convertirADto(Producto producto) {
        ProductoDto dto = new ProductoDto();
       
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setMarca(producto.getMarca());
        dto.setPrecioBase(producto.getPrecioBase());
        dto.setStock(producto.getStock());
        
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(producto.getCategoria().getIdCategoria());
        categoriaDto.setNombre(producto.getCategoria().getNombre());
        dto.setCategoriaDto(categoriaDto);

        return dto;
    }
    
    private Producto convertirAEntidad(ProductoDto dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setStock(dto.getStock());

        return producto;
    }
    
    private List<ProductoDto> convertirListaADto(List<Producto> productos) {
        List<ProductoDto> dtos = new ArrayList<>();

        for (Producto producto : productos) {
            dtos.add(convertirADto(producto));
        }
        return dtos;
    }
}