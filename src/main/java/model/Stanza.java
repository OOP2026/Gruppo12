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

    public void addLetto(Letto letto) { this.letti.add(letto);}
}
