package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sui letti persistiti nel database. */
public interface LettoDAO {
	void insertLetto(Map<String, Object> letto);

	Map<String, Object> getLettoById(String matricolaLetto);

	List<Map<String, Object>> getAllLetti();

	void updateLetto(Map<String, Object> letto);

	void deleteLetto(String matricolaLetto);
}
