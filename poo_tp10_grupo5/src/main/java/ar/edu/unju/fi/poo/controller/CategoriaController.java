package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.exceptions.CategoriaNoEncontradaException;
import ar.edu.unju.fi.poo.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias", description = "Operaciones CRUD para la gestion de categorias de productos")
public class CategoriaController {

    private final CategoriaService service;

    CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las categorias", description = "Obtiene el listado completo de categorias registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias recuperada exitosamente")
    @GetMapping
    public List<CategoriaDto> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar categoria por ID", description = "Recupera los datos de una categoria especifica a partir de su identificador unico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "No se encontro ninguna categoria con el ID especificado")
    })
    @GetMapping("/{id}")
    public CategoriaDto buscar(
            @Parameter(description = "ID unico de la categoria", example = "1", required = true)
            @PathVariable Integer id) {
        try {
            return service.buscarPorId(id);
        } catch (CategoriaNoEncontradaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoria de productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la categoría invalidos o duplicados")
    })
    @PostMapping
    public ResponseEntity<CategoriaDto> crear(@RequestBody CategoriaDto categoria) {
        CategoriaDto nueva = service.crear(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @Operation(summary = "Actualizar una categoría", description = "Modifica los datos de una categoria existente identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "categoria actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "categoria no encontrada")
    })
    @PutMapping("/{id}")
    public CategoriaDto actualizar(
            @Parameter(description = "ID de la categoria a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @RequestBody CategoriaDto categoria) {
        return service.actualizar(id, categoria);
    }

    @Operation(summary = "Eliminar una categoria", description = "Elimina logicamente o fisicamente una categoria según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "categoria eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "categoria no encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "ID de la categoria a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        service.eliminar(id);
    }
}