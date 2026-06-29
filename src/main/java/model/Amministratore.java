package model;

import java.util.ArrayList;
import java.util.List;

public class Amministratore extends Utente {
    private String matricolaAmministratore;
    private List<Ricovero> listaRicoveri;
    private List<Malattia> listaMalattie;
    private List<Reparto> listaReparto ;

    public Amministratore(String login, String password, String matricolaAmministratore )
    {
        super(login, password);
        this.matricolaAmministratore = matricolaAmministratore;
        this.listaRicoveri = new ArrayList<>();
        this.listaMalattie = new ArrayList<>();
    }

    public void addRicovero(Ricovero nuovoRicovero) {
        if (!this.listaRicoveri.contains(nuovoRicovero)) {
            this.listaRicoveri.add(nuovoRicovero);
            nuovoRicovero.addAmministratore(this);
        }
    }

    public void addMalattia(Malattia nuovaMalattia) {
        if (!this.listaMalattie.contains(nuovaMalattia)) {
            this.listaMalattie.add(nuovaMalattia);
            nuovaMalattia.setAmministratore(this);
        }
    }

    public boolean removeRicovero(Ricovero ricovero) {
        if (this.listaRicoveri.remove(ricovero)) {
            ricovero.removeAmministratore(this);
            return true;
        }
        return false;
    }

    public boolean removeMalattia(Malattia malattia) {
        if (this.listaMalattie.remove(malattia)) {
            malattia.setAmministratore(null);
            return true;
        }
        return false;
    }

    public String getMatricolaAmministratore() {
        return matricolaAmministratore;
    }

    public List<Ricovero> getListaRicoveri() {
        return listaRicoveri;
    }

    public List<Malattia> getListaMalattie() {
        return listaMalattie;
    }

    @Override
    public String toString() {
        return matricolaAmministratore + " - " + getLogin();
    }
}
