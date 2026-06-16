package pe.universidadperuanaunion.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaRequestDto {

    private String nroReser;
    private LocalDate fechaReser;
    private LocalTime horaReser;
    private String codCli;
    private Integer idProg;
    private String codDest;

    public ReservaRequestDto() {
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

    public Integer getIdProg() {
        return idProg;
    }

    public void setIdProg(Integer idProg) {
        this.idProg = idProg;
    }

    public String getCodDest() {
        return codDest;
    }

    public void setCodDest(String codDest) {
        this.codDest = codDest;
    }
}
