package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;

public interface VentaService {
    VentaContado registrarVentaContado(VentaContado venta);
    List<Venta> buscarHistorialPorCliente(Integer idCliente);
    List<Venta> buscarPorRangoFechas(LocalDate fechaDesde,LocalDate fechaHasta);
    List<Venta> buscarPorModalidad(String modalidad);
}
