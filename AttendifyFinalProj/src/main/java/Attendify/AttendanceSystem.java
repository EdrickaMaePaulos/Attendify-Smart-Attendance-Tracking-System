package Attendify;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AttendanceSystem {
    private final Connection conn;

    public AttendanceSystem() {
        conn = DatabaseConnection.connect();  // Connect to the database
        if (conn != null && DatabaseConnection.verifyConnection(conn)) {  // Verify the connection
            createTables();  // Create tables if connection is verified
        }
    }
    public void createTables() {
        final String ITALIC = "\033[3m";
        final String RESET = "\033[0m";

        String createSubjectsTable = "CREATE TABLE IF NOT EXISTS subjects (" +
                "subject_id VARCHAR(100) PRIMARY KEY, " +
                "subject_name VARCHAR(100) NOT NULL" +
                ");";

        String createBlocksTable = "CREATE TABLE IF NOT EXISTS blocks (" +
                "block_id VARCHAR(100) PRIMARY KEY, " +
                "subject_id VARCHAR(100) NOT NULL, " +
                "block_name VARCHAR(100) NOT NULL, " +
                "start_time TIME NOT NULL, " +
                "end_time TIME NOT NULL, " +
                "FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE" +
                ");";

        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id VARCHAR(100) NOT NULL, " +
                "student_name VARCHAR(255) NOT NULL, " +
                "block_id VARCHAR(100) NOT NULL, " +
                "PRIMARY KEY (student_id, block_id)," +
                "FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE" +
                ");";

        String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                "attendance_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "session_id VARCHAR(100) NOT NULL, " + // New column for session_id
                "student_id VARCHAR(100) NOT NULL, " +
                "subject_id VARCHAR (100) NOT NULL, " +
                "block_id VARCHAR(100) NOT NULL, " +
                "date VARCHAR(100) NOT NULL, " +
                "time_in TIME NOT NULL, " +
                "status ENUM('P', 'L', 'A') NOT NULL, " +
                "FOREIGN KEY (student_id, block_id) REFERENCES students(student_id, block_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE, " +
                "UNIQUE (student_id, block_id, session_id) " + // Enforcing unique combination of student_id and session_id
                ");";

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(createSubjectsTable);
            statement.executeUpdate(createBlocksTable);
            statement.executeUpdate(createStudentsTable);
            statement.executeUpdate(createAttendanceTable);
            System.out.println(ITALIC + "\t\t\t\t\t\t  (Tables created successfully.)" + RESET);
        } catch (SQLException e) {
            System.out.println(ITALIC + "Error in creating tables: " + e.getMessage() + RESET);
        }
    }



    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            final String BLACK = "\u001B[30m";
            final String ITALIC = "\033[3m";
            final String RESET = "\u001B[0m";
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\n\t\t\t\t\t\t[1] Student Menu");
            System.out.println("\t\t\t\t\t\t[2] Admin Menu");
            System.out.println("\t\t\t\t\t\t[3] Exit\n");
            System.out.println("\n\t\t\t\t" + "=".repeat(50));
            System.out.print(BLACK +"\n\t\t\t\t\t\t Choose mode:  " + RESET);

            int choice = scanner.nextInt();


            switch (choice) {
                case 1:
                    StudentMenu studentMenu = new StudentMenu(conn, this);
                    studentMenu.displayMenu();
                    break;
                case 2:
                    System.out.println("\n\t\t\t" + "=".repeat(70));
                    System.out.print("\n\t\t\t\t\tEnter admin username\t: ");
                    String username = scanner.next();
                    System.out.print("\t\t\t\t\tEnter admin password\t: ");
                    String password = scanner.next();

                    if (adminLogin(username, password)) {
                        AdminMenu adminMenu = new AdminMenu(conn, this);
                        adminMenu.displayMenu();
                    } else {
                        System.out.println(ITALIC + "\t\t\t\t\t\tInvalid admin credentials." + RESET);
                    }
                    break;
                case 3:
                    exit = true;
                    System.out.println(ITALIC + "\t\t\t\t\t\tExiting the system." + RESET);
                    break;
                default:
                    System.out.println(ITALIC + "\t\t\t\t\t\t\tInvalid choice." + RESET);
                    break;
            }
        }
        scanner.close();
    }
    public void close() {
        DatabaseConnection.closeConnection(conn);
    }

    private boolean adminLogin(String username, String password) {
        return username.equals("admin") && password.equals("password123");
    }
}

