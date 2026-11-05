package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.service.DetalleVentaService;
import ar.edu.unju.fi.poo.service.ProductoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(false)
public class DetalleVentaServiceTest {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void testCrearDetalleYGuardarEnBaseDeDatos() {
    	
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrodomésticos");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto("Cafetera", "Expreso", "Oster", 10, 25000.0, categoria);
        Producto productoGuardado = productoService.guardarProducto(producto);

        DetalleVenta detalleGuardado = detalleVentaService.crearDetalle(productoGuardado.getIdProducto(), 2);

        assertNotNull(detalleGuardado.getIdDetalle());
        assertEquals(50000.0, detalleGuardado.getSubtotal());
    }
}