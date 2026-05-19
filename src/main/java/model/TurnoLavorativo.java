package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoLavorativo {
    private String idTurno;
    private LocalDateTime inizioTurno;
    private LocalDateTime fineTurno;
    private List<Medico> medici;

    public TurnoLavorativo(String idTurno, LocalDateTime inizioTurno, LocalDateTime fineTurno){
        this.idTurno = idTurno;
        this.inizioTurno = inizioTurno;
        this.fineTurno = fineTurno;
        this.medici = new ArrayList<>();
    }

    public void addMedico(Medico medico) { this.medici.add(medico);}

    public LocalDateTime getInizioTurno() {
        return inizioTurno;
    }

    public void setInizioTurno(LocalDateTime inizioTurno) {
        this.inizioTurno = inizioTurno;
    }

    public LocalDateTime getFineTurno() {
        return fineTurno;
    }

    public void setFineTurno(LocalDateTime fineTurno) {
        this.fineTurno = fineTurno;
    }

    public String getIdTurno() {
        return idTurno;
    }

    public List<Medico> getMedici() {
        return medici;
    }
}

