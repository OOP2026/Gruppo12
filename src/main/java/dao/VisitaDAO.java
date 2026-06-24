package dao;

import model.Visita;

import java.util.List;

public interface VisitaDAO {
	void insertVisita(Visita visita);

	Visita getVisitaById(int numPrestazione);

	List<Visita> getAllVisite();

	void updateVisita(Visita visita);

	void deleteVisita(int numPrestazione);
}