package ar.edu.unju.fi.poo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.CuotaResponseDTO;
import ar.edu.unju.fi.poo.dto.VentaContadoRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaFinanciadaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // 1. POST (Registro de venta al contado)
    @PostMapping("/contado")
    public ResponseEntity<VentaResponseDTO> registrarVentaContado(@RequestBody VentaContadoRequestDTO requestDTO) {
        return new ResponseEntity<>(ventaService.registrarVentaContado(requestDTO), HttpStatus.CREATED);
    }

    // 2. GET (Historial de ventas de un cliente)
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerHistorialCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(ventaService.buscarHistorialPorCliente(idCliente));
    }

    // 3. GET (Consultar por rango de fechas)
    @GetMapping("/fechas")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorRangoFechas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ventaService.buscarPorRangoFechas(desde, hasta));
    }

    // 4. GET (Filtrar por modalidad: CONTADO o FINANCIADA)
    @GetMapping("/modalidad/{modalidad}")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorModalidad(@PathVariable String modalidad) {
        return ResponseEntity.ok(ventaService.buscarPorModalidad(modalidad));
    }

    // 5. POST (Registro de venta financiada)
    @PostMapping("/financiada")
    public ResponseEntity<VentaResponseDTO> registrarVentaFinanciada(@RequestBody VentaFinanciadaRequestDTO requestDTO) {
        return new ResponseEntity<>(ventaService.registrarVentaFinanciada(requestDTO), HttpStatus.CREATED);
    }

    // 6. GET (Consultar estado de cuenta de cuotas pendientes por ID de cliente)
    @GetMapping("/cuotas/pendientes/{idCliente}")
    public ResponseEntity<List<CuotaResponseDTO>> consultarCuotasPendientes(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(ventaService.consultarEstadoCuentaCuotasPendientes(idCliente));
    }

    // 7. GET (Consultar estado de cuenta por DNI/CUIT de cliente)
    @GetMapping("/cuotas/pendientes/identificacion/{dni}")
    public ResponseEntity<List<CuotaResponseDTO>> consultarCuotasPendientesPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(ventaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(dni));
    }
    // 8. POST (Cobrar la siguiente cuota impaga de una venta financiada)
    @PostMapping("/{idVenta}/cuotas/cobrar")
    public ResponseEntity<CuotaResponseDTO> cobrarCuota(@PathVariable Integer idVenta) {
        return ResponseEntity.ok(ventaService.cobrarCuota(idVenta));
    }

}