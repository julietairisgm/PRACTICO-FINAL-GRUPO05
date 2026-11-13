package ar.edu.unju.fi.poo.service.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.CategoriaDto;
import ar.edu.unju.fi.poo.exceptions.CategoriaNoEncontradaException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.service.CategoriaService;

import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoriaServiceImp implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImp(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Listar todas las categorías
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> listar() {

        List<Categoria> categorias = categoriaRepository.findAll();

        List<CategoriaDto> categoriasDto = new ArrayList<>();

        for (Categoria categoria : categorias) {
            categoriasDto.add(convertirADto(categoria));
        }

        return categoriasDto;
    }

    // Buscar categoría por ID
    @Override
    @Transactional(readOnly = true)
    public CategoriaDto buscarPorId(Integer id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se encontró la categoría con ID: " + id));

        return convertirADto(categoria);
    }

    // Crear categoría
    @Override
    @Transactional
    public CategoriaDto crear(CategoriaDto categoriaDto) {

        Categoria categoria = new Categoria();

        categoria.setNombre(categoriaDto.getNombre());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        return convertirADto(categoriaGuardada);
    }

    // Modificar categoría
    @Override
    @Transactional
    public CategoriaDto actualizar(Integer id, CategoriaDto categoriaDto) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se encontró la categoría con ID: " + id));

        categoria.setNombre(categoriaDto.getNombre());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);

        return convertirADto(categoriaActualizada);
    }

    // Eliminar categoría
    @Override
    @Transactional
    public void eliminar(Integer id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se encontró la categoría con ID: " + id));

        categoriaRepository.delete(categoria);
    }

    // Convertir entidad a DTO
    private CategoriaDto convertirADto(Categoria categoria) {

        CategoriaDto dto = new CategoriaDto();

        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombre());

        return dto;
    }
}

