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
import ar.edu.unju.fi.poo.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/identificacion/{numeroIdentificacion}")
    public ResponseEntity<ClienteDto> buscarPorIdentificacion(@PathVariable String numeroIdentificacion) {
        return ResponseEntity.ok(service.buscarPorNumeroIdentificacion(numeroIdentificacion));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDto>> buscarPorNombreORazonSocial(@RequestParam String termino) {
        return ResponseEntity.ok(service.buscarPorNombreORazonSocial(termino));
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(@RequestBody ClienteDto cliente) {
        ClienteDto nuevo = service.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizar(@PathVariable Integer id, @RequestBody ClienteDto cliente) {
        return ResponseEntity.ok(service.actualizarCliente(id, cliente));
    }
}
