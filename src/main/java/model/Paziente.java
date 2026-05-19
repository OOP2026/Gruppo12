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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMatricolaPaziente() {
        return matricolaPaziente;
    }

    public List<Ricovero> getListaRicoveri() {
        return listaRicoveri;
    }
}
