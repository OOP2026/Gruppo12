package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sugli interventi, viste come specializzazione delle prestazioni. */
public interface InterventoDAO {
	void insertIntervento(Map<String, Object> intervento);

	Map<String, Object> getInterventoById(int numPrestazione);

	List<Map<String, Object>> getAllInterventi();

	void updateIntervento(Map<String, Object> intervento);

	void deleteIntervento(int numPrestazione);
}
