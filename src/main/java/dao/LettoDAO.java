package dao;

import java.util.List;
import java.util.Map;

public interface LettoDAO {
	void insertLetto(Map<String, Object> letto);

	Map<String, Object> getLettoById(String matricolaLetto);

	List<Map<String, Object>> getAllLetti();

	void updateLetto(Map<String, Object> letto);

	void deleteLetto(String matricolaLetto);
}
