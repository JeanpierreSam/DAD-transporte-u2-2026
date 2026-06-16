package pe.universidadperuanaunion.reserva.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.universidadperuanaunion.reserva.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {

    List<Reserva> findByFechaReserAndCodCli(LocalDate fechaReser, String codCli);

    List<Reserva> findByIdProgAndCodDest(Integer idProg, String codDest);
}
