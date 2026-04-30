package model;
import java.util.ArrayList;
import java.util.List;

public class Paziente {
    private String matricolaPaziente;
    private String nome;
    private String cognome;
    private List<Ricovero> listaRicoveri;


    public Paziente(String matricolaPaziente, String nome, String cognome) {
        this.matricolaPaziente = matricolaPaziente;
        this.nome = nome;
        this.cognome = cognome;
        this.listaRicoveri = new ArrayList<>();
    }

    public void addRicovero(Ricovero nuovoRicovero) {
        this.listaRicoveri.add(nuovoRicovero);
    }

}
