package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.repository.CuponRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import jakarta.transaction.Transactional;
import ar.edu.unju.fi.poo.exceptions.*;
import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;

@Service
public class VentaServiceImp implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;

    public VentaServiceImp(VentaRepository ventaRepository, ProductoRepository productoRepository, CuponRepository cuponRepository){
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.cuponRepository = cuponRepository;
    }

    @Override
    @Transactional
    public VentaContado registrarVentaContado(VentaContado venta) {
        validarVenta(venta);
        validarCupon(venta);
        obtenerProductosYValidarDetalles(venta);
        validarStockDisponible(venta);
        venta.calcularTotal();
        actualizarStock(venta);
        
        if (venta.getCupon() != null) {
            venta.getCupon().setUsado(true);
            cuponRepository.save(venta.getCupon());
        }
        
        return ventaRepository.save(venta);
    }
    
    // ------------------ Metodos auxiliares --------------------------
    private void validarVenta(VentaContado venta) {
        if (venta == null) {
            throw new VentaInvalidaException("La venta no puede ser nula.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaInvalidaException("La venta debe tener al menos un detalle.");
        }
    }
    
    private void obtenerProductosYValidarDetalles(VentaContado venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            validarDetalle(detalle);
            Integer idProducto = detalle.getProducto().getIdProducto();
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new ProductoNoEncontradoException("No existe el producto con ID: " + idProducto));

            detalle.setProducto(producto);
            detalle.calcularSubtotal();
        }
    }
    
    private void validarDetalle(DetalleVenta detalle) {
        if (detalle == null || detalle.getProducto() == null) {
            throw new VentaInvalidaException("El detalle y el producto son obligatorios.");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new VentaInvalidaException("La cantidad debe ser mayor que cero.");
        }

        if (detalle.getPrecioBaseAplicado() == null || detalle.getPrecioBaseAplicado() < 0) {
            throw new VentaInvalidaException( "El precio aplicado no puede ser nulo ni negativo.");
        }

        if (detalle.getProducto().getIdProducto() == null) {
            throw new VentaInvalidaException("El producto debe tener un identificador.");
        }
    }
    
    private void validarStockDisponible(VentaContado venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }
    }
    
    private void actualizarStock(VentaContado venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }
    }
    
    private void validarCupon(VentaContado venta) {
        if (venta.getCupon() == null) { return; }

        String codigo = venta.getCupon().getCodigo();
        Cupon cupon = cuponRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new CuponInvalidoException("El cupón no existe."));

        if (!cupon.esValido()) {
            throw new CuponInvalidoException("El cupón está vencido o ya fue utilizado.");
        }

        venta.setCupon(cupon);
    }
    // ----------------------------------------------------------

    @Override
    public List<Venta> buscarHistorialPorCliente(Integer idCliente) {
    	return ventaRepository.findByCliente_IdCliente(idCliente);
    }

    @Override
    public List<Venta> buscarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null || fechaHasta == null || fechaDesde.isAfter(fechaHasta)) {
            throw new VentaInvalidaException("El rango de fechas no es válido.");
        }
        return ventaRepository.findByFechaVentaBetween(fechaDesde, fechaHasta);
    }

    @Override
    public List<Venta> buscarPorModalidad(String modalidad) {
        if (modalidad == null || modalidad.isBlank()) {
            throw new VentaInvalidaException("Debe indicar una modalidad de venta.");
        }
        if (modalidad.equalsIgnoreCase("CONTADO")) {
            return ventaRepository.findVentasContado();
        }
        throw new VentaInvalidaException("La modalidad indicada no es válida.");
    }
}