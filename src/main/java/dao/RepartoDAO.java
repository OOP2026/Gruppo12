package dao;

import java.util.List;
import java.util.Map;

public interface RepartoDAO {
	void insertReparto(Map<String, Object> reparto);

	Map<String, Object> getRepartoById(String nomeReparto);

	List<Map<String, Object>> getAllReparti();

	void updateReparto(String vecchioNome, Map<String, Object> nuovoReparto);

	void deleteReparto(String nomeReparto);
}
