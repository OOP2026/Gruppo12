package model;
import java.time.LocalDateTime;

public class TestModel {

	public static void main(String[] args) {
		// 1. Creiamo un Reparto
		Reparto cardiologia = new Reparto("Cardiologia");

		// 2. Creiamo un Medico
		Medico drHouse = new Medico("g.house", "pass123", "M001");

		// 3. Colleghiamo il Medico al Reparto (Associazione bidirezionale)
		cardiologia.addMedico(drHouse);
		drHouse.setReparto(cardiologia);

		// 4. Creiamo una Stanza e un Letto
		Stanza stanza101 = new Stanza(101);
		Letto lettoA = new Letto("L-101A");

		// 5. Assembliamo la struttura (Composizione)
		stanza101.addLetto(lettoA);
		lettoA.setStanza(stanza101);

		cardiologia.addStanza(stanza101);
		stanza101.setReparto(cardiologia);

		// 6. Registriamo una malattia per il medico
		Malattia influenza = new Malattia("MAL-001", LocalDateTime.now(), LocalDateTime.now().plusDays(5));
		influenza.setMedico(drHouse);
		drHouse.addMalattia(influenza);

		System.out.println("Test di istanziazione completato con successo! Nessun errore.");


	}

}