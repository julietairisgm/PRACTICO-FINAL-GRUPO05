package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.service.ProductoService;

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
    void testGuardarProductoEnBaseDeDatos() {
        // 1. Guardar una categoría real en MySQL
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria = categoriaRepository.save(categoria);

        // 2. Crear y guardar el producto en MySQL
        Producto producto = new Producto("Notebook", "Laptop Gamer", "Asus", 10, 150000.0, categoria);
        Producto guardado = productoService.guardarProducto(producto);

        // 3. Verificaciones
        assertNotNull(guardado.getIdProducto());
        assertEquals("Notebook", guardado.getNombre());
    }

    @Test
    void testBuscarPorIdExitoso() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Periféricos");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto("Teclado", "Mecánico RGB", "Redragon", 15, 45000.0, categoria);
        Producto guardado = productoService.guardarProducto(producto);

        Producto encontrado = productoService.buscarPorId(guardado.getIdProducto());

        assertNotNull(encontrado);
        assertEquals("Teclado", encontrado.getNombre());
    }

    @Test
    void testBuscarPorIdLanzaExcepcionCuandoNoExiste() {
        assertThrows(ProductoNoEncontradoException.class, () -> {
            productoService.buscarPorId(99999);
        });
    }

    @Test
    void testConsultarDisponibilidadStockSuficiente() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Monitores");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto("Monitor 24'", "144Hz IPS", "LG", 8, 120000.0, categoria);
        Producto guardado = productoService.guardarProducto(producto);

        boolean disponible = productoService.consultarDisponibilidad(guardado.getIdProducto(), 5);

        assertTrue(disponible);
    }

    @Test
    void testActualizarStockExitosoEnBD() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Accesorios");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto("Mouse", "Óptico Wireless", "Logitech", 20, 15000.0, categoria);
        Producto guardado = productoService.guardarProducto(producto);

        // Se descuentan 5 unidades en MySQL
        productoService.actualizarStock(guardado.getIdProducto(), 5);

        Producto actualizado = productoService.buscarPorId(guardado.getIdProducto());
        assertEquals(15, actualizado.getStock());
    }

    @Test
    void testActualizarStockLanzaExcepcionPorStockInsuficiente() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Audio");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto("Auriculares", "Bluetooth", "Sony", 3, 35000.0, categoria);
        Producto guardado = productoService.guardarProducto(producto);

        assertThrows(StockInsuficienteException.class, () -> {
            productoService.actualizarStock(guardado.getIdProducto(), 10);
        });
    }

    @Test
    void testBuscarProductosConStockCritico() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Almacenamiento");
        categoria = categoriaRepository.save(categoria);

        // Stock por debajo del límite crítico (ej. 3 unidades)
        Producto producto = new Producto("SSD 1TB", "NVMe M.2", "Kingston", 2, 85000.0, categoria);
        productoService.guardarProducto(producto);

        List<Producto> criticos = productoService.buscarProductosConStockCritico();

        assertFalse(criticos.isEmpty());
    }
}