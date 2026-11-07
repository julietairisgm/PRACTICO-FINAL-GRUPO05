package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.service.ProductoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductoServiceTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Debe guardar un producto correctamente en la base de datos")
    void testGuardarProductoEnBaseDeDatos() {
        // Guardar una categoría real en MySQL
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria = categoriaRepository.save(categoria);

        // Convertir la categoría a DTO
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        // Crear y guardar el producto
        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("Notebook");
        productoDto.setDescripcion("Laptop Gamer");
        productoDto.setMarca("Asus");
        productoDto.setStock(10);
        productoDto.setPrecioBase(150000.0);
        productoDto.setCategoriaDto(categoriaDto);

        ProductoDto guardado = productoService.guardarProducto(productoDto);

        // Verificaciones
        assertNotNull(guardado.getIdProducto());
        assertEquals("Notebook", guardado.getNombre());
    }

    @Test
    @DisplayName("Debe buscar un producto por su ID correctamente")
    void testBuscarPorIdExitoso() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Periféricos");
        categoria = categoriaRepository.save(categoria);
        
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("Teclado");
        productoDto.setDescripcion("Mecánico RGB");
        productoDto.setMarca("Redragon");
        productoDto.setStock(15);
        productoDto.setPrecioBase(45000.0);
        productoDto.setCategoriaDto(categoriaDto);

        ProductoDto guardado = productoService.guardarProducto(productoDto);
        ProductoDto encontrado = productoService.buscarPorId(guardado.getIdProducto());

        assertNotNull(encontrado);
        assertEquals("Teclado", encontrado.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar una excepción cuando el producto no existe")
    void testBuscarPorIdLanzaExcepcionCuandoNoExiste() {
        assertThrows(ProductoNoEncontradoException.class, () -> {
            productoService.buscarPorId(99999);
        });
    }

    @Test
    @DisplayName("Debe confirmar la disponibilidad cuando hay stock suficiente")
    void testConsultarDisponibilidadStockSuficiente() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Monitores");
        categoria = categoriaRepository.save(categoria);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("Monitor 24'");
        productoDto.setDescripcion("144Hz IPS");
        productoDto.setMarca("LG");
        productoDto.setStock(8);
        productoDto.setPrecioBase(120000.0);
        productoDto.setCategoriaDto(categoriaDto);

        ProductoDto guardado = productoService.guardarProducto(productoDto);
        boolean disponible = productoService.consultarDisponibilidad(guardado.getIdProducto(), 5);

        assertTrue(disponible);
    }

    @Test
    @DisplayName("Debe actualizar el stock correctamente en la base de datos")
    void testActualizarStockExitosoEnBD() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Accesorios");
        categoria = categoriaRepository.save(categoria);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("Mouse");
        productoDto.setDescripcion("Óptico Wireless");
        productoDto.setMarca("Logitech");
        productoDto.setStock(20);
        productoDto.setPrecioBase(15000.0);
        productoDto.setCategoriaDto(categoriaDto);

        ProductoDto guardado = productoService.guardarProducto(productoDto);
        // Se descuentan 5 unidades en MySQL
        productoService.actualizarStock(guardado.getIdProducto(), 5);

        ProductoDto actualizado = productoService.buscarPorId(guardado.getIdProducto());
        assertEquals(15, actualizado.getStock());
    }

    @Test
    @DisplayName("Debe lanzar una excepción al intentar actualizar con stock insuficiente")
    void testActualizarStockLanzaExcepcionPorStockInsuficiente() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Audio");
        categoria = categoriaRepository.save(categoria);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("Auriculares");
        productoDto.setDescripcion("Bluetooth");
        productoDto.setMarca("Sony");
        productoDto.setStock(3);
        productoDto.setPrecioBase(35000.0);
        productoDto.setCategoriaDto(categoriaDto);

        ProductoDto guardado = productoService.guardarProducto(productoDto);
        assertThrows(StockInsuficienteException.class, () -> {
            productoService.actualizarStock(guardado.getIdProducto(), 10);
        });
    }
    
    @Test
    @DisplayName("Debe encontrar los productos con stock crítico")
    void testBuscarProductosConStockCritico() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Almacenamiento");
        categoria = categoriaRepository.save(categoria);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setIdCategoria(categoria.getIdCategoria());

        // Stock por debajo del límite crítico (20 unidades)
        ProductoDto productoDto = new ProductoDto();
        productoDto.setNombre("SSD 1TB");
        productoDto.setDescripcion("NVMe M.2");
        productoDto.setMarca("Kingston");
        productoDto.setStock(2);
        productoDto.setPrecioBase(85000.0);
        productoDto.setCategoriaDto(categoriaDto);

        productoService.guardarProducto(productoDto);
        List<ProductoDto> criticos = productoService.buscarProductosConStockCritico();
        assertFalse(criticos.isEmpty());
    }
}