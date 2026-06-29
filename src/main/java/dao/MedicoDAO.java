package dao;

import java.util.List;
import java.util.Map;

public interface MedicoDAO {
	void insertMedico(Map<String, Object> medico);

	Map<String, Object> getMedicoById(String matricolaMedico);

	List<Map<String, Object>> getAllMedici();

	void updateMedico(Map<String, Object> medico);

	void deleteMedico(String matricolaMedico);
}
