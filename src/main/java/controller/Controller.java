package controller;

import model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Controller {
	private List<Paziente> listaPazienti = new ArrayList<>();
	private List<Amministratore> listaAmministratori = new ArrayList<>();
	private List<Utente> listaUtenti = new ArrayList<>();
	private List<Intervento> listaInterventi = new ArrayList<>();
	private List<Letto> listaLetti = new ArrayList<>();
	private List<Ricovero> listaRicoveri = new ArrayList<>();
	private List<Prestazione> listaPrestazioni = new ArrayList<>();
	private List<Visita> listaVisite = new ArrayList<>();
	private List<Medico> listaMedici = new ArrayList<>();
	private List<Reparto> listaReparti = new ArrayList<>();
	private List<Stanza> listaStanze = new ArrayList<>();
	private List<Malattia> listaMalattie = new ArrayList<>();
	private List<TurnoLavorativo> listaTurniLavorativi = new ArrayList<>();


	public Controller() {
	}

	public boolean assegnaMalattiaMedico(String matricolaMedico, String idMalattia, LocalDateTime dataInizio, LocalDateTime dataFine) {
		if (matricolaMedico == null) {
			return false;
		}

		for (Medico m : listaMedici) {
			if (matricolaMedico.equals(m.getMatricolaMedico())) {
				Malattia malattia = new Malattia(idMalattia, dataInizio, dataFine);
				malattia.setMedico(m);
				m.addMalattia(malattia);
				listaMalattie.add(malattia);

				return true;
			}
		}
		return false;
	}

	public boolean aggiungiPaziente(String matricolaPaziente, String nome, String cognome) {

		for (Paziente p : listaPazienti) {
			if (p.getMatricolaPaziente().equals(matricolaPaziente)) {
				return false;
			}
		}

		Paziente p = new Paziente(matricolaPaziente, nome, cognome);
		listaPazienti.add(p);
		return true;
	}

	public Utente effettuaLogin(String login, String password) {
		if (login == null || password == null ) {
			return null;
		}

		if (listaUtenti == null) {
			return null;
		}

		for (Utente utenteLoggato : listaUtenti) {
			if (utenteLoggato != null && utenteLoggato.login(login, password)) {
				return utenteLoggato;
			}
		}
		return null;

	}

	public boolean aggiungiRicovero(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {
		for (Ricovero r : listaRicoveri) {
			if(r.getCodiceRicovero().equals(codiceRicovero)){
				return false;
			}
		}

		Paziente pazienteTrovato = null;
		Letto lettoTrovato = null;

		for(Paziente paziente : listaPazienti) {
			if(paziente.getMatricolaPaziente().equals(matricolaPaziente)) pazienteTrovato = paziente;
		}
		for(Letto l : listaLetti) {
			if(l.getMatricolaLetto().equals(matricolaLetto)) lettoTrovato = l;
		}

		if(pazienteTrovato == null || lettoTrovato == null) {
			return false;
		}


		Ricovero r = new Ricovero(LocalDateTime.now(), null, codiceRicovero);
		r.setPaziente(pazienteTrovato);
		r.setLetto(lettoTrovato);
		pazienteTrovato.addRicovero(r);
		lettoTrovato.addRicovero(r);

		listaRicoveri.add(r);
		return true;
	}

	public boolean aggiungiDimissione(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {

		if (codiceRicovero == null) {
			return false;
		}

		for (Ricovero r : listaRicoveri) {
			if (r.getCodiceRicovero().equals(codiceRicovero)) {


				if (r.getPazienteAssegnato().getMatricolaPaziente().equals(matricolaPaziente) &&
						r.getLettoAssegnato().getMatricolaLetto().equals(matricolaLetto)) {


					if (r.getDataDimissione() == null) {


						r.setDataDimissione(LocalDateTime.now());

						return true;
					} else {
						return false;
					}
				}
			}
		}

		return false;
	}

}
