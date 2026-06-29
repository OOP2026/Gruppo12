package model;

import java.time.LocalDateTime;

public class Visita extends Prestazione {
    private String tipoVisita;

    public Visita(Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, String tipoVisita){
        super(numPrestazione, dataInizio, dataFine, esito);
        this.tipoVisita = tipoVisita;
    }

    public String getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(String tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    @Override
    protected String descrizioneTipo() {
        return "Visita " + tipoVisita;
    }
}
