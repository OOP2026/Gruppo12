package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un letto ospedaliero.
 * Tiene traccia della stanza di appartenenza e dei ricoveri collegati.
 */
public class Letto {
    private String matricolaLetto;
    private Stanza stanza;
    private List<Ricovero> ricoveri;

    public Letto(String matricolaLetto){
        this.matricolaLetto = matricolaLetto;
        this.ricoveri = new ArrayList<>();
    }

    public List<Ricovero> getRicoveri() {
        return ricoveri;
    }

    public Stanza getStanza() {
        return stanza;
    }

    public String getMatricolaLetto() {
        return matricolaLetto;
    }

    public void setStanza(Stanza stanza) {
        this.stanza = stanza;
    }

    public void addRicovero(Ricovero ricovero) {
        if (!this.ricoveri.contains(ricovero)) {
            this.ricoveri.add(ricovero);
            ricovero.setLetto(this);
        }
    }

    public boolean removeRicovero(Ricovero ricovero) {
        if (this.ricoveri.remove(ricovero)) {
            ricovero.setLetto(null);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String stanzaNumero = stanza != null ? String.valueOf(stanza.getNumeroStanza()) : "?";
        return "Letto " + matricolaLetto + " - stanza " + stanzaNumero;
    }
}
