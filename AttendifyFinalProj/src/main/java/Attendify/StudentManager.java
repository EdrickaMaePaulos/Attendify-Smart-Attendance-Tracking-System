package Attendify;

import Attendify.models.Block;
import Attendify.models.Student;
import Attendify.models.Subject;

import java.sql.*;
import java.util.Scanner;

class StudentManager implements Displayables {
    private final Connection conn;
    private final Scanner scanner;
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";

    public StudentManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    @Override
    public void displayOptions() {
        System.out.println("\n\t\t\t\t\t\tChoose Display Option:");
        System.out.println("\n\t\t\t\t\t\t[1] List all students");
        System.out.println("\t\t\t\t\t\t[2] Display student by ID");
        System.out.println("\t\t\t\t\t\t[3] Return");
    }

    @Override
    public void listAll() {
        String query = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n\t\t\t\t\t\t\tAll Students:");
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                String blockId = rs.getString("block_id");
                Student student = new Student(studentId, studentName, new Block (blockId, "", new Subject("", ""), "", ""));
                System.out.println(student.toString());
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in listAllStudents(): " + e.getMessage());
        }    }

    @Override
    public void displayById() {
        System.out.print("\n\t\t\t\t\tEnter Student ID: ");
        String studentId = scanner.nextLine().toUpperCase().trim();
        System.out.println("\n\t\t\t\t\t\t\tStudent:\n");
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String studentName = rs.getString("student_name");
                    String blockId = rs.getString("block_id");
                    Student student = new Student(studentId, studentName, new Block (blockId, "", new Subject("", ""), "", ""));
                    System.out.println(student.toString());
                } else {
                    System.out.println(ITALIC + "\n\t\t\t\t\tNo student found with ID: " + studentId + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displayStudentById(): " + e.getMessage());
        }    }
}
