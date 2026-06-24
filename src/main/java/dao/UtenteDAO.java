package dao;

import model.Utente;

import java.util.List;

public interface UtenteDAO {
	void insertUtente(Utente utente);

	Utente getUtenteById(String login);

	List<Utente> getAllUtenti();

	void updateUtente(Utente utente);

	void deleteUtente(String login);

}