package Attendify;


import java.sql.*;
import java.util.Scanner;

public class AttendanceManager implements Displayables {
    AdminMenu adminMenu;
    private final Connection conn;
    private final Scanner scanner;
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";

    public AttendanceManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
        this.adminMenu = new AdminMenu(conn, null);
    }

    @Override
    public void displayOptions() {
        System.out.println("\n\t\t\t\t\t\tChoose Display Option:");
        System.out.println("\n\t\t\t\t\t\t[1] List all attendance records");
        System.out.println("\t\t\t\t\t\t[2] Display attendance by ID");
        System.out.println("\t\t\t\t\t\t[3] Return");
    }

    @Override
    public void listAll() {
        String query = "SELECT * FROM attendance";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n\t\t\tAll Attendance Records:\n");
            while (rs.next()) {
                System.out.println("\t\t\tStudent ID: \t[" + rs.getString("student_id") +
                        "]\t Block ID: \t[" + rs.getString("block_id") +
                        "]\t Date: \t[" + rs.getString("date") +
                        "]\t Time In: \t[" + rs.getString("time_in") +
                        "]\t Status: \t[" + rs.getString("status") +
                        "]");
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in listAllAttendance(): " + e.getMessage());
        }
    }

    @Override
    public void displayById() {
        System.out.println("\n\t\t\t\t\t\tChoose Attendance Search Option:");
        System.out.println("\t\t\t\t\t\t[1] By Session ID");
        System.out.println("\t\t\t\t\t\t[2] By Student ID");
        System.out.println("\t\t\t\t\t\t[3] Return");

        int choice = adminMenu.getValidatedChoice(3);

        switch (choice) {
            case 1 -> displayBySessionId();
            case 2 -> displayByStudentId();
            case 3 -> System.out.println("\t\t\tReturning to the main menu...");
            default -> System.out.println("\t\t\tInvalid choice. Please try again.");
        }
    }

    private void displayBySessionId() {
        System.out.println("\n\t\t\t" + "=".repeat(70));
        System.out.print("\n\t\t\t\t\tEnter subject ID for the block\t\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter block name\t\t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase();
        System.out.println("\t\t\t\t\t\t\tFormat: [YEAR-MONTH-DAY]");
        System.out.print("\t\t\t\t\tEnter Date [0000-00-00]\t\t\t:");
        String date = scanner.nextLine();
        String sessionId = subjectId+ "_" + blockName + "_" + date;

        String query = "SELECT * FROM attendance WHERE session_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n\t\t\tAttendance Records for Session ID: " + sessionId + "\n");
                boolean hasRecords = false;
                while (rs.next()) {
                    hasRecords = true;
                    System.out.println("\t\t\t[Student ID] \t[" + rs.getString("student_id") +
                            "\t [Block ID] \t[" + rs.getString("block_id") +
                            "\t [Date] \t[" + rs.getString("date") +
                            "\t [Time In] \t[" + rs.getString("time_in") +
                            "\t [Status] \t[" + rs.getString("status"));
                }
                if (!hasRecords) {
                    System.out.println(ITALIC + "\t\t\tNo attendance records found for this session." + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displayBySessionId(): " + e.getMessage());
        }
    }

    private void displayByStudentId() {
        System.out.print("\n\t\t\t\t\tEnter Student ID: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        String query = "SELECT * FROM attendance WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n\t\t\tAttendance Records for Student ID: " + studentId + "\n");
                boolean hasRecords = false;
                while (rs.next()) {
                    hasRecords = true;
                    System.out.println("\t\t\t[Student ID] \t" + rs.getString("student_id") +
                            "\t [Block ID] \t" + rs.getString("block_id") +
                            "\t [Date] \t" + rs.getString("date") +
                            "]\t [Time In] \t" + rs.getString("time_in") +
                            "]\t [Status] \t" + rs.getString("status") +
                            "]");
                }
                if (!hasRecords) {
                    System.out.println(ITALIC + "\t\t\tNo attendance records found for this student." + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displayByStudentId(): " + e.getMessage());
        }
    }
}

