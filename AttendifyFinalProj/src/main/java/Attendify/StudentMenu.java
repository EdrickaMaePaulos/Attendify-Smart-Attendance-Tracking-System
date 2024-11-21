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
    private final AttendanceSystem attendanceSystem;
    private final Connection conn;
    private final Scanner scanner;
    final static String BLACK = "\u001B[30m";
    final static String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";


    public StudentMenu(Connection conn, AttendanceSystem attendanceSystem) {
        this.conn = conn;
        this.attendanceSystem = attendanceSystem;
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

            int choice;
            while (true) {
                System.out.println("\n\t\t\t\t" + "=".repeat(50));
                System.out.print(BLACK +"\n\t\t\t\t\t\t Choose mode:  " + RESET);
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline character after int
                    break;
                } else {
                    System.out.println(ITALIC + "\n\t\t\t\t\tInvalid input. Please enter a number between 1 and 3." + RESET);
                    scanner.next();  // Clear invalid input
                }
            }

            switch (choice) {
                case 1 -> checkInAttendance();
                case 2 -> viewAttendance();
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
        System.out.print("\n\t\t\t\t\tEnter Subject ID\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter Block \t\t: ");
        String blockName = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter your Student ID\t: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        String blockId = subjectId + "_" + blockName;
        // Check if student exists
        if (!isStudentValid(studentId, blockId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\t(Invalid Student ID or Block ID.)" + RESET);
            return;
        }

        Student student = getStudent(studentId, blockId);


        // Generate the session ID (blockId_date)
        String date = getCurrentDate();
        String sessionId = blockId + "_" + date;

        // Check if the student has already checked in for the session
        if (hasCheckedIn(studentId, sessionId)) {
            System.out.println(ITALIC + "\n\t\t\t\t     (You have already checked in for this session.)" + RESET);
            return;
        }

        String checkInTime = getCurrentTime();

        // Determine status (Present, Late, Absent)
        String status = determineStatus(blockId, checkInTime);

        // Record the attendance
        recordAttendance(studentId,sessionId, subjectId, blockId,  checkInTime, date, status);

        // Show the attendance record
        System.out.println("\n\t\t\t\t\t\tAttendance Recorded: ");
        System.out.println("\t\t\t  " + studentId + " | " + blockId + " | " + sessionId + " | " + checkInTime + " | " + status);
        System.out.println(ITALIC + BLACK+ "\t\t\t\t\t\tP: Present \t L: Late \t A: Absent" +RESET);

    }

    public void viewAttendance() {
        System.out.println("\n\t\t\t" + "=".repeat(70));
        System.out.println(ITALIC + BLACK + "\n\t\t\t\t\tFormat: CourseCode_Block_Year-Month-Day" + RESET);
        System.out.print("\t\t\t\tEnter Session ID to view attendance: ");
        String sessionId = scanner.nextLine().trim();

        // Fetch and display the attendance for the session
        displayAttendance(sessionId);
    }
    private Student getStudent(String studentId, String blockId) {
        String sql = "SELECT student_name FROM students WHERE student_id = ? AND block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String studentName = rs.getString("student_name");
                    Block block = getBlockById(blockId); // Assuming this method exists
                    return new Student(studentId, studentName, block);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in getStudent: " + e.getMessage());
        }
        return null; // Return null if student is not found
    }

    private boolean isStudentValid(String studentId, String blockId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ? AND block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;  // Student exists in the given block
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
                    return true;  // Student has already checked in for the session
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
        // Retrieve start and end time for the block
        String sql = "SELECT start_time, end_time FROM blocks WHERE block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalTime start = LocalTime.parse(rs.getString("start_time"));
                    LocalTime end = LocalTime.parse(rs.getString("end_time"));
                    LocalTime checkIn = LocalTime.parse(checkInTime);

                    if (!checkIn.isBefore(start) && checkIn.isBefore(start.plusMinutes(30))) {
                        return Student.STATUS_PRESENT;  // Present
                    } else if (!checkIn.isBefore(start.plusMinutes(31)) && checkIn.isBefore(end)) {
                        return Student.STATUS_LATE;  // Late
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in determineStatus: " + e.getMessage());
        }
        return Student.STATUS_ABSENT;  // Default to Absent
    }

    void displayAttendance(String sessionId) {
        String sql = "SELECT * FROM attendance WHERE session_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasRecords = false;
                while (rs.next()) {
                    hasRecords = true;
                    System.out.println("\n\t\t\tStudent ID: " + rs.getString("student_id") +
                            ", Block ID: " + rs.getString("block_id") +
                            ", Time In: " + rs.getString("time_in") +
                            ", Status: " + rs.getString("status"));
                }
                if (!hasRecords) {
                    System.out.println(ITALIC + "\n\t\t\t\t\tNo attendance records found for this session.");
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displayAttendance: " + e.getMessage());
        }
    }

    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private Block getBlockById(String blockId) {
        // Assuming you have a method to retrieve Block details based on blockId.
        String sql = "SELECT block_name, subject_id FROM blocks WHERE block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String blockName = rs.getString("block_name");
                    String subjectId = rs.getString("subject_id");
                    // Assuming you have a method to get the Subject object
                    Subject subject = getSubjectById(subjectId);
                    return new Block(blockId, blockName, subject, null, null); // Assuming start and end time are not needed here
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in getBlockById: " + e.getMessage());
        }
        return null; // Return null if block is not found
    }

    private Subject getSubjectById(String subjectId) {
        // Implement logic to fetch the Subject from the database
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
        return null; // Return null if no subject is found
    }
}

