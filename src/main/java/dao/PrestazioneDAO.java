package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Operazioni CRUD sulle prestazioni persistite nel database. */
public interface PrestazioneDAO {
	void insertPrestazione(Map<String, Object> prestazione);

	Map<String, Object> getPrestazioneById(int numPrestazione);

	List<Map<String, Object>> getAllPrestazioni();

	void updatePrestazione(Map<String, Object> prestazione);

	void deletePrestazione(int numPrestazione);

	void sostituisciMedicoInPrestazione(int numPrestazione, String matricolaAssente, String matricolaSostituto);
}
