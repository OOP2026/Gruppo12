package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sui pazienti persistiti nel database. */
public interface PazienteDAO {
	void insertPaziente(Map<String, Object> paziente);

	Map<String, Object> getPazienteById(String matricolaPaziente);

	List<Map<String, Object>> getAllPazienti();

	void updatePaziente(Map<String, Object> paziente);

	void deletePaziente(String matricolaPaziente);
}
