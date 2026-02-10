import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DSGVO_GUI extends JFrame {

    private JTextField idField;
    private JTextArea outputArea;

    public DSGVO_GUI() {
        setTitle("DSGVO Kundenverwaltung");
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Oberer Bereich (Eingabe)
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Kunden-ID:"));
        idField = new JTextField(10);
        topPanel.add(idField);

        JButton anzeigenBtn = new JButton("Daten anzeigen");
        JButton loeschenBtn = new JButton("Kunde löschen");

        topPanel.add(anzeigenBtn);
        topPanel.add(loeschenBtn);

        add(topPanel, BorderLayout.NORTH);

        // Ausgabefeld
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Button-Aktionen
        anzeigenBtn.addActionListener(e -> zeigeKundendaten());
        loeschenBtn.addActionListener(e -> loescheKunde());
    }

    private Connection getConnection() throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");

        String url = "jdbc:mariadb://localhost:3306/mysql"; // in heidi steht da glaube server id name oder so
        String user = "root"; //name in heidi
        String password = "root"; // da steht passwort in heidi

        return DriverManager.getConnection(url, user, password);
    }

    private void zeigeKundendaten() {
        try {
            int id = Integer.parseInt(idField.getText());

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
                                    "Adresse: " + rs.getString("strasse") + " " +
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

    private void loescheKunde() { //Nach dem Löschen muss Heidi neugestartet werden
        try {
            int id = Integer.parseInt(idField.getText().trim());

            String sql = "DELETE FROM kundendaten WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();

                System.out.println("Gelöschte Zeilen: " + rows);

                if (rows > 0) {
                    outputArea.setText("Kunde erfolgreich gelöscht.");
                } else {
                    outputArea.setText("Kunde nicht gefunden.");
                }
            }

        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DSGVO_GUI app = new DSGVO_GUI();
            app.setVisible(true);
        });
    }
}
