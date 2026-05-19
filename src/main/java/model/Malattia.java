package model;

import java.time.LocalDateTime;

public class Malattia {
    private String idMalattia;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Amministratore amministratoreAssegnato;
    private Medico medicoAssegnato;

    public Malattia(String idMalattia, LocalDateTime dataInizio, LocalDateTime dataFine){
        this.idMalattia = idMalattia;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }
    public void setMedico(Medico medicoAssegnato) {
        this.medicoAssegnato = medicoAssegnato;
    }
    public void setAmministratore(Amministratore amministratoreAssegnato){
        this.amministratoreAssegnato = amministratoreAssegnato;
    }

    public String getIdMalattia() {
        return idMalattia;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public Amministratore getAmministratoreAssegnato() {
        return amministratoreAssegnato;
    }

    public Medico getMedicoAssegnato() {
        return medicoAssegnato;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }
}

