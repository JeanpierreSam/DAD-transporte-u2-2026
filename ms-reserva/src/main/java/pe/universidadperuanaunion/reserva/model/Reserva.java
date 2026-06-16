package pe.universidadperuanaunion.reserva.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "reserva", indexes = {
        @Index(name = "idx_fecha_cli", columnList = "fecha_reser, cod_cli"),
        @Index(name = "idx_prog_dest", columnList = "id_prog, cod_dest")
})
public class Reserva {

    @Id
    @Column(name = "nro_reser", length = 8, nullable = false)
    private String nroReser;

    @Column(name = "fecha_reser", nullable = false)
    private LocalDate fechaReser;

    @Column(name = "hora_reser", nullable = false)
    private LocalTime horaReser;

    @Column(name = "cod_cli", length = 5, nullable = false)
    private String codCli;

    @Column(name = "id_prog", nullable = false)
    private Integer idProg;

    @Column(name = "cod_dest", length = 4, nullable = false)
    private String codDest;

    public Reserva() {
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
