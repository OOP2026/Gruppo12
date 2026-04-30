package model;

import java.util.ArrayList;
import java.util.List;

public class Letto {
    private String matricolaLetto;
    private Stanza stanza;
    private List<Ricovero> ricoveri;

    public Letto(String matricolaLetto){
        this.matricolaLetto = matricolaLetto;
        this.ricoveri = new ArrayList<>();
    }

    public void setStanza(Stanza stanza) {
        this.stanza = stanza;
    }

    public void addRicovero(Ricovero ricovero) { this.ricoveri.add(ricovero);}
}
