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
}

