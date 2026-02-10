package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    // Datenbankverbindung
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        return DriverManager.getConnection(url, user, password);
    }

    // Kundendaten anzeigen
    public static void zeigeKundendaten(int id) {
        String sql = "SELECT * FROM kundendaten WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nKundendaten:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " +
                        rs.getString("vorname") + " " +
                        rs.getString("nachname"));
                System.out.println("Adresse: " +
                        rs.getString("strasse") + " " +
                        rs.getString("strassennummer") + ", " +
                        rs.getString("plz") + " " +
                        rs.getString("ort"));
            } else {
                System.out.println("Kunde nicht gefunden.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kunde löschen
    public static void loescheKunde(int id) {
        String sql = "DELETE FROM kundendaten WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Kunde erfolgreich gelöscht.");
            } else {
                System.out.println("Kunde nicht gefunden.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hauptprogramm
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("DSGVO Kundenverwaltung");
        System.out.println("1 = Kundendaten anzeigen");
        System.out.println("2 = Kunde löschen");
        System.out.print("Auswahl: ");

        int auswahl = scanner.nextInt();

        System.out.print("Kunden-ID: ");
        int id = scanner.nextInt();

        if (auswahl == 1) {
            zeigeKundendaten(id);
        } else if (auswahl == 2) {
            loescheKunde(id);
        } else {
            System.out.println("Ungültige Auswahl.");
        }

        scanner.close();
    }
}
