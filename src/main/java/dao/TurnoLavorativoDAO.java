package dao;

import java.util.List;
import java.util.Map;

public interface TurnoLavorativoDAO {
	void insertTurnoLavorativo(Map<String, Object> turnoLavorativo);

	Map<String, Object> getTurnoLavorativoById(String idTurno);

	List<Map<String, Object>> getAllTurniLavorativi();

	void updateTurnoLavorativo(Map<String, Object> turnoLavorativo);

	void deleteTurnoLavorativo(String idTurno);
}
