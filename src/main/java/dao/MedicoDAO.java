package dao;

import model.Medico;

import java.util.List;

public interface MedicoDAO {
	void insertMedico(Medico medico);

	Medico getMedicoById(String matricolaMedico);

	List<Medico> getAllMedici();

	void updateMedico(Medico medico);

	void deleteMedico(String matricolaMedico);
}