package dao;

import java.util.List;
import java.util.Map;

public interface VisitaDAO {
	void insertVisita(Map<String, Object> visita);

	Map<String, Object> getVisitaById(int numPrestazione);

	List<Map<String, Object>> getAllVisite();

	void updateVisita(Map<String, Object> visita);

	void deleteVisita(int numPrestazione);
}
