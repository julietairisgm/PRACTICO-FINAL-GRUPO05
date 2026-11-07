package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.exceptions.CategoriaNoEncontradaException;
import ar.edu.unju.fi.poo.service.CategoriaService;


@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService service;

    CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // Listar todas las categorías
    @GetMapping
    public List<CategoriaDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CategoriaDto buscar(@PathVariable Integer id) {
    	 try {
    		 return service.buscarPorId(id);
    	 } catch (CategoriaNoEncontradaException e) {
    	     throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    	 }
    }

    @PostMapping
    public ResponseEntity<CategoriaDto> crear(@RequestBody CategoriaDto categoria) {
        CategoriaDto nueva = service.crear(categoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public CategoriaDto actualizar(@PathVariable Integer id, @RequestBody CategoriaDto categoria) {
        return service.actualizar(id, categoria);
    }

    // Eliminar categoría
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
