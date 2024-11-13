package attendanceSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class StudentMenu {
    private AttendanceSystem attendanceSystem; 
    private Connection conn;
    private Scanner scanner;

    public StudentMenu(Connection conn, AttendanceSystem attendanceSystem) {
        this.conn = conn;
        this.attendanceSystem = attendanceSystem;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
    	final String BLACK = "\u001B[30m";
    	final String ITALIC = "\033[3m";
        final String RESET = "\u001B[0m";
        
        while (true) {
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\t\t\t\t\t\t\tStudent Menu:");
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\t\t\t\t\t\t\t[1] Check In Attendance");
            System.out.println("\t\t\t\t\t\t\t[2] View Attendance");
            System.out.println("\t\t\t\t\t\t\t[3] Return to Main Menu");

            int choice;
            while (true) {
                System.out.println("\n\t\t\t" + "=".repeat(70));
            	System.out.print(BLACK +"\n\t\t\t\t\t\t Choose mode:  " + RESET);
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline character after int
                    break;
                } else {
                    System.out.println(ITALIC + "\t\t\t\t\tInvalid input. Please enter a number between 1 and 3." + RESET);
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
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void checkInAttendance() {
        System.out.print("Enter your Student ID: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        System.out.print("Enter Block ID: ");
        String blockId = scanner.nextLine().toUpperCase().trim();

        // Check if student exists
        if (!isStudentValid(studentId, blockId)) {
            System.out.println("Invalid Student ID or Block ID.");
            return;
        }

        // Generate the session ID (blockId_date)
        String date = getCurrentDate();
        String sessionId = blockId + "_" + getCurrentDate();

        // Check if the student has already checked in for the session
        if (hasCheckedIn(studentId, sessionId)) {
            System.out.println("You have already checked in for this session.");
            return;
        }

        String checkInTime = getCurrentTime();

        // Determine status (Present, Late, Absent)
        String status = determineStatus(blockId, checkInTime);

        // Record the attendance
        recordAttendance(studentId, blockId, sessionId, checkInTime, date, status);

        // Show the attendance record
        System.out.println("Attendance Recorded: " + studentId + " | " + blockId + " | " + sessionId + " | " + checkInTime + " | " + status);
    }

    private void viewAttendance() {
        System.out.print("Enter Session ID to view attendance: ");
        String sessionId = scanner.nextLine().trim();

        // Fetch and display the attendance for the session
        displayAttendance(sessionId);
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
            System.out.println("Error in isStudentValid: " + e.getMessage());
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
            System.out.println("Error in hasCheckedIn: " + e.getMessage());
        }
        return false;
    }

    private void recordAttendance(String studentId, String blockId, String sessionId, String checkInTime, String date, String status) {
        String sql = "INSERT INTO attendance (student_id, block_id, session_id, time_in, date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, blockId);
            stmt.setString(3, sessionId);
            stmt.setString(4, checkInTime);
            stmt.setString(5, date);
            stmt.setString(6, status);
            stmt.executeUpdate();
            System.out.println("Attendance for " + studentId + " has been successfully recorded as " + status);
        } catch (SQLException e) {
            System.out.println("Error in recordAttendance: " + e.getMessage());
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
                        return "P";  // Present
                    } else if (!checkIn.isBefore(start.plusMinutes(31)) && checkIn.isBefore(end)) {
                        return "L";  // Late
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in determineStatus: " + e.getMessage());
        }
        return "A";  // Default to Absent
    }

    void displayAttendance(String sessionId) {
        String sql = "SELECT * FROM attendance WHERE session_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
            	boolean hasRecords = false;
                while (rs.next()) {
                	hasRecords = true;
                    System.out.println("Student ID: " + rs.getString("student_id") +
                                       ", Block ID: " + rs.getString("block_id") +
                                       ", Time In: " + rs.getString("time_in") +
                                       ", Status: " + rs.getString("status"));
                }
                if (!hasRecords) {
                    System.out.println("No attendance records found for this session.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in displayAttendance: " + e.getMessage());
        }
    }
    
    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
    
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
