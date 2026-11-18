package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Gestion del catalogo de productos, precios, filtros y control de stock")
public class ProductoController {

    private final ProductoService productoService;

    ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Buscar producto por ID", description = "Obtiene el detalle de un producto a partir de su identificador unico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> buscar(
            @Parameter(description = "ID unico del producto", example = "1", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los productos", description = "Devuelve el listado completo de productos activos")
    @ApiResponse(responseCode = "200", description = "Listado de productos obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<ProductoDto>> listarProductos() {
        List<ProductoDto> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Registrar nuevo producto", description = "Crea un producto nuevo asociado a una categoria existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del producto invalidos"),
            @ApiResponse(responseCode = "404", description = "La categoria asociada no existe")
    })
    @PostMapping
    public ResponseEntity<ProductoDto> guardarProducto(@RequestBody ProductoDto productoDto) {
        ProductoDto productoGuardado = productoService.guardarProducto(productoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
    }

    @Operation(summary = "Modificar producto existente", description = "Actualiza nombre, descripcion, marca, stock, precio base y categoría de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> modificar(
            @Parameter(description = "ID del producto a modificar", example = "1", required = true)
            @PathVariable Integer id,
            @RequestBody ProductoDto producto) {
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

    @Operation(summary = "Filtrar productos por categoria", description = "Obtiene todos los productos pertenecientes a una categoria especifica")
    @ApiResponse(responseCode = "200", description = "Listado filtrado por categoria")
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDto>> buscarPorCategoria(
            @Parameter(description = "ID de la categoria", example = "1", required = true)
            @PathVariable Integer idCategoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(idCategoria));
    }

    @Operation(summary = "Filtrar productos por marca", description = "Busca productos que coincidan con la marca especificada")
    @ApiResponse(responseCode = "200", description = "Listado filtrado por marca")
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoDto>> buscarPorMarca(
            @Parameter(description = "Nombre de la marca", example = "Samsung", required = true)
            @PathVariable String marca) {
        return ResponseEntity.ok(productoService.buscarPorMarca(marca));
    }

    @Operation(summary = "Filtrar productos por rango de precio", description = "Obtiene productos cuyo precio base este entre un valor minimo y máximo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados dentro del rango"),
            @ApiResponse(responseCode = "400", description = "Rango de precios invalido")
    })
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDto>> buscarPorPrecio(
            @Parameter(description = "Precio minimo", example = "10000.0", required = true)
            @RequestParam Double min,
            @Parameter(description = "Precio maximo", example = "150000.0", required = true)
            @RequestParam Double max) {
        return ResponseEntity.ok(productoService.buscarPorRangoDePrecio(min, max));
    }

    @Operation(summary = "Consultar productos con stock crítico", description = "Devuelve productos con stock bajo. Si no se envia umbral, aplica el umbral critico por defecto")
    @ApiResponse(responseCode = "200", description = "Listado de productos con stock critico")
    @GetMapping("/stock-critico")
    public ResponseEntity<List<ProductoDto>> buscarStockCritico(
            @Parameter(description = "Umbral máximo de stock opcional", example = "5")
            @RequestParam(required = false) Integer umbral) {
        if (umbral == null) {
            return ResponseEntity.ok(productoService.buscarProductosConStockCritico());
        }
        return ResponseEntity.ok(productoService.buscarProductosPorDebajoDeUmbral(umbral));
    }

    @Operation(summary = "Consultar disponibilidad de stock", description = "Verifica si existe stock suficiente para cubrir una determinada cantidad de unidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificacion realizada (true si hay stock suficiente, false en caso contrario)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Boolean> consultarDisponibilidad(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Cantidad solicitada", example = "3", required = true)
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(productoService.consultarDisponibilidad(id, cantidad));
    }

    @Operation(summary = "Descontar/actualizar stock de un producto", description = "Reduce el stock disponible de un producto en función de la cantidad comprada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o cantidad inválida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/stock")
    public void actualizarStock(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Cantidad de unidades a descontar", example = "2", required = true)
            @RequestParam int cantidadAComprar) {
        productoService.actualizarStock(id, cantidadAComprar);
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del catalogo mediante su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}