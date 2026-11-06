package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

public interface VentaService {
    VentaContado registrarVentaContado(VentaContado venta);
    
    VentaFinanciada registrarVentaFinanciada(VentaFinanciada venta);
    List<Cuota> consultarEstadoCuentaCuotasPendientes(Integer idCliente);
    List<Cuota> consultarEstadoCuentaCuotasPendientesPorIdentificacion(String numeroIdentificacion);

    List<Venta> buscarHistorialPorCliente(Integer idCliente);
    List<Venta> buscarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta);
    List<Venta> buscarPorModalidad(String modalidad);
}