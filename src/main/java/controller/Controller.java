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

/**
 * Gestisce i dati dell'app e le regole di business.
 * Carica lo stato dal database e fornisce i metodi usati dalle viste.
 */
public class Controller {
    private static final String DEFAULT_ADMIN = "admin";

    // DAO usati per leggere e scrivere i dati nel database.
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

    // Liste in memoria usate dalla UI e dalle verifiche interne.
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
        // Prepara l'utente di default e carica i dati all'avvio.
        sincronizzaUtentiPredefinitiSulDatabase();
        caricaDatiPersistentiDalDatabase();
    }

    private void sincronizzaUtentiPredefinitiSulDatabase() {
        // Inserisce l'admin base solo se manca.
        if (utenteDAO.getUtenteById(DEFAULT_ADMIN) == null) {
            amministratoreDAO.insertAmministratore(new Amministratore(DEFAULT_ADMIN, DEFAULT_ADMIN, "AMM1"));
        }
    }

    private void caricaDatiPersistentiDalDatabase() {
        // Carica prima i dati base e poi le relazioni tra gli oggetti.
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

        // Gli amministratori vengono caricati come utenti.
        for (Amministratore amministratore : amministratoreDAO.getAllAmministratori()) {
            listaUtenti.add(amministratore);
        }

        // I medici vengono ricollegati al reparto.
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

        // Carica i reparti.
        listaReparti.addAll(repartoDAO.getAllReparti());

        // Carica le stanze e collega il reparto corretto.
        for (Stanza stanza : stanzaDAO.getAllStanze()) {
            associaRepartoAStanza(stanza);
            listaStanze.add(stanza);
        }

        // Carica i letti e collega la stanza corretta.
        for (Letto letto : lettoDAO.getAllLetti()) {
            associaStanzaALetto(letto);
            listaLetti.add(letto);
        }
    }

    private void associaRepartoAStanza(Stanza stanza) {
        // Se manca il reparto, non c'è nulla da collegare.
        if (stanza.getReparto() == null) {
            return;
        }

        // Usa il reparto già caricato in memoria.
        Reparto reparto = trovaRepartoPerNome(stanza.getReparto().getNomeReparto());
        if (reparto == null) {
            return;
        }

        stanza.setReparto(reparto);
        if (!reparto.getStanze().contains(stanza)) {
            reparto.addStanza(stanza);
        }
    }

    private void associaStanzaALetto(Letto letto) {
        // Se manca la stanza, non c'è nulla da collegare.
        if (letto.getStanza() == null) {
            return;
        }

        Stanza stanza = trovaStanzaPerNumero(letto.getStanza().getNumeroStanza());
        if (stanza == null) {
            return;
        }

        letto.setStanza(stanza);
        if (!stanza.getLetti().contains(letto)) {
            stanza.addLetto(letto);
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
        // I turni servono prima di collegarli ai medici.
        listaTurniLavorativi.addAll(turnoLavorativoDAO.getAllTurniLavorativi());
    }

    private void caricaRicoveriDalDatabase() {
        listaRicoveri.clear();
        // Ricollega ogni ricovero a paziente e letto in memoria.
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
        // Ricollega ogni prestazione ai medici e al ricovero.
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
        // Cerca un reparto nella lista in memoria.
        for (Reparto reparto : listaReparti) {
            if (reparto.getNomeReparto().equals(nomeReparto)) {
                return reparto;
            }
        }
        return null;
    }

    private Stanza trovaStanzaPerNumero(Integer numeroStanza) {
        // Cerca una stanza nella lista in memoria.
        for (Stanza stanza : listaStanze) {
            if (stanza.getNumeroStanza().equals(numeroStanza)) {
                return stanza;
            }
        }
        return null;
    }

    private Letto trovaLettoPerMatricola(String matricolaLetto) {
        // Cerca un letto nella lista in memoria.
        for (Letto letto : listaLetti) {
            if (letto.getMatricolaLetto().equals(matricolaLetto)) {
                return letto;
            }
        }
        return null;
    }

    private Paziente trovaPazientePerMatricola(String matricolaPaziente) {
        // Cerca un paziente nella lista in memoria.
        for (Paziente paziente : listaPazienti) {
            if (paziente.getMatricolaPaziente().equals(matricolaPaziente)) {
                return paziente;
            }
        }
        return null;
    }

    private Ricovero trovaRicoveroPerCodice(String codiceRicovero) {
        // Cerca un ricovero nella lista in memoria.
        for (Ricovero ricovero : listaRicoveri) {
            if (ricovero.getCodiceRicovero().equals(codiceRicovero)) {
                return ricovero;
            }
        }
        return null;
    }

    private Medico trovaMedicoPerMatricola(String matricolaMedico) {
        // Cerca un medico nella lista in memoria.
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
        // Cerca una malattia nella lista in memoria.
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
        // Se i medici non hanno turni, assegna tutti i turni caricati.
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
        // Assegna una malattia a un medico esistente.
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
        // Evita duplicati prima di creare il nuovo paziente.
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
        // Confronta le credenziali con gli utenti già caricati.
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
        // Crea un ricovero solo se i dati sono coerenti.
        if (trovaRicoveroPerCodice(codiceRicovero) != null) {
            return false;
        }

        Paziente pazienteTrovato = trovaPazientePerMatricola(matricolaPaziente);
        Letto lettoTrovato = trovaLettoPerMatricola(matricolaLetto);

        if (pazienteTrovato == null || lettoTrovato == null) {
            return false;
        }

        if (!isLettoValidoPerNuovoRicovero(lettoTrovato, pazienteTrovato)) {
            return false;
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

    private boolean isLettoValidoPerNuovoRicovero(Letto letto, Paziente pazienteRichiedente) {
        // Controlla se il letto può essere usato senza conflitti.
        LocalDateTime newStart = LocalDateTime.now();

        for (Ricovero ricoveroEsistente : letto.getRicoveri()) {
            boolean overlaps = verificaSovrapposizioneDate(newStart, ricoveroEsistente);

            if (overlaps) {
                Paziente pAssegnato = ricoveroEsistente.getPazienteAssegnato();
                // Se il paziente non coincide, il letto non va bene.
                if (pAssegnato == null || !pAssegnato.getMatricolaPaziente().equals(pazienteRichiedente.getMatricolaPaziente())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verificaSovrapposizioneDate(LocalDateTime newStart, Ricovero ricoveroEsistente) {
        // Un ricovero aperto occupa il letto fino a tempo indefinito.
        LocalDateTime existStart = ricoveroEsistente.getDataAmmissione();
        LocalDateTime existEnd = ricoveroEsistente.getDataDimissione();

        if (existEnd == null) {
            return true;
        }

        LocalDateTime newEnd = LocalDateTime.MAX;

        return overlap(newStart, newEnd, existStart, existEnd);
    }

    public boolean aggiungiDimissione(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {
        // Chiude un ricovero solo se i dati combaciano e non è già chiuso.
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
        // Inserisce un medico solo se la matricola non esiste già.
        for (Medico m : listaMedici) if (m.getMatricolaMedico().equals(medico.getMatricolaMedico())) return false;
        listaMedici.add(medico);
        medicoDAO.insertMedico(medico);
        return true;
    }

    public boolean aggiungiReparto(Reparto reparto) {
        // Inserisce un reparto solo se il nome non è già presente.
        for (Reparto r : listaReparti) if (r.getNomeReparto().equals(reparto.getNomeReparto())) return false;
        listaReparti.add(reparto);
        repartoDAO.insertReparto(reparto);
        return true;
    }

    public boolean aggiungiStanza(Stanza stanza) {
        // Inserisce una stanza solo se il numero non è già usato.
        for (Stanza s : listaStanze) if (s.getNumeroStanza().equals(stanza.getNumeroStanza())) return false;
        listaStanze.add(stanza);
        stanzaDAO.insertStanza(stanza);
        return true;
    }

    public boolean aggiungiLetto(Letto letto) {
        // Inserisce un letto solo se la matricola non è già presente.
        for (Letto l : listaLetti) if (l.getMatricolaLetto().equals(letto.getMatricolaLetto())) return false;
        listaLetti.add(letto);
        lettoDAO.insertLetto(letto);
        return true;
    }

    public boolean aggiungiTurno(TurnoLavorativo turno) {
        // Inserisce un turno solo se l'id non è già presente.
        for (TurnoLavorativo t : listaTurniLavorativi) if (t.getIdTurno().equals(turno.getIdTurno())) return false;
        listaTurniLavorativi.add(turno);
        turnoLavorativoDAO.insertTurnoLavorativo(turno);
        return true;
    }

    private boolean overlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        // Due intervalli si sovrappongono se ciascuno inizia prima che l'altro finisca.
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private boolean isMedicoInTurno(Medico medico, LocalDateTime dataInizio, LocalDateTime dataFine) {
        // La prestazione deve stare dentro almeno un turno del medico.
        for (TurnoLavorativo turno : medico.getListaTurniLavorativi()) {
            if (!dataInizio.isBefore(turno.getInizioTurno()) && !dataFine.isAfter(turno.getFineTurno())) {
                return true;
            }
        }
        return false;
    }

    private boolean isLettoOccupato(Letto letto) {
        // Un letto è occupato se c'è almeno un ricovero ancora aperto.
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
        // Registra una visita solo se il medico è disponibile.
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
        // Registra un intervento con le stesse regole della visita.
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
        // Aggiorna l'esito di una prestazione già salvata.
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
        // Restituisce le prestazioni del medico in un giorno.
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
        // Restituisce le prestazioni del medico nella settimana indicata.
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
        // Filtra i letti del reparto e tiene solo quelli liberi.
        List<Letto> disponibili = new ArrayList<>();
        for (Letto letto : cercaLettiPerReparto(nomeReparto)) {
            if (!isLettoOccupato(letto)) {
                disponibili.add(letto);
            }
        }
        return disponibili;
    }

    public List<Letto> cercaLettiPerReparto(String nomeReparto) {
        // Restituisce tutti i letti di un reparto.
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
        // Verifica se un letto specifico è occupato.
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
        // Restituisce i ricoveri dimessi in una data precisa.
        List<Ricovero> result = new ArrayList<>();
        for (Ricovero r : listaRicoveri) {
            if (r.getDataDimissione() != null && r.getDataDimissione().toLocalDate().equals(data)) result.add(r);
        }
        return result;
    }

    public List<Medico> suggerisciSostituti(String idMalattia) {
        // Cerca i medici adatti a sostituire il medico assente.
        List<Medico> suggeriti = new ArrayList<>();
        if (idMalattia == null || idMalattia.isBlank()) {
            return suggeriti;
        }

        Malattia mal = malattiaDAO.getMalattiaById(idMalattia);
        if (mal == null) {
            return suggeriti;
        }

        Medico assente = ottieniMedicoAssente(mal);
        if (assente == null) {
            return suggeriti;
        }

        // Cerca nel reparto del medico, se c'è.
        List<Medico> candidati = assente.getReparto() != null
                ? assente.getReparto().getMedici()
                : listaMedici;

        LocalDateTime start = mal.getDataInizio();
        LocalDateTime end = mal.getDataFine();

        for (Medico candidato : candidati) {
            // Aggiunge solo i medici liberi.
            if (candidato != assente && isMedicoDisponibile(candidato, start, end)) {
                suggeriti.add(candidato);
            }
        }

        return suggeriti;
    }

    private Medico ottieniMedicoAssente(Malattia mal) {
        // Usa il medico presente in memoria, se esiste.
        if (mal.getMedicoAssegnato() == null) {
            return null;
        }

        Medico medicoInMemoria = trovaMedicoPerMatricola(mal.getMedicoAssegnato().getMatricolaMedico());
        // Usa il medico già caricato, se c'è.
        return medicoInMemoria != null ? medicoInMemoria : mal.getMedicoAssegnato();
    }

    private boolean isMedicoDisponibile(Medico candidato, LocalDateTime start, LocalDateTime end) {
        // Un medico è disponibile se non ha prestazioni sovrapposte.
        // Controlla se ha altre prestazioni nello stesso intervallo.
        for (Prestazione p : candidato.getListaPrestazioni()) {
            if (overlap(start, end, p.getDataInizio(), p.getDataFine())) {
                return false; // C'è un conflitto.
            }
        }
        return true;
    }

    public List<Medico> suggerisciSostituto(String idMalattia) {
        // Mantiene un nome alternativo per compatibilità.
        return suggerisciSostituti(idMalattia);
    }

    public boolean effettuaSostituzione(String idMalattia, String matricolaSostituto) {
        // Riassegna la malattia e le prestazioni al sostituto.
        if (idMalattia == null || matricolaSostituto == null) return false;

        Malattia mal = trovaMalattiaPerId(idMalattia);
        if (mal == null) {
            mal = malattiaDAO.getMalattiaById(idMalattia);
        }
        if (mal == null || mal.getMedicoAssegnato() == null) return false;

        // Prende i due medici coinvolti.
        Medico assente = mal.getMedicoAssegnato();
        Medico sostituto = trovaMedicoPerMatricola(matricolaSostituto);
        if (sostituto == null || sostituto == assente) return false;

        // Aggiorna la malattia con il sostituto.
        if (assente != null) {
            assente.removeMalattia(mal);
        }
        sostituto.addMalattia(mal);
        malattiaDAO.updateMalattia(mal);

        LocalDateTime start = mal.getDataInizio();
        LocalDateTime end = mal.getDataFine();

        // Trova le prestazioni da spostare.
        List<Prestazione> prestazioniDaRiassegnare = new ArrayList<>();
        for (Prestazione p : assente.getListaPrestazioni()) {
            if (overlap(start, end, p.getDataInizio(), p.getDataFine())) {
                prestazioniDaRiassegnare.add(p);
            }
        }

        // Se non ci sono prestazioni da spostare, la sostituzione è già fatta.
        if (prestazioniDaRiassegnare.isEmpty()) return true;

        // Aggiorna ogni prestazione coinvolta.
        for (Prestazione p : prestazioniDaRiassegnare) {

            p.removeMedico(assente);
            p.addMedico(sostituto);

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