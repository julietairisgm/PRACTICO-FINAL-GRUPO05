package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNumeroIdentificacion(String numeroIdentificacion);

    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Cliente> buscarPorNombreORazonSocial(@Param("termino") String termino);
}