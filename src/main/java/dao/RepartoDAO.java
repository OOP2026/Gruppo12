package dao;

import model.Reparto;

import java.util.List;

public interface RepartoDAO {
	void insertReparto(Reparto reparto);

	Reparto getRepartoById(String nomeReparto);

	List<Reparto> getAllReparti();

	void updateReparto(String vecchioNome, Reparto nuovoReparto);

	void deleteReparto(String nomeReparto);
}