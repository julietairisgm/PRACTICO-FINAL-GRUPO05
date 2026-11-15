package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.dto.CuotaResponseDTO;
import ar.edu.unju.fi.poo.dto.VentaContadoRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaFinanciadaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;

public interface VentaService {
    VentaResponseDTO registrarVentaContado(VentaContadoRequestDTO requestDTO);
    VentaResponseDTO registrarVentaFinanciada(VentaFinanciadaRequestDTO requestDTO);

    List<CuotaResponseDTO> consultarEstadoCuentaCuotasPendientes(Integer idCliente);
    List<CuotaResponseDTO> consultarEstadoCuentaCuotasPendientesPorIdentificacion(String numeroIdentificacion);

    List<VentaResponseDTO> buscarHistorialPorCliente(Integer idCliente);
    List<VentaResponseDTO> buscarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta);
    List<VentaResponseDTO> buscarPorModalidad(String modalidad);
}