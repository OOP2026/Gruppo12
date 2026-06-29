package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard del medico.
 * Permette di registrare prestazioni, aggiornare esiti e consultare l'agenda.
 */
public class Medico {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Controller controller;
    private JPanel panel;
    private JLabel statusLabel;
    private JTextField medicoMatricolaField;
    private JTextField ricoveroCodiceField;
    private JTextField numPrestazioneField;
    private JTextField dataInizioField;
    private JTextField dataFineField;
    private JTextField esitoField;
    private JTextField tipoVisitaField;
    private JTextField salaOperatoriaField;
    private JButton registraVisitaButton;
    private JButton registraInterventoButton;
    private JButton aggiornaEsitoButton;
    private JTextField agendaDataField;
    private JButton agendaGiornalieraButton;
    private JButton agendaSettimanaleButton;
    private JList<String> agendaList;
    private JButton logoutButton;

    private DefaultListModel<String> agendaListModel;

    public Medico() {
        this(new Controller());
    }

    public Medico(Controller controller) {
        this.controller = controller != null ? controller : new Controller();
        this.agendaListModel = new DefaultListModel<>();

        // Componenti inizializzati dal GUI builder ($$$setupUI$$$)
        configureUi();
    }

    public JComponent getContentPane() {
        return panel;
    }

    private void configureUi() {
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(245, 247, 250));
        statusLabel.setForeground(new Color(96, 96, 96));

        agendaList.setModel(agendaListModel);

        LocalDate oggi = LocalDate.now();
        LocalDateTime adesso = LocalDateTime.now().withSecond(0).withNano(0);
        agendaDataField.setText(oggi.format(DATE_FORMATTER));
        dataInizioField.setText(adesso.format(DATE_TIME_FORMATTER));
        dataFineField.setText(adesso.plusMinutes(30).format(DATE_TIME_FORMATTER));
        esitoField.setText("In attesa");

        registraVisitaButton.addActionListener(e -> elaboraPrestazione(false));
        registraInterventoButton.addActionListener(e -> elaboraPrestazione(true));
        aggiornaEsitoButton.addActionListener(e -> aggiornaEsitoPrestazione());
        agendaGiornalieraButton.addActionListener(e -> caricaAgendaGiornaliera());
        agendaSettimanaleButton.addActionListener(e -> caricaAgendaSettimanale());
        logoutButton.addActionListener(e -> logout());
    }

    private void elaboraPrestazione(boolean isIntervento) {
        String matricolaMedico = leggi(medicoMatricolaField);
        String codiceRicovero = leggi(ricoveroCodiceField);
        String numeroPrestazione = leggi(numPrestazioneField);
        String dataInizio = leggi(dataInizioField);
        String dataFine = leggi(dataFineField);
        String esito = leggi(esitoField);

        // Leggo il campo specifico in base al tipo
        String campoSpecifico = isIntervento ? leggi(salaOperatoriaField) : leggi(tipoVisitaField);

        if (matricolaMedico.isEmpty() || codiceRicovero.isEmpty() || numeroPrestazione.isEmpty() ||
                dataInizio.isEmpty() || dataFine.isEmpty() || campoSpecifico.isEmpty()) {
            mostraAvviso("Compila tutti i campi: matricola, ricovero, numero, date e " +
                    (isIntervento ? "sala operatoria." : "tipo visita."));
            return;
        }

        try {
            Integer numero = Integer.valueOf(numeroPrestazione);
            LocalDateTime inizio = LocalDateTime.parse(dataInizio, DATE_TIME_FORMATTER);
            LocalDateTime fine = LocalDateTime.parse(dataFine, DATE_TIME_FORMATTER);

            if (!fine.isAfter(inizio)) {
                mostraAvviso("La data fine deve essere successiva alla data inizio.");
                return;
            }

            boolean registrata;
            if (isIntervento) {
                Integer sala = Integer.valueOf(campoSpecifico);
                registrata = controller.registraIntervento(codiceRicovero, numero, inizio, fine, esito, sala, matricolaMedico);
            } else {
                registrata = controller.registraVisita(codiceRicovero, numero, inizio, fine, esito, campoSpecifico, matricolaMedico);
            }

            if (registrata) {
                String tipo = isIntervento ? "Intervento " : "Visita ";
                aggiornaStato(tipo + numero + " registrato con successo.");
            } else {
                mostraAvviso("Impossibile registrare: verifica turno, sovrapposizioni e dati inseriti.");
            }

        } catch (NumberFormatException ex) {
            mostraAvviso("Numero prestazione o sala operatoria non validi.");
        } catch (DateTimeParseException ex) {
            mostraAvviso("Data/ora non valida. Usa il formato gg/mm/aaaa hh:mm.");
        }
    }

    private void aggiornaEsitoPrestazione() {
        String numeroPrestazione = leggi(numPrestazioneField);
        String esito = leggi(esitoField);

        if (numeroPrestazione.isEmpty() || esito.isEmpty()) {
            mostraAvviso("Compila numero prestazione ed esito.");
            return;
        }

        try {
            Integer numero = Integer.valueOf(numeroPrestazione);
            if (controller.modificaEsitoPrestazione(numero, esito)) {
                aggiornaStato("Esito della prestazione " + numero + " aggiornato.");
            } else {
                mostraAvviso("Prestazione non trovata.");
            }
        } catch (NumberFormatException ex) {
            mostraAvviso("Numero prestazione non valido.");
        }
    }

    private void caricaAgendaGiornaliera() {
        String matricolaMedico = leggi(medicoMatricolaField);
        String data = leggi(agendaDataField);

        if (matricolaMedico.isEmpty() || data.isEmpty()) {
            mostraAvviso("Compila matricola medico e data agenda.");
            return;
        }

        try {
            LocalDate giorno = LocalDate.parse(data, DATE_FORMATTER);
            List<Controller.PrestazioneView> agenda = controller.agendaGiornalieraView(matricolaMedico, giorno);
            caricaPrestazioni(agenda, "Nessuna prestazione in agenda per il giorno indicato.");
            aggiornaStato("Agenda giornaliera caricata per " + giorno.format(DATE_FORMATTER) + ".");
        } catch (DateTimeParseException ex) {
            mostraAvviso("Data agenda non valida. Usa il formato gg/mm/aaaa.");
        }
    }

    private void caricaAgendaSettimanale() {
        String matricolaMedico = leggi(medicoMatricolaField);
        String data = leggi(agendaDataField);

        if (matricolaMedico.isEmpty() || data.isEmpty()) {
            mostraAvviso("Compila matricola medico e data agenda.");
            return;
        }

        try {
            LocalDate giorno = LocalDate.parse(data, DATE_FORMATTER);
            LocalDate inizioSettimana = giorno.with(DayOfWeek.MONDAY);
            List<Controller.PrestazioneView> agenda = controller.agendaSettimanaleView(matricolaMedico, inizioSettimana);
            caricaPrestazioni(agenda, "Nessuna prestazione in agenda per la settimana indicata.");
            aggiornaStato("Agenda settimanale caricata da " + inizioSettimana.format(DATE_FORMATTER) + ".");
        } catch (DateTimeParseException ex) {
            mostraAvviso("Data agenda non valida. Usa il formato gg/mm/aaaa.");
        }
    }

    private void caricaPrestazioni(List<Controller.PrestazioneView> prestazioni, String emptyMessage) {
        agendaListModel.clear();
        if (prestazioni.isEmpty()) {
            agendaListModel.addElement(emptyMessage);
            return;
        }
        for (Controller.PrestazioneView prestazione : prestazioni) {
            agendaListModel.addElement(prestazione.toString());
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(panel, "Tornare alla pagina di login?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Window currentWindow = SwingUtilities.getWindowAncestor(panel);
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        login.showLogin(controller);
    }

    private String leggi(JTextField field) {
        return field.getText() != null ? field.getText().trim() : "";
    }

    private void aggiornaStato(String messaggio) {
        statusLabel.setText(messaggio);
    }

    private void mostraAvviso(String messaggio) {
        JOptionPane.showMessageDialog(panel, messaggio, "Dashboard medico", JOptionPane.WARNING_MESSAGE);
        aggiornaStato(messaggio);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Dashboard medico");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Medico medicoView = new Medico();
                frame.setContentPane(medicoView.getContentPane());
                frame.setMinimumSize(new Dimension(980, 640));
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), 12, 12));
        panel.setBackground(new Color(-657414));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Dialog", Font.BOLD, 22, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Dashboard medico");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusLabel = new JLabel();
        statusLabel.setText("Gestisci prestazioni e agenda.");
        panel.add(statusLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(9, 2, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Registrazione prestazioni"));
        final JLabel label2 = new JLabel();
        label2.setText("Matricola medico");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        medicoMatricolaField = new JTextField();
        panel1.add(medicoMatricolaField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Codice ricovero");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ricoveroCodiceField = new JTextField();
        panel1.add(ricoveroCodiceField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Numero prestazione");
        panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numPrestazioneField = new JTextField();
        panel1.add(numPrestazioneField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Data inizio");
        panel1.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataInizioField = new JTextField();
        panel1.add(dataInizioField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Data fine");
        panel1.add(label6, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataFineField = new JTextField();
        panel1.add(dataFineField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Esito");
        panel1.add(label7, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        esitoField = new JTextField();
        panel1.add(esitoField, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Prestazione");
        panel1.add(label8, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tipoVisitaField = new JTextField();
        panel1.add(tipoVisitaField, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Sala operatoria");
        panel1.add(label9, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        salaOperatoriaField = new JTextField();
        panel1.add(salaOperatoriaField, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 8, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registraVisitaButton = new JButton();
        registraVisitaButton.setText("Registra visita");
        panel2.add(registraVisitaButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registraInterventoButton = new JButton();
        registraInterventoButton.setText("Registra intervento");
        panel2.add(registraInterventoButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aggiornaEsitoButton = new JButton();
        aggiornaEsitoButton.setText("Aggiorna esito");
        panel2.add(aggiornaEsitoButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Agenda medico"));
        final JLabel label10 = new JLabel();
        label10.setText("Data agenda");
        panel3.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        agendaDataField = new JTextField();
        panel3.add(agendaDataField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(170, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 8, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        agendaGiornalieraButton = new JButton();
        agendaGiornalieraButton.setText("Agenda giornaliera");
        panel4.add(agendaGiornalieraButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        agendaSettimanaleButton = new JButton();
        agendaSettimanaleButton.setText("Agenda settimanale");
        panel4.add(agendaSettimanaleButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setText("Logout");
        panel4.add(logoutButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        agendaList = new JList();
        panel3.add(agendaList, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(260, 120), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
