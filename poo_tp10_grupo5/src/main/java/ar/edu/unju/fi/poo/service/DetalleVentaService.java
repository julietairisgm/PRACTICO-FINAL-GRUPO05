package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.DetalleVenta;

public interface DetalleVentaService {
    DetalleVenta crearDetalle(Integer idProducto, Integer cantidad);
    double calcularSubtotal(DetalleVenta detalle);
}