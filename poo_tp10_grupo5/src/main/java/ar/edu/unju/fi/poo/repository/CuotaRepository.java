package ar.edu.unju.fi.poo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.EstadoCuota;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    // Consulta el estado de cuenta de cuotas pendientes por ID de cliente
    @Query("SELECT c FROM Cuota c WHERE c.ventaFinanciada.cliente.idCliente = :idCliente AND c.estado = :estado")
    List<Cuota> findByClienteIdAndEstado(@Param("idCliente") Integer idCliente, @Param("estado") EstadoCuota estado);

    // Consulta el estado de cuenta de cuotas pendientes por numero de identificación 
    @Query("SELECT c FROM Cuota c WHERE c.ventaFinanciada.cliente.numeroIdentificacion = :numeroIdentificacion AND c.estado = :estado")
    List<Cuota> findByClienteNumeroIdentificacionAndEstado(@Param("numeroIdentificacion") String numeroIdentificacion, @Param("estado") EstadoCuota estado);
}