package dao;

import java.util.List;
import java.util.Map;

public interface AmministratoreDAO {
	void insertAmministratore(Map<String, Object> amministratore);

	Map<String, Object> getAmministratoreById(String matricolaAmministratore);

	List<Map<String, Object>> getAllAmministratori();

	void updateAmministratore(Map<String, Object> amministratore);

	void deleteAmministratore(String matricolaAmministratore);
}
