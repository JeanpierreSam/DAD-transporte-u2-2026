package pe.universidadperuanaunion.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ReservaResponseDto {

    private String nroReser;
    private LocalDate fechaReser;
    private LocalTime horaReser;
    private String codCli;
    private String nombreCliente;
    private Integer idProg;
    private String descripcionProgramacion;
    private String codDest;
    private String nombreDestino;
}
