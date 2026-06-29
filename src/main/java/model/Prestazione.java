package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modello base per le prestazioni sanitarie.
 * Gestisce dati comuni, relazione con i medici e formattazione testuale.
 */
public abstract class Prestazione {
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer numPrestazione;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String esito;
    private Ricovero ricoveroAssegnato;
    private List<Medico> medici;


    public Prestazione(Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito) {
        this.numPrestazione = numPrestazione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.esito = esito;
        this.medici = new ArrayList<>();
    }

    public void setRicovero(Ricovero ricovero) {
        this.ricoveroAssegnato = ricovero;
    }

    public void addMedico(Medico medico) {
        if (!this.medici.contains(medico)) {
            this.medici.add(medico);
            medico.addPrestazione(this);
        }
    }

    public boolean removeMedico(Medico medico) {
        if (this.medici.remove(medico)) {
            medico.removePrestazione(this);
            return true;
        }
        return false;
    }
    public Integer getNumPrestazione() {
        return numPrestazione;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public String getEsito() {
        return esito;
    }

    public Ricovero getRicoveroAssegnato() {
        return ricoveroAssegnato;
    }

    public List<Medico> getMedici() {
        return medici;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    protected String descrizioneTipo() {
        return "Prestazione";
    }

    @Override
    public String toString() {
        String esitoDescrizione = esito != null ? esito : "N/D";
        return "#" + numPrestazione + " - " + descrizioneTipo() + " - "
                + dataInizio.format(DISPLAY_FORMATTER) + " - esito " + esitoDescrizione;
    }
}
