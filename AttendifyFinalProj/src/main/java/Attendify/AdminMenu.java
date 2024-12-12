package Attendify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import Attendify.models.Block;
import Attendify.models.Student;
import Attendify.models.Subject;


public class AdminMenu {
    private final Connection conn;
    SubjectManager subjectManager;
    BlockManager blockManager;
    StudentManager studentManager;
    private final AttendanceSystem attendanceSystem;
    private final Scanner scanner;
    final String BLACK = "\u001B[30m";
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";



    public AdminMenu(Connection conn, AttendanceSystem attendanceSystem) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
        this.attendanceSystem = attendanceSystem;
        this.studentManager = new StudentManager(conn, scanner);
        this.blockManager = new BlockManager(conn, scanner);
        this.subjectManager = new SubjectManager(conn, scanner);

    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\t\t\t\t\t\tA D M I N  M E N U ");
            System.out.println("\t\t\t" + "=".repeat(70));
            System.out.println("\n\t\t\t\t\t\t[1] Add Subject");
            System.out.println("\t\t\t\t\t\t[2] Remove Subject");
            System.out.println("\t\t\t\t\t\t[3] Add Block");
            System.out.println("\t\t\t\t\t\t[4] Remove Block");
            System.out.println("\t\t\t\t\t\t[5] Add Student");
            System.out.println("\t\t\t\t\t\t[6] Remove Student");
            System.out.println("\t\t\t\t\t\t[7] Display");
            System.out.println("\t\t\t\t\t\t[8] Return to Main Menu");

            int choice = getValidatedChoice(8);

            try {
                switch (choice) {
                    case 1 -> addSubject();
                    case 2 -> removeSubject();
                    case 3 -> addBlock();
                    case 4 -> removeBlock();
                    case 5 -> addStudent();
                    case 6 -> removeStudent();
                    case 7 -> display();
                    case 8 -> {
                        if (attendanceSystem != null) {
                            attendanceSystem.mainMenu();
                        }
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }catch (NullPointerException e) {
                System.err.println("NullPointerException: " + e.getMessage());
            }
        }
    }
    public void studentDisplay(){
        System.out.println("\n");
        studentManager.listAll();
    }
    public void subjectDisplay(){
        System.out.println("\n");

        subjectManager.listAll();
    }
    public void blockDisplay(){
        System.out.println("\n");
        blockManager.listAll();
    }

    public int getValidatedChoice(int max) {
        int choice;
        while (true) {
            System.out.println("\n\t\t\t\t" + "=".repeat(50));
            System.out.print(BLACK +"\n\t\t\t\t\t\t\t Choice:  " + RESET);
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= max) {
                    return choice;
                }
            } else {
                scanner.next();
            }
            System.out.println("\t\t\t\tInvalid input. Please enter a number between " + 1 + " and " + max + ".");
        }
    }

    private boolean doesSubjectExist(String subjectId) {
        String checkSubjectSql = "SELECT COUNT(*) FROM subjects WHERE subject_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSubjectSql)) {
            checkStmt.setString(1, subjectId);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in checking subject existence: " + e.getMessage());
        }
        return false;
    }

    private boolean doesBlockExist(String blockId) {
        String checkBlockSql = "SELECT COUNT(*) FROM blocks WHERE block_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkBlockSql)) {
            checkStmt.setString(1, blockId);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in checking block existence: " + e.getMessage());
        }
        return false;
    }

    private boolean doesStudentExist(String studentId, String blockId) {
        String checkStudentSql = "SELECT COUNT(*) FROM students WHERE student_id = ? AND block_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkStudentSql)) {
            checkStmt.setString(1, studentId);
            checkStmt.setString(2, blockId);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in checking student existence: " + e.getMessage());
        }
        return false;
    }

    private void addSubject() {
        subjectDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject code (alphanumeric)\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        if (doesSubjectExist(subjectId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\t\tSubject with ID " + subjectId + " already exists" + RESET);
            return;
        }
        System.out.print("\t\t\t\t\tEnter subject name\t\t\t\t\t: ");
        String subjectName = scanner.nextLine().toUpperCase();
        Subject subject = new Subject(subjectName, subjectId);
        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO subjects (subject_id, subject_name) VALUES (?, ?)")) {
            statement.setString(1, subject.getId());
            statement.setString(2, subject.getName());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(ITALIC + "\n\t\t\t\t\t\tSubject added successfully." + RESET);
                subjectDisplay();
            } else {
                System.out.println("\n\t\t\t\t\t\tFailed to add subject.");
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in addSubject(): " + e.getMessage());
        }
    }

    private void removeSubject() {
        subjectDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject ID to remove: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();

        if (!doesSubjectExist(subjectId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\tSubject ID " + subjectId + " does not exist." + RESET);
            return;
        }

        System.out.print("\n\t\t\tPress [1] to delete all related data or [2] to delete only the subject: ");
        int choice = getValidatedChoice(2);
        System.out.print("\n");

        try {
            if (choice == 1) {
                String removeBlocksSql = "DELETE FROM blocks WHERE subject_id = ?";
                try (PreparedStatement removeBlocksStmt = conn.prepareStatement(removeBlocksSql)) {
                    removeBlocksStmt.setString(1, subjectId);
                    int blocksAffected = removeBlocksStmt.executeUpdate();
                    System.out.println(ITALIC + "\t\t\t\t\t" + blocksAffected + " blocks removed for subject ID: " + subjectId + RESET);
                }

                String removeAttendanceSql = "DELETE FROM attendance WHERE subject_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, subjectId);
                    int attendanceAffected = removeAttendanceStmt.executeUpdate();
                    System.out.println(ITALIC + "\t\t\t\t\t" + attendanceAffected + " attendance records removed for subject ID: " + subjectId + RESET);
                }
            }
            else if (choice == 2) {
                String removeAttendanceSql = "DELETE FROM attendance WHERE subject_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, subjectId);
                    int attendanceAffected = removeAttendanceStmt.executeUpdate();
                    System.out.println(ITALIC + "\t\t\t\t\t" + attendanceAffected + " attendance records removed for subject ID: " + subjectId + RESET);
                }
            }

            String removeSubjectSql = "DELETE FROM subjects WHERE subject_id = ?";
            try (PreparedStatement removeSubjectStmt = conn.prepareStatement(removeSubjectSql)) {
                removeSubjectStmt.setString(1, subjectId);
                int rowsAffected = removeSubjectStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\t\t\t\t\tSubject removed successfully.");
                    subjectDisplay();
                } else {
                    System.out.println("\t\t\t\t\tFailed to remove subject.");
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in removeSubject(): " + e.getMessage());
        }
    }

    public void addBlock() {
        blockDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject ID for the block\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter block name\t\t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase();
        System.out.print("\t\t\t\t\tEnter start time (HH:mm)\t\t: ");
        String startTime = scanner.nextLine();
        System.out.print("\t\t\t\t\tEnter end time (HH:mm)\t\t\t: ");
        String endTime = scanner.nextLine();

        String blockId = subjectId + "_" + blockName;

        Subject subject = getSubjectById(subjectId);
        if (subject == null) {
            System.out.println(ITALIC + "\n\t\t\t\t\tNo subject found with ID: " + subjectId + RESET);
            return;
        }
        Block block = new Block(blockId, blockName, subject, startTime, endTime);

        String sql = "INSERT INTO blocks (block_id, subject_id, block_name, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, block.getId());
            statement.setString(2, block.getSubjectId());
            statement.setString(3, block.getName());
            statement.setString(4, block.getStartTime());
            statement.setString(5, block.getEndTime());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(ITALIC + "\n\t\t\t\t\tBlock added successfully with ID: " + blockId + RESET);
                blockDisplay();
            } else {
                System.out.println(ITALIC + "\n\t\t\t\t\t\tFailed to add block." + RESET);
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in addBlock(): " + e.getMessage());
        }
    }

    private Subject getSubjectById(String subjectId) {
        String sql = "SELECT subject_id, subject_name FROM subjects WHERE subject_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, subjectId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("subject_name");
                return new Subject(name, subjectId);
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError retrieving subject: " + e.getMessage());
        }
        return null;
    }

    private void removeBlock() {
        blockDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject ID for the block\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter block name\t\t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase();
        String blockId = subjectId + "_" + blockName;

        if (!doesBlockExist(blockId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\tBlock ID " + blockId + " does not exist." + RESET);
            return;
        }

        System.out.println("\n\t\t\t\t\t\t[1] Attendance record");
        System.out.println("\t\t\t\t\t\t[2] Student record");
        System.out.println("\t\t\t\t\t\t[3] All data");
        System.out.println("\t\t\t\t\t\t[4] Subject only");
        System.out.println("\n\t\t\t\t\t\tWhat would you like to remove?");

        int choice = getValidatedChoice(4);

        try {
            if (choice == 1) {
                String removeAttendanceSql = "DELETE FROM attendance WHERE block_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, blockId);
                    removeAttendanceStmt.executeUpdate();
                    System.out.println(ITALIC + "\n\t\t\t\t\tAttendance records removed for block ID: " + blockId + RESET);
                }
            }
            else if (choice == 2 ) {
                String removeStudentsSql = "DELETE FROM students WHERE block_id = ?";
                try (PreparedStatement removeStudentsStmt = conn.prepareStatement(removeStudentsSql)) {
                    removeStudentsStmt.setString(1, blockId);
                    int studentRowsAffected = removeStudentsStmt.executeUpdate();
                    if (studentRowsAffected > 0) {
                        System.out.println(ITALIC + "\n\t\t\t\t\t"+ studentRowsAffected + " student(s) removed for block ID: " + blockId +RESET);
                    } else {
                        System.out.println(ITALIC + "\n\t\t\t\t\tNo students found under block ID: " + blockId + RESET);
                    }
                }
            }
            else if (choice == 3){
                String removeAttendanceSql = "DELETE FROM attendance WHERE block_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, blockId);
                    removeAttendanceStmt.executeUpdate();
                    System.out.println(ITALIC + "\n\t\t\t\t\tAttendance records removed for block ID: " + blockId + RESET);
                }
                String removeStudentsSql = "DELETE FROM students WHERE block_id = ?";
                try (PreparedStatement removeStudentsStmt = conn.prepareStatement(removeStudentsSql)) {
                    removeStudentsStmt.setString(1, blockId);
                    int studentRowsAffected = removeStudentsStmt.executeUpdate();
                    if (studentRowsAffected > 0) {
                        System.out.println(ITALIC + "\n\t\t\t\t\t"+ studentRowsAffected + " student(s) removed for block ID: " + blockId +RESET);
                    } else {
                        System.out.println(ITALIC + "\n\t\t\t\t\tNo students found under block ID: " + blockId + RESET);
                    }
                }
            }
            String removeBlockSql = "DELETE FROM blocks WHERE block_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(removeBlockSql)) {
                statement.setString(1, blockId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(ITALIC + "\n\t\t\t\t\tBlock removed successfully." + RESET);
                    System.out.println("\n\t\t\t" + "=".repeat(70));
                    blockDisplay();
                } else {
                    System.out.println(ITALIC + "\n\t\t\t\t\tFailed to remove block." + RESET);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("\t\t\tError in removeBlock(): " + e.getMessage());
        }
    }

    private void addStudent() {
        studentDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject ID for the block\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter block name\t\t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase();
        System.out.print("\t\t\t\t\tEnter student ID\t\t\t\t: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        String blockId = subjectId + "_" + blockName;
        if (doesStudentExist(studentId, blockId)) {
            System.out.println(ITALIC + "\n\t\t\t\t\tStudent with ID " + studentId + " already exists in block " + blockId + RESET);
            return;
        }

        System.out.print("\t\t\t\t\tEnter student name\t\t\t\t: ");
        String studentName = scanner.nextLine().toUpperCase();

        Block block = getBlockById(blockId);
        if (block == null) {
            System.out.println(ITALIC + "\n\t\t\t\t\tNo block found with ID: " + blockId + RESET);
            return;
        }

        Student student = new Student(studentId, studentName, block);

        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO students (student_id, student_name, block_id) VALUES (?, ?, ?)")) {
            statement.setString(1, student.getId());
            statement.setString(2, student.getName());
            statement.setString(3, block.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(ITALIC + "\n\t\t\t\t\tStudent added successfully to block " + blockId + RESET);
                studentDisplay();
            } else {
                System.out.println(ITALIC + "\n\t\t\t\t\t\tFailed to add student." + RESET);
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in addStudent(): " + e.getMessage());
        }
    }
    private Block getBlockById(String blockId) {
        String sql = "SELECT block_id, block_name, start_time, end_time, subject_id FROM blocks WHERE block_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, blockId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String blockName = resultSet.getString("block_name");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                String subjectId = resultSet.getString("subject_id");
                Subject subject = getSubjectById(subjectId);
                return new Block(blockId, blockName, subject, startTime, endTime);
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError retrieving block: " + e.getMessage());
        }
        return null;
    }

    private void removeStudent() {
        studentDisplay();
        System.out.print("\n\t\t\t\t\tEnter subject ID for the block\t: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("\t\t\t\t\tEnter block name\t\t\t\t: ");
        String blockName = scanner.nextLine().toUpperCase();
        System.out.print("\t\t\t\t\tEnter student ID\t\t\t\t: ");
        String studentId = scanner.nextLine().toUpperCase().trim();

        String blockId = subjectId + "_" + blockName;

        if (!doesStudentExist(studentId, blockId)) {
            System.out.println("\n\t\t\t\t\tStudent with ID " + studentId + " does not exist in block " + blockId);
            return;
        }

        System.out.println("\n\t\t\t\t\t[1] Delete from Attendance records");
        System.out.println("\t\t\t\t\t[2] Delete from Studentlist");
        System.out.print("\t\t\t\t\t[3] Delete from both");

        int choice = getValidatedChoice(3);

        try {

            if (choice == 1) {
                System.out.println("\t\t\t\t\t\t\tFormat: [YEAR-MONTH-DAY]");
                System.out.print("\n\t\t\t\t\tEnter Date [0000-00-00]\t\t\t:");
                String date = scanner.nextLine();
                String sessionId = blockId + "_" + date;
                String removeAttendanceSql = "DELETE FROM attendance WHERE student_id = ? AND session_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, studentId);
                    removeAttendanceStmt.setString(2, sessionId);
                    int rowsAffected = removeAttendanceStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println(ITALIC + "\n\t\t\t\t\tStudent attendance record removed successfully." + RESET);
                    } else {
                        System.out.println(ITALIC + "\n\t\t\t\t\tNo attendance record found for this session." + RESET);
                    }
                }
            }
            else if (choice == 2) {
                String removeStudentSql = "DELETE FROM students WHERE student_id = ? AND block_id = ?";
                try (PreparedStatement removeStudentStmt = conn.prepareStatement(removeStudentSql)) {
                    removeStudentStmt.setString(1, studentId);
                    removeStudentStmt.setString(2, blockId);
                    int rowsAffected = removeStudentStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println(ITALIC + "\n\t\t\t\t\tStudent removed from student list successfully." +RESET);
                    } else {
                        System.out.println(ITALIC + "\n\t\t\t\t\tFailed to remove student from student list." + RESET);
                    }
                }
            }
            else if (choice == 3) {
                System.out.println("\t\t\t\t\t\t\tFormat: [YEAR-MONTH-DAY]");
                System.out.print("\n\t\t\t\t\tEnter Date [0000-00-00]\t\t\t:");
                String date = scanner.nextLine();
                String sessionId = blockId + "_" + date;
                String removeAttendanceSql = "DELETE FROM attendance WHERE student_id = ? AND session_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, studentId);
                    removeAttendanceStmt.setString(2, sessionId);
                    removeAttendanceStmt.executeUpdate();
                    System.out.println(ITALIC + "\n\t\t\t\t\tStudent attendance record removed successfully." + RESET);
                }

                String removeStudentSql = "DELETE FROM students WHERE student_id = ? AND block_id = ?";
                try (PreparedStatement removeStudentStmt = conn.prepareStatement(removeStudentSql)) {
                    removeStudentStmt.setString(1, studentId);
                    removeStudentStmt.setString(2, blockId);
                    removeStudentStmt.executeUpdate();
                    System.out.println(ITALIC + "\n\t\t\t\t\tStudent removed from student list successfully." + RESET);
                    studentDisplay();
                }
            }
            else {
                System.out.println(ITALIC + "\n\t\t\t\t\t\tPlease choose a valid option [1, 2, or 3]." + RESET);
            }
        }
        catch (SQLException e) {
            System.out.println("\t\t\tError in removeStudent(): " + e.getMessage());
        }
    }
    private void display() {
        System.out.println("\n\t\t\t" + "=".repeat(70));
        System.out.println("\n\t\t\t\t\t\t[1] Display Subjects");
        System.out.println("\t\t\t\t\t\t[2] Display Blocks");
        System.out.println("\t\t\t\t\t\t[3] Display Students");
        System.out.println("\t\t\t\t\t\t[4] Display Attendance");
        System.out.println("\t\t\t\t\t\t[5] Return");

        int choice = getValidatedChoice(5);

        switch (choice) {
            case 1 ->{
                SubjectManager subjectManager = new SubjectManager(conn, scanner);
                subjectManager.displayOptions();
                int subChoice = getValidatedChoice(3);
                handleSubjectChoice(subjectManager, subChoice);
            }
            case 2 ->{
                BlockManager blockManager = new BlockManager(conn, scanner);
                blockManager.displayOptions();
                int subChoice = getValidatedChoice(3);
                handleBlockChoice(blockManager, subChoice);
            }
            case 3 -> {
                StudentManager studentManager = new StudentManager(conn, scanner);
                studentManager.displayOptions();
                int subChoice = getValidatedChoice(3);
                handleStudentChoice(studentManager, subChoice);
            }
            case 4 -> {
                AttendanceManager attendanceManager = new AttendanceManager(conn, scanner);
                attendanceManager.displayOptions();
                int subChoice = getValidatedChoice(3);
                handleAttendanceChoice(attendanceManager, subChoice);
            }
            case 5 -> displayMenu();
            default -> System.out.println("Invalid choice.");
        }
    }
    private void handleSubjectChoice(SubjectManager subjectManager, int choice) {
        switch (choice) {
            case 1 -> subjectManager.listAll();
            case 2 -> subjectManager.displayById();
            case 3 -> display();
            default -> System.out.println("Invalid choice.");
        }
    }
    private void handleBlockChoice(BlockManager blockManager, int choice) {
        switch (choice) {
            case 1 -> blockManager.listAll();
            case 2 -> blockManager.displayById();
            case 3 -> display();
            default -> System.out.println("Invalid choice.");
        }
    }
    private void handleStudentChoice(StudentManager studentManager, int choice) {
        switch (choice) {
            case 1 -> studentManager.listAll();
            case 2 -> studentManager.displayById();
            case 3 -> display();
            default -> System.out.println("Invalid choice.");
        }
    }
    public void handleAttendanceChoice(AttendanceManager attendanceManager, int choice) {
        switch (choice) {
            case 1 -> attendanceManager.listAll();
            case 2 -> attendanceManager.displayById();
            case 3 -> display();
            default -> System.out.println("Invalid choice.");
        }
    }
}



