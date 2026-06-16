package pe.universidadperuanaunion.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

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

    public ReservaResponseDto() {
    }

    public String getNroReser() {
        return nroReser;
    }

    public void setNroReser(String nroReser) {
        this.nroReser = nroReser;
    }

    public LocalDate getFechaReser() {
        return fechaReser;
    }

    public void setFechaReser(LocalDate fechaReser) {
        this.fechaReser = fechaReser;
    }

    public LocalTime getHoraReser() {
        return horaReser;
    }

    public void setHoraReser(LocalTime horaReser) {
        this.horaReser = horaReser;
    }

    public String getCodCli() {
        return codCli;
    }

    public void setCodCli(String codCli) {
        this.codCli = codCli;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Integer getIdProg() {
        return idProg;
    }

    public void setIdProg(Integer idProg) {
        this.idProg = idProg;
    }

    public String getDescripcionProgramacion() {
        return descripcionProgramacion;
    }

    public void setDescripcionProgramacion(String descripcionProgramacion) {
        this.descripcionProgramacion = descripcionProgramacion;
    }

    public String getCodDest() {
        return codDest;
    }

    public void setCodDest(String codDest) {
        this.codDest = codDest;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }

    public void setNombreDestino(String nombreDestino) {
        this.nombreDestino = nombreDestino;
    }
}
