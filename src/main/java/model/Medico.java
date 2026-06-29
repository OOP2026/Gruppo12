package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un medico del sistema.
 * Mantiene reparto, malattie, turni e prestazioni assegnate.
 */
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

    public void addMalattia(Malattia nuovaMalattia) {
        this.listaMalattie.add(nuovaMalattia);
        nuovaMalattia.setMedico(this);
    }

    public boolean removeMalattia(Malattia malattia) {
        if (this.listaMalattie.remove(malattia)) {
            malattia.setMedico(null);
            return true;
        }
        return false;
    }

    public void setReparto(Reparto reparto) {
        this.reparto = reparto;
    }

    public Reparto getReparto() {
        return this.reparto;
    }

    public void addTurnoLavorativo(TurnoLavorativo turno) {
    if(!this.listaTurniLavorativi.contains(turno)) {
        this.listaTurniLavorativi.add(turno);
        turno.addMedico(this);
    }
}

      public boolean removeTurnoLavorativo(TurnoLavorativo turnoLavorativo) {
        if (this.listaTurniLavorativi.remove(turnoLavorativo)) {
            turnoLavorativo.removeMedico(this); 
            return true;
        }
        return false;
    }

    public void addPrestazione(Prestazione nuovaPrestazione) {
        if (!this.listaPrestazioni.contains(nuovaPrestazione)) {
            this.listaPrestazioni.add(nuovaPrestazione);
            nuovaPrestazione.addMedico(this);
        }
    }

    public boolean removePrestazione(Prestazione prestazione) {
        if (this.listaPrestazioni.remove(prestazione)) {
            prestazione.getMedici().remove(this);
            return true;
        }
        return false;
    }

    public List<Prestazione> getListaPrestazioni() {
        return listaPrestazioni;
    }

    public List<TurnoLavorativo> getListaTurniLavorativi() {
        return listaTurniLavorativi;
    }

    public List<Malattia> getListaMalattie() {
        return listaMalattie;
    }

    public String getMatricolaMedico() {
        return matricolaMedico;
    }

    public void setMatricolaMedico(String matricolaMedico) {
        this.matricolaMedico = matricolaMedico;
    }

    @Override
    public String toString() {
        String repartoNome = reparto != null ? reparto.getNomeReparto() : "?";
        return matricolaMedico + " - " + getLogin() + " - reparto " + repartoNome;
    }
}
