package ar.edu.unju.fi.poo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.VentaFinanciadaInvalidaException;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.EstadoCuota;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.repository.CuotaRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.VentaFinanciadaRepository;

@Service
public class VentaFinanciadaServiceImp implements VentaFinanciadaService {

    private final VentaFinanciadaRepository ventaFinanciadaRepository;
    private final ProductoRepository productoRepository;
    private final CuotaRepository cuotaRepository;

    public VentaFinanciadaServiceImp(VentaFinanciadaRepository ventaFinanciadaRepository,
                                     ProductoRepository productoRepository,
                                     CuotaRepository cuotaRepository) {
        this.ventaFinanciadaRepository = ventaFinanciadaRepository;
        this.productoRepository = productoRepository;
        this.cuotaRepository = cuotaRepository;
    }

    @Override
    @Transactional
    public VentaFinanciada registrarVentaFinanciada(VentaFinanciada venta) {
        validarVentaFinanciada(venta);
        obtenerProductosYValidarDetalles(venta);
        validarStockDisponible(venta);
        
        // Calcula subtotal, recargo y total financiado
        venta.calcularTotal();
        
        // Genera las 6 cuotas mensuales consecutivas en estado ADEUDADA
        venta.generarCuotas();
        
        // Actualiza el stock de los productos vendidos
        actualizarStock(venta);

        return ventaFinanciadaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuota> consultarEstadoCuentaCuotasPendientes(Integer idCliente) {
        if (idCliente == null) {
            throw new VentaFinanciadaInvalidaException("El identificador del cliente no puede ser nulo.");
        }
        return cuotaRepository.findByClienteIdAndEstado(idCliente, EstadoCuota.ADEUDADA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuota> consultarEstadoCuentaCuotasPendientesPorIdentificacion(String numeroIdentificacion) {
        if (numeroIdentificacion == null || numeroIdentificacion.isBlank()) {
            throw new VentaFinanciadaInvalidaException("El numero de identificación del cliente es obligatorio.");
        }
        return cuotaRepository.findByClienteNumeroIdentificacionAndEstado(numeroIdentificacion, EstadoCuota.ADEUDADA);
    }

    // ------------------ Metodos Auxiliares --------------------------

    private void validarVentaFinanciada(VentaFinanciada venta) {
        if (venta == null) {
            throw new VentaFinanciadaInvalidaException("La venta no puede ser nula.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaFinanciadaInvalidaException("La venta debe tener al menos un detalle.");
        }
        if (venta.getPorcentajeRecargo() < 0) {
            throw new VentaFinanciadaInvalidaException("El recargo financiero no puede ser negativo.");
        }
        if (venta.getCliente() == null) {
            throw new VentaFinanciadaInvalidaException("El cliente es obligatorio.");
        }
    }

    private void obtenerProductosYValidarDetalles(VentaFinanciada venta) {
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
            throw new VentaFinanciadaInvalidaException("El detalle y el producto son obligatorios.");
        }
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new VentaFinanciadaInvalidaException("La cantidad debe ser mayor a cero.");
        }
        if (detalle.getPrecioBaseAplicado() == null || detalle.getPrecioBaseAplicado() < 0) {
            throw new VentaFinanciadaInvalidaException("El precio aplicado no puede ser nulo ni negativo.");
        }
        if (detalle.getProducto().getIdProducto() == null) {
            throw new VentaFinanciadaInvalidaException("El producto debe tener un identificador.");
        }
    }

    private void validarStockDisponible(VentaFinanciada venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }
    }

    private void actualizarStock(VentaFinanciada venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }
    }
}