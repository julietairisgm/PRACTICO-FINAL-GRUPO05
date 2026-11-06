package ar.edu.unju.fi.poo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Historial de ventas de un cliente
	List<Venta> findByCliente_IdCliente(Integer idCliente);

    // Ventas dentro de un rango de fechas
    List<Venta> findByFechaVentaBetween(LocalDate fechaDesde, LocalDate fechaHasta);

    @Query("SELECT v FROM Venta v WHERE TYPE(v) = VentaContado")
    List<Venta> findVentasContado();
}