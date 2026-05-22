package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private Integer numeroStanza;
    private Reparto reparto;
    private List<Letto> letti;

    public Stanza(Integer numeroStanza)
    {
        this.numeroStanza = numeroStanza;
        this.letti = new ArrayList<>();

    }

    public void setReparto(Reparto reparto) {
        this.reparto = reparto;
    }

    public void addLetto(Letto letto) {
        if (!this.letti.contains(letto)) {
            this.letti.add(letto);
            letto.setStanza(this);
        }
    }

    public boolean removeLetto(Letto letto) {
        if (this.letti.remove(letto)) {
            letto.setStanza(null);
            return true;
        }
        return false;
    }

    public Integer getNumeroStanza() {
        return numeroStanza;
    }

    public Reparto getReparto() {
        return reparto;
    }

    public List<Letto> getLetti() {
        return letti;
    }
}
