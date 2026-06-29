package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nomeReparto;
    private List<Medico> medici;
    private List<Stanza> stanze;

    public Reparto(String nomeReparto) {
        this.nomeReparto = nomeReparto;
        this.medici = new ArrayList<>();
        this.stanze = new ArrayList<>();
    }

    public void addMedico(Medico medico) {
        if (!this.medici.contains(medico)) {
            this.medici.add(medico);
            medico.setReparto(this);
        }
    }

    public void addStanza(Stanza stanza) {
        if (!this.stanze.contains(stanza)) {
            this.stanze.add(stanza);
            stanza.setReparto(this);
        }
    }

    public boolean removeMedico(Medico medico) {
        if (this.medici.remove(medico)) {
            medico.setReparto(null);
            return true;
        }
        return false;
    }

    public boolean removeStanza(Stanza stanza) {
        if (this.stanze.remove(stanza)) {
            stanza.setReparto(null);
            return true;
        }
        return false;
    }

    public String getNomeReparto() {
        return nomeReparto;
    }

    public List<Medico> getMedici() {
        return medici;
    }

    public List<Stanza> getStanze() {
        return stanze;
    }

    @Override
    public String toString() {
        return nomeReparto;
    }
 }
