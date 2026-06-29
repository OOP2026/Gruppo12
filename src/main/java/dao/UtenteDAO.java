package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sugli utenti persistiti nel database. */
public interface UtenteDAO {
	void insertUtente(Map<String, Object> utente);

	Map<String, Object> getUtenteById(String login);

	List<Map<String, Object>> getAllUtenti();

	void updateUtente(Map<String, Object> utente);

	void deleteUtente(String login);
}
