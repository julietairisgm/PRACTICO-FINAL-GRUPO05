package ar.edu.unju.fi.poo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import java.util.List;

@Repository
public interface VentaFinanciadaRepository extends JpaRepository<VentaFinanciada, Integer> {

    // Metodo para consultar ventas financiadas de un cliente por su ID
    List<VentaFinanciada> findByCliente_IdCliente(Integer idCliente);

    // Consulta JPQL para filtrar unicamente las ventas financiadas
    @Query("SELECT v FROM VentaFinanciada v")
    List<VentaFinanciada> findVentasFinanciadas();
}