package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ricovero {
    private LocalDateTime dataAmmissione;
    private LocalDateTime dataDimissione;
    private Paziente pazienteAssegnato;
    private Letto lettoAssegnato;
    private List<Amministratore> listaAmministratori;
    private List<Prestazione> listaPrestazioni;


    public Ricovero(LocalDateTime dataAmmissione, LocalDateTime dataDimissione){
    this.dataAmmissione = dataAmmissione;
    this.dataDimissione = dataDimissione;
    this.listaAmministratori = new ArrayList<>();
    this.listaPrestazioni = new ArrayList<>();
    }

    public void setPaziente(Paziente paziente) {
        this.pazienteAssegnato = paziente;
    }

    public void setLetto(Letto letto) {
        this.lettoAssegnato = letto;
    }

    public void addAmministratore(Amministratore nuovoAmministratore) { this.listaAmministratori.add(nuovoAmministratore);}

    public void registraNuovaVisita(Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, String tipoVisita){
        Visita nuovaVisita = new Visita(numPrestazione, dataInizio, dataFine, esito, tipoVisita);
        this.listaPrestazioni.add(nuovaVisita);
    }

    public void registraNuovoIntervento(Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, Integer salaOperatoria){
        Intervento nuovoIntervento = new Intervento(numPrestazione, dataInizio, dataFine, esito, salaOperatoria);
        this.listaPrestazioni.add(nuovoIntervento);
    }
}
