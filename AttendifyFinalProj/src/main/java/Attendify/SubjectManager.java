package Attendify;

import Attendify.models.Subject;
import java.sql.*;
import java.util.Scanner;

class SubjectManager implements Displayables {
    private final Connection conn;
    private final Scanner scanner;
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";

    public SubjectManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    @Override
    public void displayOptions() {
        System.out.println("\n\t\t\t\t\t\tChoose Display Option:");
        System.out.println("\n\t\t\t\t\t\t[1] List all subjects");
        System.out.println("\t\t\t\t\t\t[2] Display subject by ID");
        System.out.println("\t\t\t\t\t\t[3] Return");
    }

    @Override
    public  void listAll() {
        String query = "SELECT * FROM subjects";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n\t\t\t\t\t\t\tAll Subjects:\n");
            while (rs.next()) {
                String subjectId = rs.getString("subject_id");
                String subjectName = rs.getString("subject_name");
                System.out.println(new Subject(subjectName, subjectId).toString());
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in listAllSubjects(): " + e.getMessage());
        }
    }

    @Override
    public void displayById() {
        System.out.print("\n\t\t\t\t\tEnter Subject ID\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.println("\n\t\t\t\t\t\t\tSubject:\n");
        String query = "SELECT * FROM subjects WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String subjectName = rs.getString("subject_name");
                    System.out.println(new Subject(subjectName, subjectId).toString());
                } else {
                    System.out.println(ITALIC + "\n\t\t\t\t\tNo subject found with ID: " + subjectId + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displaySubjectById(): " + e.getMessage());
        }    }
}
