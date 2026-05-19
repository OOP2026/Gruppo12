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

    public void addMedico(Medico medico) { this.medici.add(medico);}
    public void addStanza(Stanza stanza) { this.stanze.add(stanza);}

    public String getNomeReparto() {
        return nomeReparto;
    }

    public List<Medico> getMedici() {
        return medici;
    }

    public List<Stanza> getStanze() {
        return stanze;
    }
}
