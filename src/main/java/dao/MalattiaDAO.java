package dao;

import model.Malattia;

import java.util.List;

public interface MalattiaDAO {
	void insertMalattia(Malattia malattia);

	Malattia getMalattiaById(String idMalattia);

	List<Malattia> getAllMalattie();

	void updateMalattia(Malattia malattia);

	void deleteMalattia(String idMalattia);
}