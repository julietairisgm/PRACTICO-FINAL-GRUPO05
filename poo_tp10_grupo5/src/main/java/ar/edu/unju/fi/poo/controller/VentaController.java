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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas y Cuotas", description = "Registro de ventas al contado y financiadas, consultas de historial y cobro correlativo de cuotas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Registrar venta al contado", description = "Registra una venta de contado, aplica cupon de descuento si corresponde y descuenta el stock de los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta al contado registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos, cupón expirado/usado o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    @PostMapping("/contado")
    public ResponseEntity<VentaResponseDTO> registrarVentaContado(@RequestBody VentaContadoRequestDTO requestDTO) {
        return new ResponseEntity<>(ventaService.registrarVentaContado(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener historial de ventas de un cliente", description = "Lista todas las ventas (contado y financiadas) realizadas por un cliente.")
    @ApiResponse(responseCode = "200", description = "Historial de ventas recuperado")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerHistorialCliente(
            @Parameter(description = "ID único del cliente", example = "1", required = true)
            @PathVariable Integer idCliente) {
        return ResponseEntity.ok(ventaService.buscarHistorialPorCliente(idCliente));
    }

    @Operation(summary = "Buscar ventas por rango de fechas", description = "Filtra las ventas registradas entre una fecha de inicio y una fecha de fin (inclusive).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas encontradas en el rango"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido (fecha desde posterior a fecha hasta)")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorRangoFechas(
            @Parameter(description = "Fecha inicial (formato YYYY-MM-DD)", example = "2026-11-01", required = true)
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final (formato YYYY-MM-DD)", example = "2026-11-30", required = true)
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ventaService.buscarPorRangoFechas(desde, hasta));
    }

    @Operation(summary = "Filtrar ventas por modalidad", description = "Obtiene las ventas filtradas por modalidad de pago: CONTADO o FINANCIADO/FINANCIADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de ventas de la modalidad solicitada"),
            @ApiResponse(responseCode = "400", description = "Modalidad de venta no válida")
    })
    @GetMapping("/modalidad/{modalidad}")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorModalidad(
            @Parameter(description = "Modalidad de pago (CONTADO o FINANCIADA)", example = "CONTADO", required = true)
            @PathVariable String modalidad) {
        return ResponseEntity.ok(ventaService.buscarPorModalidad(modalidad));
    }

    @Operation(summary = "Registrar venta financiada", description = "Registra una venta financiada en 6 cuotas mensuales consecutivas aplicando el porcentaje de recargo indicado y descontando stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta financiada y plan de cuotas generados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Recargo negativo, detalles vacíos o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    @PostMapping("/financiada")
    public ResponseEntity<VentaResponseDTO> registrarVentaFinanciada(@RequestBody VentaFinanciadaRequestDTO requestDTO) {
        return new ResponseEntity<>(ventaService.registrarVentaFinanciada(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Consultar cuotas pendientes por ID de cliente", description = "Recupera el listado de cuotas adeudadas o vencidas asociadas a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de cuotas pendientes recuperado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/cuotas/pendientes/{idCliente}")
    public ResponseEntity<List<CuotaResponseDTO>> consultarCuotasPendientes(
            @Parameter(description = "ID único del cliente", example = "1", required = true)
            @PathVariable Integer idCliente) {
        return ResponseEntity.ok(ventaService.consultarEstadoCuentaCuotasPendientes(idCliente));
    }

    @Operation(summary = "Consultar cuotas pendientes por DNI/CUIT", description = "Recupera el listado de cuotas pendientes a partir del número de identificación del cliente.")
    @ApiResponse(responseCode = "200", description = "Listado de cuotas pendientes recuperado")
    @GetMapping("/cuotas/pendientes/identificacion/{dni}")
    public ResponseEntity<List<CuotaResponseDTO>> consultarCuotasPendientesPorDni(
            @Parameter(description = "Número de DNI o CUIT del cliente", example = "22334455", required = true)
            @PathVariable String dni) {
        return ResponseEntity.ok(ventaService.consultarEstadoCuentaCuotasPendientesPorIdentificacion(dni));
    }

    @Operation(summary = "Cobrar la siguiente cuota impaga de una venta", description = "Registra el pago de la primera cuota correlativa pendiente (adeudada o vencida) de una venta financiada. Al abonar la última cuota, actualiza el estado de la venta a SALDADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuota abonada exitosamente y estados actualizados"),
            @ApiResponse(responseCode = "400", description = "La venta no tiene cuotas pendientes o ya se encuentra saldada"),
            @ApiResponse(responseCode = "404", description = "No existe una venta financiada con el ID indicado")
    })
    @PostMapping("/{idVenta}/cuotas/cobrar")
    public ResponseEntity<CuotaResponseDTO> cobrarCuota(
            @Parameter(description = "ID de la venta financiada", example = "1", required = true)
            @PathVariable Integer idVenta) {
        return ResponseEntity.ok(ventaService.cobrarCuota(idVenta));
    }
}