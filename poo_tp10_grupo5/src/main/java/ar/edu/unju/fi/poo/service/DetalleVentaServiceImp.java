package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.DetalleVentaRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleVentaServiceImp implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public DetalleVenta crearDetalle(Integer idProducto, Integer cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioBaseAplicado(producto.getPrecioBase());
        detalle.calcularSubtotal();

        return detalleVentaRepository.save(detalle);
    }

    @Override
    public double calcularSubtotal(DetalleVenta detalle) {
        return detalle.calcularSubtotal();
    }
}