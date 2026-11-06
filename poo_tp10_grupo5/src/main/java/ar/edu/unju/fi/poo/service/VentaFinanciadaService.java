package ar.edu.unju.fi.poo.service;

import java.util.List;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

public interface VentaFinanciadaService {

    VentaFinanciada registrarVentaFinanciada(VentaFinanciada venta);

    List<Cuota> consultarEstadoCuentaCuotasPendientes(Integer idCliente);

    List<Cuota> consultarEstadoCuentaCuotasPendientesPorIdentificacion(String numeroIdentificacion);
}