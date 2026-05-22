package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ricovero {
    private String codiceRicovero;
    private LocalDateTime dataAmmissione;
    private LocalDateTime dataDimissione;
    private Paziente pazienteAssegnato;
    private Letto lettoAssegnato;
    private List<Amministratore> listaAmministratori;
    private List<Prestazione> listaPrestazioni;


    public Ricovero(LocalDateTime dataAmmissione, LocalDateTime dataDimissione, String codiceRicovero){
    this.dataAmmissione = dataAmmissione;
    this.dataDimissione = dataDimissione;
    this.listaAmministratori = new ArrayList<>();
    this.listaPrestazioni = new ArrayList<>();
    this.codiceRicovero = codiceRicovero;
    }

    public void setPaziente(Paziente paziente) {
        this.pazienteAssegnato = paziente;
    }

    public void setLetto(Letto letto) {
        this.lettoAssegnato = letto;
    }

    public void addAmministratore(Amministratore nuovoAmministratore) { this.listaAmministratori.add(nuovoAmministratore);}

    public boolean removeAmministratore(Amministratore amministratore) {
        return this.listaAmministratori.remove(amministratore);
    }

    public void addPrestazione(Prestazione prestazione) {
    this.listaPrestazioni.add(prestazione);
    prestazione.setRicovero(this); // Bidirezionalità!
}

    public boolean removePrestazione(Prestazione prestazione) {
        if (this.listaPrestazioni.remove(prestazione)) {
            prestazione.setRicovero(null);
            return true;
        }
        return false;
    }

    public LocalDateTime getDataAmmissione() {
        return dataAmmissione;
    }

    public LocalDateTime getDataDimissione() {
        return dataDimissione;
    }

    public Paziente getPazienteAssegnato() {
        return pazienteAssegnato;
    }

    public Letto getLettoAssegnato() {
        return lettoAssegnato;
    }

    public List<Amministratore> getListaAmministratori() {
        return listaAmministratori;
    }

    public List<Prestazione> getListaPrestazioni() {
        return listaPrestazioni;
    }

    public void setDataDimissione(LocalDateTime dataDimissione) {
        this.dataDimissione = dataDimissione;
    }

    public String getCodiceRicovero() {
        return codiceRicovero;
    }

    public void setCodiceRicovero(String codiceRicovero) {
        this.codiceRicovero = codiceRicovero;
    }
}
