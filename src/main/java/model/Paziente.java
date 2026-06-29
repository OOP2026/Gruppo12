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
        if (!this.listaRicoveri.contains(nuovoRicovero)) {
            this.listaRicoveri.add(nuovoRicovero);
            nuovoRicovero.setPaziente(this);
        }
    }

    public boolean removeRicovero(Ricovero ricovero) {
        if (this.listaRicoveri.remove(ricovero)) {
            ricovero.setPaziente(null);
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return matricolaPaziente + " - " + nome + " " + cognome;
    }
}
