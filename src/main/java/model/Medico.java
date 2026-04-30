package model;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String matricolaMedico;
    private List<Malattia> listaMalattie;
    private Reparto reparto;
    private List<TurnoLavorativo> listaTurniLavorativi;
    private List<Prestazione> listaPrestazioni;

    public Medico(String login, String password, String matricolaMedico)
    {
        super(login, password);
        this.matricolaMedico = matricolaMedico;
        this.listaMalattie = new ArrayList<>();
        this.listaTurniLavorativi = new ArrayList<>();
        this.listaPrestazioni = new ArrayList<>();
    }
    public void addMalattia(Malattia nuovaMalattia) { this.listaMalattie.add(nuovaMalattia); }
    public void setReparto(Reparto reparto) {
        this.reparto = reparto;
    }
    public void addTurnoLavorativo(TurnoLavorativo nuovoTurnoLavorativo) { this.listaTurniLavorativi.add(nuovoTurnoLavorativo); }
    public void addPrestazione(Prestazione nuovaPrestazione) { this.listaPrestazioni.add(nuovaPrestazione); }
}

