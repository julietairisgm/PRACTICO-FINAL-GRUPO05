package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.service.ProductoService;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
	private final ProductoService productoService;

    ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

	// Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductoDto>> listarProductos() {
        List<ProductoDto> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }
    
    // Crear Producto 
    @PostMapping
    public ResponseEntity<ProductoDto> guardarProducto(@RequestBody ProductoDto productoDto) {
        ProductoDto productoGuardado = productoService.guardarProducto(productoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
    }

    // Modificar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> modificar(@PathVariable Integer id, @RequestBody ProductoDto producto) {
        ProductoDto existente = productoService.buscarPorId(id);

        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setMarca(producto.getMarca());
        existente.setStock(producto.getStock());
        existente.setPrecioBase(producto.getPrecioBase());
        existente.setCategoriaDto(producto.getCategoriaDto());

        ProductoDto actualizado = productoService.guardarProducto(existente);

        return ResponseEntity.ok(actualizado);
    }

    // Filtrar por categoría
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDto>> buscarPorCategoria(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(idCategoria));
    }

    // Filtrar por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoDto>> buscarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(productoService.buscarPorMarca(marca));
    }

    // Filtrar por rango de precio
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDto>> buscarPorPrecio(@RequestParam Double min,@RequestParam Double max) {
        return ResponseEntity.ok(productoService.buscarPorRangoDePrecio(min, max));
    }

    // Consultar productos con stock crítico o por debajo de un umbral
    @GetMapping("/stock-critico")
    public ResponseEntity<List<ProductoDto>> buscarStockCritico(@RequestParam(required = false) Integer umbral) {
        if (umbral == null) {
            return ResponseEntity.ok(productoService.buscarProductosConStockCritico());
        }

        return ResponseEntity.ok(productoService.buscarProductosPorDebajoDeUmbral(umbral));
    }
    
    // Consultar disponibilidad de stock
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Boolean> consultarDisponibilidad(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {

        return ResponseEntity.ok(
                productoService.consultarDisponibilidad(id, cantidad));
    }
    
    // Actualizar stock
    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidadAComprar) {

        productoService.actualizarStock(id, cantidadAComprar);
        return ResponseEntity.noContent().build();
    }
    
    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
