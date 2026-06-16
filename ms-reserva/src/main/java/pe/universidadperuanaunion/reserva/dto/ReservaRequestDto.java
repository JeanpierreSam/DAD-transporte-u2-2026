package pe.universidadperuanaunion.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ReservaRequestDto {

    private String nroReser;
    private LocalDate fechaReser;
    private LocalTime horaReser;
    private String codCli;
    private Integer idProg;
    private String codDest;
}
