package dao;

import model.Paziente;

import java.util.List;

public interface PazienteDAO {
	void insertPaziente(Paziente paziente);

	Paziente getPazienteById(String matricolaPaziente);

	List<Paziente> getAllPazienti();

	void updatePaziente(Paziente paziente);

	void deletePaziente(String matricolaPaziente);
}