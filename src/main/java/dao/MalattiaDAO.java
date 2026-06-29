package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sulle malattie persistite nel database. */
public interface MalattiaDAO {
	void insertMalattia(Map<String, Object> malattia);

	Map<String, Object> getMalattiaById(String idMalattia);

	List<Map<String, Object>> getAllMalattie();

	void updateMalattia(Map<String, Object> malattia);

	void deleteMalattia(String idMalattia);
}
