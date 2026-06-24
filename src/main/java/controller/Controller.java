package controller;

import dao.AmministratoreDAO;
import dao.LettoDAO;
import dao.MalattiaDAO;
import dao.MedicoDAO;
import dao.PazienteDAO;
import dao.PrestazioneDAO;
import dao.RepartoDAO;
import dao.RicoveroDAO;
import dao.StanzaDAO;
import dao.TurnoLavorativoDAO;
import dao.UtenteDAO;
import implementazioneDao.AmministratorePostgresDao;
import implementazioneDao.LettoPostgresDao;
import implementazioneDao.MalattiaPostgresDao;
import implementazioneDao.MedicoPostgresDao;
import implementazioneDao.PazientePostgresDao;
import implementazioneDao.PrestazionePostgresDao;
import implementazioneDao.RepartoPostgresDao;
import implementazioneDao.RicoveroPostgresDao;
import implementazioneDao.StanzaPostgresDao;
import implementazioneDao.TurnoLavorativoPostgresDao;
import implementazioneDao.UtentePostgresDao;
import model.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final UtenteDAO utenteDAO = new UtentePostgresDao();
    private final AmministratoreDAO amministratoreDAO = new AmministratorePostgresDao();
    private final MedicoDAO medicoDAO = new MedicoPostgresDao();
    private final PazienteDAO pazienteDAO = new PazientePostgresDao();
    private final RicoveroDAO ricoveroDAO = new RicoveroPostgresDao();
    private final PrestazioneDAO prestazioneDAO = new PrestazionePostgresDao();
    private final MalattiaDAO malattiaDAO = new MalattiaPostgresDao();
    private final RepartoDAO repartoDAO = new RepartoPostgresDao();
    private final StanzaDAO stanzaDAO = new StanzaPostgresDao();
    private final LettoDAO lettoDAO = new LettoPostgresDao();
    private final TurnoLavorativoDAO turnoLavorativoDAO = new TurnoLavorativoPostgresDao();

    private List<Paziente> listaPazienti = new ArrayList<>();
    private List<Utente> listaUtenti = new ArrayList<>();
    private List<Letto> listaLetti = new ArrayList<>();
    private List<Ricovero> listaRicoveri = new ArrayList<>();
    private List<Prestazione> listaPrestazioni = new ArrayList<>();
    private List<Medico> listaMedici = new ArrayList<>();
    private List<Reparto> listaReparti = new ArrayList<>();
    private List<Stanza> listaStanze = new ArrayList<>();
    private List<Malattia> listaMalattie = new ArrayList<>();
    private List<TurnoLavorativo> listaTurniLavorativi = new ArrayList<>();

    public Controller() {
        sincronizzaUtentiPredefinitiSulDatabase();
        caricaDatiPersistentiDalDatabase();
    }

    private void sincronizzaUtentiPredefinitiSulDatabase() {
        if (utenteDAO.getUtenteById("admin") == null) {
            amministratoreDAO.insertAmministratore(new Amministratore("admin", "admin", "AMM1"));
        }
    }

    private void caricaDatiPersistentiDalDatabase() {
        caricaRepartiStanzeELettiDalDatabase();
        caricaUtentiDalDatabase();
        caricaTurniDalDatabase();
        associaTurniAiMedici();
        caricaPazientiDalDatabase();
        caricaRicoveriDalDatabase();
        caricaPrestazioniDalDatabase();
    }

    private void caricaUtentiDalDatabase() {
        listaUtenti.clear();
        listaMedici.clear();

        for (Amministratore amministratore : amministratoreDAO.getAllAmministratori()) {
            listaUtenti.add(amministratore);
        }

        for (Medico medico : medicoDAO.getAllMedici()) {
            String nomeReparto = medico.getReparto() != null ? medico.getReparto().getNomeReparto() : null;
            if (nomeReparto != null) {
                Reparto reparto = trovaRepartoPerNome(nomeReparto);
                if (reparto != null) {
                    medico.setReparto(reparto);
                    if (!reparto.getMedici().contains(medico)) {
                        reparto.addMedico(medico);
                    }
                }
            }
            listaUtenti.add(medico);
            listaMedici.add(medico);
        }
    }

    private void caricaRepartiStanzeELettiDalDatabase() {
        listaReparti.clear();
        listaStanze.clear();
        listaLetti.clear();

        for (Reparto reparto : repartoDAO.getAllReparti()) {
            listaReparti.add(reparto);
        }

        for (Stanza stanza : stanzaDAO.getAllStanze()) {
            Reparto reparto = stanza.getReparto() != null ? trovaRepartoPerNome(stanza.getReparto().getNomeReparto()) : null;
            if (reparto != null) {
                stanza.setReparto(reparto);
                if (!reparto.getStanze().contains(stanza)) {
                    reparto.addStanza(stanza);
                }
            }
            listaStanze.add(stanza);
        }

        for (Letto letto : lettoDAO.getAllLetti()) {
            Stanza stanza = letto.getStanza() != null ? trovaStanzaPerNumero(letto.getStanza().getNumeroStanza()) : null;
            if (stanza != null) {
                letto.setStanza(stanza);
                if (!stanza.getLetti().contains(letto)) {
                    stanza.addLetto(letto);
                }
            }
            listaLetti.add(letto);
        }
    }

    private void caricaPazientiDalDatabase() {
        listaPazienti.clear();
        for (Paziente paziente : pazienteDAO.getAllPazienti()) {
            listaPazienti.add(paziente);
        }
    }

    private void caricaTurniDalDatabase() {
        listaTurniLavorativi.clear();
        listaTurniLavorativi.addAll(turnoLavorativoDAO.getAllTurniLavorativi());
    }

    private void caricaRicoveriDalDatabase() {
        listaRicoveri.clear();
        for (Ricovero ricovero : ricoveroDAO.getAllRicoveri()) {
            Paziente paziente = ricovero.getPazienteAssegnato() != null ? trovaPazientePerMatricola(ricovero.getPazienteAssegnato().getMatricolaPaziente()) : null;
            Letto letto = ricovero.getLettoAssegnato() != null ? trovaLettoPerMatricola(ricovero.getLettoAssegnato().getMatricolaLetto()) : null;

            if (paziente != null) {
                ricovero.setPaziente(paziente);
                if (!paziente.getListaRicoveri().contains(ricovero)) {
                    paziente.addRicovero(ricovero);
                }
            }

            if (letto != null) {
                ricovero.setLetto(letto);
                if (!letto.getRicoveri().contains(ricovero)) {
                    letto.addRicovero(ricovero);
                }
            }

            listaRicoveri.add(ricovero);
        }
    }

    private void caricaPrestazioniDalDatabase() {
        listaPrestazioni.clear();
        for (Prestazione prestazione : prestazioneDAO.getAllPrestazioni()) {
            if (prestazione == null) {
                continue;
            }

            List<Medico> mediciCaricati = new ArrayList<>(prestazione.getMedici());
            prestazione.getMedici().clear();
            for (Medico medicoCaricato : mediciCaricati) {
                Medico medico = trovaMedicoPerMatricola(medicoCaricato.getMatricolaMedico());
                if (medico != null) {
                    medico.addPrestazione(prestazione);
                }
            }

            Ricovero ricovero = prestazione.getRicoveroAssegnato() != null
                    ? trovaRicoveroPerCodice(prestazione.getRicoveroAssegnato().getCodiceRicovero())
                    : null;
            if (ricovero != null) {
                prestazione.setRicovero(ricovero);
                if (!ricovero.getListaPrestazioni().contains(prestazione)) {
                    ricovero.addPrestazione(prestazione);
                }
            }
            listaPrestazioni.add(prestazione);
        }
    }

    private Reparto trovaRepartoPerNome(String nomeReparto) {
        for (Reparto reparto : listaReparti) {
            if (reparto.getNomeReparto().equals(nomeReparto)) {
                return reparto;
            }
        }
        return null;
    }

    private Stanza trovaStanzaPerNumero(Integer numeroStanza) {
        for (Stanza stanza : listaStanze) {
            if (stanza.getNumeroStanza().equals(numeroStanza)) {
                return stanza;
            }
        }
        return null;
    }

    private Letto trovaLettoPerMatricola(String matricolaLetto) {
        for (Letto letto : listaLetti) {
            if (letto.getMatricolaLetto().equals(matricolaLetto)) {
                return letto;
            }
        }
        return null;
    }

    private Paziente trovaPazientePerMatricola(String matricolaPaziente) {
        for (Paziente paziente : listaPazienti) {
            if (paziente.getMatricolaPaziente().equals(matricolaPaziente)) {
                return paziente;
            }
        }
        return null;
    }

    private Ricovero trovaRicoveroPerCodice(String codiceRicovero) {
        for (Ricovero ricovero : listaRicoveri) {
            if (ricovero.getCodiceRicovero().equals(codiceRicovero)) {
                return ricovero;
            }
        }
        return null;
    }

    private Medico trovaMedicoPerMatricola(String matricolaMedico) {
        if (matricolaMedico == null) {
            return null;
        }

        for (Medico medico : listaMedici) {
            if (medico.getMatricolaMedico().equals(matricolaMedico)) {
                return medico;
            }
        }
        return null;
    }

    private Malattia trovaMalattiaPerId(String idMalattia) {
        if (idMalattia == null) {
            return null;
        }

        for (Malattia malattia : listaMalattie) {
            if (idMalattia.equals(malattia.getIdMalattia())) {
                return malattia;
            }
        }

        return null;
    }

    private void associaTurniAiMedici() {
        if (listaTurniLavorativi.isEmpty() || listaMedici.isEmpty()) {
            return;
        }

        for (Medico medico : listaMedici) {
            if (medico.getListaTurniLavorativi().isEmpty()) {
                for (TurnoLavorativo turno : listaTurniLavorativi) {
                    medico.addTurnoLavorativo(turno);
                }
            }
        }
    }

    public boolean assegnaMalattiaMedico(String matricolaMedico, String idMalattia, LocalDateTime dataInizio, LocalDateTime dataFine) {
        if (matricolaMedico == null) {
            return false;
        }

        Medico medico = trovaMedicoPerMatricola(matricolaMedico);
        if (medico != null) {
            Malattia malattia = new Malattia(idMalattia, dataInizio, dataFine);
            malattia.setMedico(medico);
            medico.addMalattia(malattia);
            listaMalattie.add(malattia);
            malattiaDAO.insertMalattia(malattia);
            return true;
        }
        return false;
    }

    public boolean aggiungiPaziente(String matricolaPaziente, String nome, String cognome) {

        for (Paziente p : listaPazienti) {
            if (p.getMatricolaPaziente().equals(matricolaPaziente)) {
                return false;
            }
        }

        Paziente p = new Paziente(matricolaPaziente, nome, cognome);
        listaPazienti.add(p);
        pazienteDAO.insertPaziente(p);
        return true;
    }

    public Utente effettuaLogin(String login, String password) {
        if (login == null || password == null) {
            return null;
        }

        if (listaUtenti == null) {
            return null;
        }

        for (Utente utenteLoggato : listaUtenti) {
            if (utenteLoggato != null && utenteLoggato.login(login, password)) {
                return utenteLoggato;
            }
        }
        return null;

    }

    public boolean aggiungiRicovero(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {
        for (Ricovero r : listaRicoveri) {
            if (r.getCodiceRicovero().equals(codiceRicovero)) {
                return false;
            }
        }

        Paziente pazienteTrovato = null;
        Letto lettoTrovato = null;

        for (Paziente paziente : listaPazienti) {
            if (paziente.getMatricolaPaziente().equals(matricolaPaziente)) pazienteTrovato = paziente;
        }
        for (Letto l : listaLetti) {
            if (l.getMatricolaLetto().equals(matricolaLetto)) lettoTrovato = l;
        }

        if (pazienteTrovato == null || lettoTrovato == null) {
            return false;
        }


        LocalDateTime newStart = LocalDateTime.now();
        LocalDateTime newEnd = null; // aperto
        for (Ricovero ricoveroEsistente : lettoTrovato.getRicoveri()) {
            LocalDateTime existStart = ricoveroEsistente.getDataAmmissione();
            LocalDateTime existEnd = ricoveroEsistente.getDataDimissione();


            boolean overlaps;
            if (existEnd == null) {
                overlaps = !newStart.isAfter(existStart);
                overlaps = true;
            } else {

                LocalDateTime aStart = newStart;
                LocalDateTime aEnd = (newEnd == null) ? LocalDateTime.MAX : newEnd;
                LocalDateTime bStart = existStart;
                LocalDateTime bEnd = existEnd;
                overlaps = overlap(aStart, aEnd, bStart, bEnd);
            }

            if (overlaps) {
                Paziente pAssegnato = ricoveroEsistente.getPazienteAssegnato();
                if (pAssegnato == null || !pAssegnato.getMatricolaPaziente().equals(pazienteTrovato.getMatricolaPaziente())) {
                    return false;
                }
            }
        }

        Ricovero r = new Ricovero(LocalDateTime.now(), null, codiceRicovero);
        r.setPaziente(pazienteTrovato);
        r.setLetto(lettoTrovato);
        pazienteTrovato.addRicovero(r);
        lettoTrovato.addRicovero(r);

        listaRicoveri.add(r);
        ricoveroDAO.insertRicovero(r);
        return true;
    }

    public boolean aggiungiDimissione(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {

        if (codiceRicovero == null) {
            return false;
        }

        for (Ricovero r : listaRicoveri) {
            if (r.getCodiceRicovero().equals(codiceRicovero)) {


                if (r.getPazienteAssegnato().getMatricolaPaziente().equals(matricolaPaziente) &&
                        r.getLettoAssegnato().getMatricolaLetto().equals(matricolaLetto)) {


                    if (r.getDataDimissione() == null) {


                        r.setDataDimissione(LocalDateTime.now());
                        ricoveroDAO.updateRicovero(r);

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    public boolean aggiungiMedico(Medico medico) {
        for (Medico m : listaMedici) if (m.getMatricolaMedico().equals(medico.getMatricolaMedico())) return false;
        listaMedici.add(medico);
        medicoDAO.insertMedico(medico);
        return true;
    }

    public boolean aggiungiReparto(Reparto reparto) {
        for (Reparto r : listaReparti) if (r.getNomeReparto().equals(reparto.getNomeReparto())) return false;
        listaReparti.add(reparto);
        repartoDAO.insertReparto(reparto);
        return true;
    }

    public boolean aggiungiStanza(Stanza stanza) {
        for (Stanza s : listaStanze) if (s.getNumeroStanza().equals(stanza.getNumeroStanza())) return false;
        listaStanze.add(stanza);
        stanzaDAO.insertStanza(stanza);
        return true;
    }

    public boolean aggiungiLetto(Letto letto) {
        for (Letto l : listaLetti) if (l.getMatricolaLetto().equals(letto.getMatricolaLetto())) return false;
        listaLetti.add(letto);
        lettoDAO.insertLetto(letto);
        return true;
    }

    public boolean aggiungiTurno(TurnoLavorativo turno) {
        for (TurnoLavorativo t : listaTurniLavorativi) if (t.getIdTurno().equals(turno.getIdTurno())) return false;
        listaTurniLavorativi.add(turno);
        turnoLavorativoDAO.insertTurnoLavorativo(turno);
        return true;
    }

    private boolean overlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private boolean isMedicoInTurno(Medico medico, LocalDateTime dataInizio, LocalDateTime dataFine) {
        for (TurnoLavorativo turno : medico.getListaTurniLavorativi()) {
            if (!dataInizio.isBefore(turno.getInizioTurno()) && !dataFine.isAfter(turno.getFineTurno())) {
                return true;
            }
        }
        return false;
    }

    private boolean isLettoOccupato(Letto letto) {
        if (letto == null) {
            return false;
        }

        for (Ricovero ricovero : letto.getRicoveri()) {
            if (ricovero.getDataDimissione() == null) {
                return true;
            }
        }

        return false;
    }

    public boolean registraVisita(String codiceRicovero, Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, String tipoVisita, String matricolaMedico) {
        if (codiceRicovero == null || matricolaMedico == null) return false;
        Ricovero ricovero = null;
        Medico medico = trovaMedicoPerMatricola(matricolaMedico);
        for (Ricovero r : listaRicoveri) if (r.getCodiceRicovero().equals(codiceRicovero)) ricovero = r;
        if (ricovero == null || medico == null) return false;

        if (!isMedicoInTurno(medico, dataInizio, dataFine)) return false;

        for (Prestazione p : medico.getListaPrestazioni()) {
            if (overlap(dataInizio, dataFine, p.getDataInizio(), p.getDataFine())) return false;
        }

        Visita v = new Visita(numPrestazione, dataInizio, dataFine, esito, tipoVisita);
        v.setRicovero(ricovero);
        medico.addPrestazione(v);
        prestazioneDAO.insertPrestazione(v);
        ricovero.addPrestazione(v);
        listaPrestazioni.add(v);
        return true;
    }

    public boolean registraIntervento(String codiceRicovero, Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, Integer salaOperatoria, String matricolaMedico) {
        if (codiceRicovero == null || matricolaMedico == null) return false;
        Ricovero ricovero = null;
        Medico medico = trovaMedicoPerMatricola(matricolaMedico);
        for (Ricovero r : listaRicoveri) if (r.getCodiceRicovero().equals(codiceRicovero)) ricovero = r;
        if (ricovero == null || medico == null) return false;

        if (!isMedicoInTurno(medico, dataInizio, dataFine)) return false;

        for (Prestazione p : medico.getListaPrestazioni()) {
            if (overlap(dataInizio, dataFine, p.getDataInizio(), p.getDataFine())) return false;
        }

        Intervento it = new Intervento(numPrestazione, dataInizio, dataFine, esito, salaOperatoria);
        it.setRicovero(ricovero);
        medico.addPrestazione(it);
        prestazioneDAO.insertPrestazione(it);
        ricovero.addPrestazione(it);
        listaPrestazioni.add(it);
        return true;
    }

    public boolean modificaEsitoPrestazione(Integer numPrestazione, String esito) {
        for (Prestazione p : listaPrestazioni) {
            if (p.getNumPrestazione().equals(numPrestazione)) {
                p.setEsito(esito);
                prestazioneDAO.updatePrestazione(p);
                return true;
            }
        }
        return false;
    }

    public List<Prestazione> agendaGiornaliera(String matricolaMedico, LocalDate giorno) {
        List<Prestazione> result = new ArrayList<>();
        for (Prestazione p : listaPrestazioni) {
            for (Medico m : p.getMedici()) {
                if (m.getMatricolaMedico().equals(matricolaMedico) && p.getDataInizio().toLocalDate().equals(giorno))
                    result.add(p);
            }
        }
        return result;
    }

    public List<Prestazione> agendaSettimanale(String matricolaMedico, LocalDate inizioSettimana) {
        List<Prestazione> result = new ArrayList<>();
        LocalDate fine = inizioSettimana.plusDays(6);
        for (Prestazione p : listaPrestazioni) {
            for (Medico m : p.getMedici()) {
                LocalDate d = p.getDataInizio().toLocalDate();
                if (m.getMatricolaMedico().equals(matricolaMedico) && (d.isEqual(inizioSettimana) || (d.isAfter(inizioSettimana) && d.isBefore(fine)) || d.isEqual(fine)))
                    result.add(p);
            }
        }
        return result;
    }

    public List<Letto> cercaLettiDisponibili(String nomeReparto) {
        List<Letto> disponibili = new ArrayList<>();
        for (Letto letto : cercaLettiPerReparto(nomeReparto)) {
            if (!isLettoOccupato(letto)) {
                disponibili.add(letto);
            }
        }
        return disponibili;
    }

    public List<Letto> cercaLettiPerReparto(String nomeReparto) {
        List<Letto> letti = new ArrayList<>();
        Reparto target = null;
        for (Reparto r : listaReparti) if (r.getNomeReparto().equals(nomeReparto)) target = r;
        if (target == null) return letti;
        for (Stanza s : target.getStanze()) {
            for (Letto l : s.getLetti()) {
                letti.add(l);
            }
        }
        return letti;
    }

    public boolean lettoOccupato(String matricolaLetto) {
        if (matricolaLetto == null) {
            return false;
        }

        for (Letto letto : listaLetti) {
            if (matricolaLetto.equals(letto.getMatricolaLetto())) {
                return isLettoOccupato(letto);
            }
        }

        return false;
    }

    public List<Ricovero> dimissioniInData(LocalDate data) {
        List<Ricovero> result = new ArrayList<>();
        for (Ricovero r : listaRicoveri) {
            if (r.getDataDimissione() != null && r.getDataDimissione().toLocalDate().equals(data)) result.add(r);
        }
        return result;
    }

    public List<Medico> suggerisciSostituti(String idMalattia) {
        List<Medico> suggeriti = new ArrayList<>();
        if (idMalattia == null || idMalattia.isBlank()) {
            return suggeriti;
        }

        Malattia mal = malattiaDAO.getMalattiaById(idMalattia);
        if (mal == null) return suggeriti;
        Medico assente = null;
        if (mal.getMedicoAssegnato() != null) {
            assente = trovaMedicoPerMatricola(mal.getMedicoAssegnato().getMatricolaMedico());
        }
        if (assente == null) {
            assente = mal.getMedicoAssegnato();
        }
        if (assente == null) return suggeriti;
        Reparto rep = assente.getReparto();
        LocalDateTime start = mal.getDataInizio();
        LocalDateTime end = mal.getDataFine();
        List<Medico> candidati = new ArrayList<>();

        if (rep != null) candidati.addAll(rep.getMedici());
        else candidati.addAll(listaMedici);

        for (Medico c : candidati) {
            if (c == assente) continue; // Salta il medico malato
            boolean conflitto = false;

            // Unico vero conflitto: il collega sta già operando/visitando un altro paziente?
            for (Prestazione p : c.getListaPrestazioni()) {
                if (overlap(start, end, p.getDataInizio(), p.getDataFine())) {
                    conflitto = true;
                    break;
                }
            }

            // Se non è impegnato in altre prestazioni, è un ottimo sostituto!
            if (!conflitto) suggeriti.add(c);
        }
        return suggeriti;
    }

    public List<Medico> suggerisciSostituto(String idMalattia) {
        return suggerisciSostituti(idMalattia);
    }

    public boolean effettuaSostituzione(String idMalattia, String matricolaSostituto) {
        if (idMalattia == null || matricolaSostituto == null) return false;

        // 1. Recupero la malattia, prima dalla memoria e poi dal DB
        Malattia mal = trovaMalattiaPerId(idMalattia);
        if (mal == null) {
            mal = malattiaDAO.getMalattiaById(idMalattia);
        }
        if (mal == null || mal.getMedicoAssegnato() == null) return false;

        // 2. Recupero i due medici coinvolti
        Medico assente = mal.getMedicoAssegnato();
        Medico sostituto = trovaMedicoPerMatricola(matricolaSostituto);
        if (sostituto == null || sostituto == assente) return false;

        // 3. Aggiorno il medico assegnato alla malattia
        if (assente != null) {
            assente.removeMalattia(mal);
        }
        sostituto.addMalattia(mal);
        malattiaDAO.updateMalattia(mal);

        LocalDateTime start = mal.getDataInizio();
        LocalDateTime end = mal.getDataFine();

        // 4. Trovo le prestazioni assegnate al medico malato che cadono nel periodo di assenza
        List<Prestazione> prestazioniDaRiassegnare = new ArrayList<>();
        for (Prestazione p : assente.getListaPrestazioni()) {
            if (overlap(start, end, p.getDataInizio(), p.getDataFine())) {
                prestazioniDaRiassegnare.add(p);
            }
        }

        // Se non ci sono prestazioni in quei giorni, l'operazione è comunque un successo
        if (prestazioniDaRiassegnare.isEmpty()) return true;

        // 5. Eseguo lo scambio (Swap)
        for (Prestazione p : prestazioniDaRiassegnare) {
            // Sostituzione in memoria (liste Java)
            p.removeMedico(assente);
            p.addMedico(sostituto);

            // Sostituzione persistente nel Database
            if (prestazioneDAO instanceof PrestazionePostgresDao) {
                ((PrestazionePostgresDao) prestazioneDAO).sostituisciMedicoInPrestazione(
                        p.getNumPrestazione(),
                        assente.getMatricolaMedico(),
                        sostituto.getMatricolaMedico()
                );
            }
        }
        return true;
    }


}
