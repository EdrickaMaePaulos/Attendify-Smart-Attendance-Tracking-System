package Attendify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import Attendify.models.Block;
import Attendify.models.Student;
import Attendify.models.Subject;

public class StudentMenu {
    AttendanceSystem attendanceSystem;
    AdminMenu adminMenu;
    Connection conn;
    private final Scanner scanner;
    final static String BLACK = "\u001B[30m";
    final static String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";


    public StudentMenu(Connection conn, AttendanceSystem attendanceSystem) {
        this.conn = conn;
        this.attendanceSystem = attendanceSystem;
        this.adminMenu = new AdminMenu(conn, attendanceSystem);
        this.scanner = new Scanner(System.in);

    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\t\t\t\t\t\tS T U D E N T  M E N U");
            System.out.println("\t\t\t" + "=".repeat(70));
            System.out.println("\n\t\t\t\t\t\t[1] Check In Attendance");
            System.out.println("\t\t\t\t\t\t[2] View Attendance");
            System.out.println("\t\t\t\t\t\t[3] Return to Main Menu");

            int choice = adminMenu.getValidatedChoice(3);

            switch (choice) {
                case 1 -> checkInAttendance();
                case 2 -> {
                    AttendanceManager attendanceManager = new AttendanceManager(conn, scanner);
                    attendanceManager.displayOptions();
                    int subChoice = adminMenu.getValidatedChoice(3);
                    adminMenu.handleAttendanceChoice(attendanceManager, subChoice);
                }
                case 3 -> {
                    if (attendanceSystem != null) {
                        attendanceSystem.mainMenu();
                    }
                    return;
                }
                default -> System.out.println(ITALIC + "\t\t\t\t\t\t(Invalid choice.)" + RESET);
            }
        }
    }

    private void checkInAttendance() {
        System.out.println("\n\t\t\t" + "=".repeat(70));
        System.out.print("\n\t\t\t\t\tEnter Subject ID\t\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter Block \t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter your Student ID\t: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        String blockId = subjectId + "_" + blockName;

        if (!isStudentValid(studentId, blockId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\t(Invalid Student ID or Block ID.)" + RESET);
            return;
        }

        Student student = getStudent(studentId, blockId);
        if (student == null) {
            System.out.println(ITALIC + "\n\t\t\t\t\t(Invalid Student ID or Block ID.)" + RESET);
            return;
        }

        String date = getCurrentDate();
        String sessionId = blockId + "_" + date;
        if (hasCheckedIn(studentId, blockId)) {
            System.out.println(ITALIC + "\n\t\t\t\t     (You have already checked in for this session.)" + RESET);
            return;
        }

        String checkInTime = getCurrentTime();
        String status = determineStatus(blockId, checkInTime);
        recordAttendance(studentId,sessionId, subjectId, blockId,  checkInTime, date, status);
        System.out.println("\n\t\t\t\t\t\tAttendance Recorded: ");
        System.out.println("\t\t\t  " + studentId + " | " + blockId + " | " + sessionId + " | " + checkInTime + " | " + status);
        System.out.println(ITALIC + BLACK+ "\t\t\t\t\t\tP: Present \t L: Late \t A: Absent" +RESET);
    }

    private Student getStudent(String studentId, String blockId) {
        String sql = "SELECT student_name FROM students WHERE student_id = ? AND block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String studentName = rs.getString("student_name");
                    Block block = getBlockById(blockId);
                    return new Student(studentId, studentName, block);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in getStudent: " + e.getMessage());
        }
        return null;
    }

    private boolean isStudentValid(String studentId, String blockId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ? AND block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in isStudentValid: " + e.getMessage());
        }
        return false;
    }

    private boolean hasCheckedIn(String studentId, String sessionId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND session_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in hasCheckedIn: " + e.getMessage());
        }
        return false;
    }

    private void recordAttendance(String studentId, String sessionId, String subjectId, String blockId, String checkInTime, String date, String status) {
        String sql = "INSERT INTO attendance (student_id, session_id, subject_id, block_id,  time_in, date, status) VALUES (?, ?, ?,?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, sessionId);
            stmt.setString(3, subjectId);
            stmt.setString(4, blockId);
            stmt.setString(5, checkInTime);
            stmt.setString(6, date);
            stmt.setString(7, status);
            stmt.executeUpdate();
            System.out.println(ITALIC + "\n\t\t\tAttendance for " + studentId + " has been successfully recorded as " + status + RESET);
        } catch (SQLException e) {
            System.out.println("\t\t\tError in recordAttendance: " + e.getMessage());
        }
    }

    private String determineStatus(String blockId, String checkInTime) {
        String sql = "SELECT start_time, end_time FROM blocks WHERE block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalTime start = LocalTime.parse(rs.getString("start_time"));
                    LocalTime end = LocalTime.parse(rs.getString("end_time"));
                    LocalTime checkIn = LocalTime.parse(checkInTime);

                    if (!checkIn.isBefore(start) && checkIn.isBefore(start.plusMinutes(30))) {
                        return Student.STATUS_PRESENT;
                    } else if (!checkIn.isBefore(start.plusMinutes(31)) && checkIn.isBefore(end)) {
                        return Student.STATUS_LATE;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in determineStatus: " + e.getMessage());
        }
        return Student.STATUS_ABSENT;
    }

    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private Block getBlockById(String blockId) {
        String sql = "SELECT block_name, subject_id FROM blocks WHERE block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String blockName = rs.getString("block_name");
                    String subjectId = rs.getString("subject_id");
                    Subject subject = getSubjectById(subjectId);
                    return new Block(blockId, blockName, subject, null, null);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in getBlockById: " + e.getMessage());
        }
        return null;
    }

    private Subject getSubjectById(String subjectId) {
        String sql = "SELECT subject_name FROM subjects WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(subjectId, rs.getString("subject_name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in getSubjectById: " + e.getMessage());
        }
        return null;
    }
}

