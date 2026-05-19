package model;

import java.util.ArrayList;
import java.util.List;

public class Amministratore extends Utente {
    private String matricolaAmministratore;
    private List<Ricovero> listaRicoveri;
    private List<Malattia> listaMalattie;

    public Amministratore(String login, String password, String matricolaAmministratore )
    {
        super(login, password);
        this.matricolaAmministratore = matricolaAmministratore;
        this.listaRicoveri = new ArrayList<>();
        this.listaMalattie = new ArrayList<>();
    }

    public void addRicovero(Ricovero nuovoRicovero) { this.listaRicoveri.add(nuovoRicovero);}
    public void addMalattia(Malattia nuovaMalattia) { this.listaMalattie.add(nuovaMalattia);}

    public String getMatricolaAmministratore() {
        return matricolaAmministratore;
    }

    public List<Ricovero> getListaRicoveri() {
        return listaRicoveri;
    }

    public List<Malattia> getListaMalattie() {
        return listaMalattie;
    }
}
