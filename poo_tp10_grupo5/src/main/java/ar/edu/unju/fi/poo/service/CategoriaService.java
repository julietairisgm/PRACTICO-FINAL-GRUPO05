package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.dto.CategoriaDto;

public interface CategoriaService {

    // Listar todas las categorías
    List<CategoriaDto> listar();

    // Buscar categoría por ID
    CategoriaDto buscarPorId(Integer id);

    // Crear categoría
    CategoriaDto crear(CategoriaDto categoriaDto);

    // Modificar categoría
    CategoriaDto actualizar(Integer id, CategoriaDto categoriaDto);

    // Eliminar categoría
    void eliminar(Integer id);
}