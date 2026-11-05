package ar.edu.unju.fi.poo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Cupon;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Integer> {

	@Query("SELECT c FROM Cupon c WHERE c.codigo = :codigo")
	Optional<Cupon> buscarPorCodigo(@Param("codigo") String codigo);

    boolean existsByCodigo(String codigo);

}