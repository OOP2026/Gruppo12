package dao;

import model.TurnoLavorativo;

import java.util.List;

public interface TurnoLavorativoDAO {
	void insertTurnoLavorativo(TurnoLavorativo turnoLavorativo);

	TurnoLavorativo getTurnoLavorativoById(String idTurno);

	List<TurnoLavorativo> getAllTurniLavorativi();

	void updateTurnoLavorativo(TurnoLavorativo turnoLavorativo);

	void deleteTurnoLavorativo(String idTurno);
}