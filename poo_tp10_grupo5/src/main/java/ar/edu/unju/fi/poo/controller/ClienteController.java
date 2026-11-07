package ar.edu.unju.fi.poo.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listarTodos();
    }

    @GetMapping("/identificacion/{numeroIdentificacion}")
    public Cliente buscarPorIdentificacion(@PathVariable String numeroIdentificacion) {
        return service.buscarPorNumeroIdentificacion(numeroIdentificacion);
    }

    @GetMapping("/buscar")
    public List<Cliente> buscarPorNombreORazonSocial(@RequestParam String termino) {
        return service.buscarPorNombreORazonSocial(termino);
    }

    @PostMapping
    public Cliente crear(@RequestBody Cliente cliente) {
        return service.guardarCliente(cliente);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        return service.actualizarCliente(id, cliente);
    }
}