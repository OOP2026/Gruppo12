package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sulle visite, viste come specializzazione delle prestazioni. */
public interface VisitaDAO {
	void insertVisita(Map<String, Object> visita);

	Map<String, Object> getVisitaById(int numPrestazione);

	List<Map<String, Object>> getAllVisite();

	void updateVisita(Map<String, Object> visita);

	void deleteVisita(int numPrestazione);
}
