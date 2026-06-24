package dao;

import model.Prestazione;

import java.util.List;

public interface PrestazioneDAO {
	void insertPrestazione(Prestazione prestazione);

	Prestazione getPrestazioneById(int numPrestazione);

	List<Prestazione> getAllPrestazioni();

	void updatePrestazione(Prestazione prestazione);

	void deletePrestazione(int numPrestazione);

	void sostituisciMedicoInPrestazione(int numPrestazione, String matricolaAssente, String matricolaSostituto);
}