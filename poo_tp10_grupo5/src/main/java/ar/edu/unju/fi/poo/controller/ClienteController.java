package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDTO;
import ar.edu.unju.fi.poo.service.ClienteService;
import ar.edu.unju.fi.poo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Gestion de clientes particulares/empresas y consulta de estado de cuenta")
public class ClienteController {

    private final ClienteService service;
    private final VentaService ventaService;

    public ClienteController(ClienteService service, VentaService ventaService) {
        this.service = service;
        this.ventaService = ventaService;
    }

    @Operation(summary = "Listar todos los clientes", description = "Obtiene el listado completo de clientes registrados.")
    @ApiResponse(responseCode = "200", description = "Listado de clientes recuperado exitosamente")
    @GetMapping
    public ResponseEntity<List<ClienteDto>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar cliente por DNI o CUIT", description = "Recupera un cliente a partir de su numero de identificación unico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese numero de identificacion")
    })
    @GetMapping("/identificacion/{numeroIdentificacion}")
    public ResponseEntity<ClienteDto> buscarPorIdentificacion(
            @Parameter(description = "DNI o CUIT del cliente", example = "22334455", required = true)
            @PathVariable String numeroIdentificacion) {
        return ResponseEntity.ok(service.buscarPorNumeroIdentificacion(numeroIdentificacion));
    }

    @Operation(summary = "Buscar clientes por nombre o razón social", description = "Filtra clientes cuyo nombre, apellido o razon social coincida con el termino de busqueda")
    @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda")
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDto>> buscarPorNombreORazonSocial(
            @Parameter(description = "Texto a buscar en nombre, apellido o razon social", example = "Lopez", required = true)
            @RequestParam String termino) {
        return ResponseEntity.ok(service.buscarPorNombreORazonSocial(termino));
    }

    @Operation(summary = "Consultar estado de cuenta del cliente", description = "Recupera la informacion detallada del cliente, incluyendo su historial de ventas registradas y el estado de sus cuotas pendientes o vencidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de cuenta recuperado correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontro el cliente especificado")
    })
    @GetMapping("/{id}/estado-cuenta")
    public ResponseEntity<EstadoCuentaResponseDTO> obtenerEstadoCuenta(
            @Parameter(description = "ID único del cliente", example = "1", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.consultarEstadoCuentaCliente(id));
    }

    @Operation(summary = "Registrar nuevo cliente", description = "Da de alta un nuevo cliente (PARTICULAR o EMPRESA) validando que su identificación no este duplicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o número de identificacion ya existente")
    })
    @PostMapping
    public ResponseEntity<ClienteDto> crear(@RequestBody ClienteDto cliente) {
        ClienteDto nuevo = service.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar datos de un cliente", description = "Modifica la informacion de un cliente existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizar(
            @Parameter(description = "ID único del cliente", example = "1", required = true)
            @PathVariable Integer id,
            @RequestBody ClienteDto cliente) {
        return ResponseEntity.ok(service.actualizarCliente(id, cliente));
    }
}