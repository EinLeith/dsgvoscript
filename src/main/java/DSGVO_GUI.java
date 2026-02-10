import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DSGVO_GUI extends JFrame {

    // Felder
    private JTextField idField;
    private JTextArea outputArea;

    private JTextField vornameField;
    private JTextField nachnameField;
    private JTextField strasseField;
    private JTextField nummerField;
    private JTextField plzField;
    private JTextField ortField;

    public DSGVO_GUI() {
        setTitle("DSGVO Kundenverwaltung");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Oberes Panel (ID + Buttons)
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Kunden-ID:"));
        idField = new JTextField(8);
        topPanel.add(idField);

        JButton anzeigenBtn = new JButton("Daten anzeigen");
        JButton loeschenBtn = new JButton("Kunde löschen");
        JButton hinzufuegenBtn = new JButton("Kunde hinzufügen");

        topPanel.add(anzeigenBtn);
        topPanel.add(loeschenBtn);
        topPanel.add(hinzufuegenBtn);

        add(topPanel, BorderLayout.NORTH);

        // Formular für neuen Kunden
        JPanel formPanel = new JPanel(new GridLayout(6, 2));

        formPanel.add(new JLabel("Vorname:"));
        vornameField = new JTextField();
        formPanel.add(vornameField);

        formPanel.add(new JLabel("Nachname:"));
        nachnameField = new JTextField();
        formPanel.add(nachnameField);

        formPanel.add(new JLabel("Straße:"));
        strasseField = new JTextField();
        formPanel.add(strasseField);

        formPanel.add(new JLabel("Nr:"));
        nummerField = new JTextField();
        formPanel.add(nummerField);

        formPanel.add(new JLabel("PLZ:"));
        plzField = new JTextField();
        formPanel.add(plzField);

        formPanel.add(new JLabel("Ort:"));
        ortField = new JTextField();
        formPanel.add(ortField);

        add(formPanel, BorderLayout.CENTER);

        // Ausgabefeld
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Button-Aktionen
        anzeigenBtn.addActionListener(e -> zeigeKundendaten());
        loeschenBtn.addActionListener(e -> loescheKunde());
        hinzufuegenBtn.addActionListener(e -> kundeHinzufuegen());
    }

    private Connection getConnection() throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");

        String url = "jdbc:mariadb://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        return DriverManager.getConnection(url, user, password);
    }

    private void zeigeKundendaten() {
        try {
            int id = Integer.parseInt(idField.getText().trim());

            String sql = "SELECT * FROM kundendaten WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    outputArea.setText(
                            "ID: " + rs.getInt("id") + "\n" +
                                    "Name: " + rs.getString("vorname") + " " +
                                    rs.getString("nachname") + "\n" +
                                    "Adresse: " +
                                    rs.getString("strasse") + " " +
                                    rs.getString("strassennummer") + ", " +
                                    rs.getString("plz") + " " +
                                    rs.getString("ort")
                    );
                } else {
                    outputArea.setText("Kunde nicht gefunden.");
                }
            }

        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
        }
    }

    private void loescheKunde() {
        try {
            int id = Integer.parseInt(idField.getText().trim());

            String sql = "DELETE FROM kundendaten WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    outputArea.setText("Kunde erfolgreich gelöscht.");
                } else {
                    outputArea.setText("Kunde nicht gefunden.");
                }
            }

        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
        }
    }

    private void kundeHinzufuegen() {
        try {
            String sql = "INSERT INTO kundendaten " +
                    "(vorname, nachname, strasse, strassennummer, plz, ort) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, vornameField.getText().trim());
                stmt.setString(2, nachnameField.getText().trim());
                stmt.setString(3, strasseField.getText().trim());
                stmt.setString(4, nummerField.getText().trim());
                stmt.setString(5, plzField.getText().trim());
                stmt.setString(6, ortField.getText().trim());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int neueId = rs.getInt(1);
                    outputArea.setText("Kunde hinzugefügt. Neue ID: " + neueId);
                }
            }

        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DSGVO_GUI app = new DSGVO_GUI();
            app.setVisible(true);
        });
    }
}
