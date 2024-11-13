package attendanceSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminMenu {
    private Connection conn;
    private AttendanceSystem attendanceSystem; 
    private Scanner scanner;


    public AdminMenu(Connection conn, AttendanceSystem attendanceSystem) {
        this.conn = conn;
        this.attendanceSystem = attendanceSystem;  // Store the instance
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
    	while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add Subject");
            System.out.println("2. Remove Subject");
            System.out.println("3. Add Block");
            System.out.println("4. Remove Block");
            System.out.println("5. Add Student");
            System.out.println("6. Remove Student");
            System.out.println("7. Return to Main Menu");

            int choice = getValidatedChoice(1, 7);   
            
            switch (choice) {
            case 1 -> addSubject();
            case 2 -> removeSubject();
            case 3 -> addBlock();
            case 4 -> removeBlock();
            case 5 -> addStudent();
            case 6 -> removeStudent();
            case 7 -> {
            	attendanceSystem.mainMenu();
            	return;
            }
            default -> System.out.println("Invalid choice.");
            }
    	}
    }
    
    private int getValidatedChoice(int min, int max) {
        int choice;
        while (true) {
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= min && choice <= max) {
                   return choice;
                }
            } else {
                scanner.next();
            }
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
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
            System.out.println("Error in checking subject existence: " + e.getMessage());
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
            System.out.println("Error in checking block existence: " + e.getMessage());
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
            System.out.println("Error in checking student existence: " + e.getMessage());
        }
        return false;
    }

    private void addSubject() {
    	System.out.print("Enter subject code (alphanumeric): ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        
        if (doesSubjectExist(subjectId)) {
            System.out.println("Subject with ID " + subjectId + " already exists");
            return;
        }
        
    	System.out.print("Enter subject name: ");
        String subjectName = scanner.nextLine().toUpperCase();
        
            
        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO subjects (subject_id, subject_name) VALUES (?, ?)")) {
            statement.setString(1, subjectId);
            statement.setString(2, subjectName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Subject added successfully.");
            } else {
                System.out.println("Failed to add subject.");
            }
        } catch (SQLException e) {
            System.out.println("Error in addSubject(): " + e.getMessage());
            e.printStackTrace();
        }
        displayMenu();
    }
    
    private void removeSubject() {
        System.out.print("Enter subject ID to remove: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();

        // Check if subject exists
        if (!doesSubjectExist(subjectId)) {
            System.out.println("Subject ID " + subjectId + " does not exist.");
            return;
        }

        // Ask admin if they want to delete related blocks and attendance
        System.out.print("Press [1] to delete all related data or [2] to delete only the subject: ");
        int choice = getValidatedChoice(1, 2);

        try {
            // If admin chooses to delete related blocks and attendance records
            if (choice == 1) {
                String removeBlocksSql = "DELETE FROM blocks WHERE subject_id = ?";
                try (PreparedStatement removeBlocksStmt = conn.prepareStatement(removeBlocksSql)) {
                    removeBlocksStmt.setString(1, subjectId);
                    int blocksAffected = removeBlocksStmt.executeUpdate();
                    System.out.println(blocksAffected + " blocks removed for subject ID: " + subjectId);
                }

                // Delete attendance records for blocks under this subject
                String removeAttendanceSql = "DELETE FROM attendance WHERE subject_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, subjectId);
                    int attendanceAffected = removeAttendanceStmt.executeUpdate();
                    System.out.println(attendanceAffected + " attendance records removed for subject ID: " + subjectId);
                }
            }
            else if (choice == 2) {
            	// Delete attendance records for blocks under this subject
                String removeAttendanceSql = "DELETE FROM attendance WHERE subject_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, subjectId);
                    int attendanceAffected = removeAttendanceStmt.executeUpdate();
                    System.out.println(attendanceAffected + " attendance records removed for subject ID: " + subjectId);
                }
            }

            // Now, remove the subject itself
            String removeSubjectSql = "DELETE FROM subjects WHERE subject_id = ?";
            try (PreparedStatement removeSubjectStmt = conn.prepareStatement(removeSubjectSql)) {
                removeSubjectStmt.setString(1, subjectId);
                int rowsAffected = removeSubjectStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Subject removed successfully.");
                } else {
                    System.out.println("Failed to remove subject.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in removeSubject(): " + e.getMessage());
            e.printStackTrace();
        }

        displayMenu();
    }


    public void addBlock() {
        System.out.print("Enter subject ID for the block: ");
        String subjectId = scanner.nextLine().toUpperCase().trim();
        System.out.print("Enter block name: ");
        String blockName = scanner.nextLine().toUpperCase();
        System.out.print("Enter start time (HH:mm): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (HH:mm): ");
        String endTime = scanner.nextLine();
        
        // Generate block_id as "subject_id_block_name"
        String blockId = subjectId + "_" + blockName;

        // SQL to insert block with formatted block_id
        String sql = "INSERT INTO blocks (block_id, subject_id, block_name, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, blockId);      // Set formatted block_id
            statement.setString(2, subjectId);    // Set subject_id
            statement.setString(3, blockName);    // Set block_name
            statement.setString(4, startTime);    // Set start_time
            statement.setString(5, endTime);      // Set end_time

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Block added successfully with ID: " + blockId);
            } else {
                System.out.println("Failed to add block.");
            }
        } catch (SQLException e) {
            System.out.println("Error in addBlock(): " + e.getMessage());
            e.printStackTrace();
        }
        displayMenu();
    }


    private void removeBlock() {
    	System.out.print("Enter block ID to remove: ");
    	String blockId = scanner.nextLine().toUpperCase().trim();
    	
    	 if (!doesBlockExist(blockId)) {
             System.out.println("Block ID " + blockId + " does not exist.");
             return;
         }
        
     // Ask admin if they want to delete related attendance
    	 System.out.println("[1] Attendance record");
    	 System.out.println("[2] Student record");
    	 System.out.println("[3] Both");
    	 System.out.println("What would you like to remove?");
    
         int choice = getValidatedChoice(1, 3); // Always 1 since it's the only choice
            
    	try {
                // Remove attendance records for the block first
    		if (choice == 1 || choice == 3) {
	    		String removeAttendanceSql = "DELETE FROM attendance WHERE block_id = ?";
	    		try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
	    			removeAttendanceStmt.setString(1, blockId);
	    			removeAttendanceStmt.executeUpdate();
                    System.out.println("Attendance records removed for block ID: " + blockId);
	    		}
    		}
    		else if (choice == 2 || choice == 3) {
    			String removeStudentsSql = "DELETE FROM students WHERE block_id = ?";
                try (PreparedStatement removeStudentsStmt = conn.prepareStatement(removeStudentsSql)) {
                    removeStudentsStmt.setString(1, blockId);
                    int studentRowsAffected = removeStudentsStmt.executeUpdate();
                    if (studentRowsAffected > 0) {
                        System.out.println(studentRowsAffected + " student(s) removed for block ID: " + blockId);
                    } else {
                        System.out.println("No students found under block ID: " + blockId);
                    }
                }
    		}

    		// Remove the block
            String removeBlockSql = "DELETE FROM blocks WHERE block_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(removeBlockSql)) {
    			statement.setString(1, blockId);
    			int rowsAffected = statement.executeUpdate();
    			if (rowsAffected > 0) {
    				System.out.println("Block removed successfully.");
    			} else {
    				System.out.println("Failed to remove block.");
    			}
    		}
    	} catch (SQLException e) {
    		System.out.println("Error in removeBlock(): " + e.getMessage());
    	}
    	displayMenu();
    }

    private void addStudent() {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine().toUpperCase().trim();
            
            System.out.print("Enter block ID to add student to: ");
        	String blockId = scanner.nextLine().toUpperCase().trim();
        	
        	if (doesStudentExist(studentId, blockId)) {
                System.out.println("Student with ID " + studentId + " already exists in block " + blockId);
                return;
            }
        	
            // If the student doesn't exist, proceed to add
            System.out.print("Enter student name: ");
            String studentName = scanner.nextLine().toUpperCase();

            
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO students (student_id, student_name, block_id) VALUES (?, ?, ?)")) {
                statement.setString(1, studentId);
                statement.setString(2, studentName);
                statement.setString(3, blockId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Student added successfully to block " + blockId);
                } else {
                    System.out.println("Failed to add student.");
                }
            } catch (SQLException e) {
                System.out.println("Error in addStudent(): " + e.getMessage());
            }
        	displayMenu();
        }

    private void removeStudent() {
    	System.out.print("Enter student ID to remove: ");
    	String studentId = scanner.nextLine().toUpperCase().trim(); 
    	
    	System.out.print("Enter Block ID: ");
    	String blockId = scanner.nextLine().toUpperCase().trim();
    	
    	// Check if the student exists in the specified block
        if (!doesStudentExist(studentId, blockId)) {
            System.out.println("Student with ID " + studentId + " does not exist in block " + blockId);
            return;
        }
        
    	System.out.print("[1] Delete from Attendance records");
    	System.out.print("[2] Delete from Studentlist");
    	System.out.print("[3] Delete from both");
    	int choice = scanner.nextInt();
    	 scanner.nextLine();
               
    	try {
    		
    		if (choice == 1) {
    			 // Prompt for Session ID before removing from attendance records
                System.out.print("Enter Session ID for the attendance record: ");
                String sessionId = scanner.nextLine().trim();
	    		// Remove student from any attendance records
                String removeAttendanceSql = "DELETE FROM attendance WHERE student_id = ? AND session_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, studentId);
                    removeAttendanceStmt.setString(2, sessionId);
                    int rowsAffected = removeAttendanceStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Student attendance record removed successfully.");
                    } else {
                        System.out.println("No attendance record found for this session.");
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
                        System.out.println("Student removed from student list successfully.");
                    } else {
                        System.out.println("Failed to remove student from student list.");
                    }
                }
            }
    		else if (choice == 3) {
    			// First, remove from attendance records (with session ID prompt)
                System.out.print("Enter Session ID for the attendance record: ");
                String sessionId = scanner.nextLine().trim();

                String removeAttendanceSql = "DELETE FROM attendance WHERE student_id = ? AND session_id = ?";
                try (PreparedStatement removeAttendanceStmt = conn.prepareStatement(removeAttendanceSql)) {
                    removeAttendanceStmt.setString(1, studentId);
                    removeAttendanceStmt.setString(2, sessionId);
                    removeAttendanceStmt.executeUpdate();
                    System.out.println("Student attendance record removed successfully.");
                }

                // Then, remove from student list
                String removeStudentSql = "DELETE FROM students WHERE student_id = ? AND block_id = ?";
                try (PreparedStatement removeStudentStmt = conn.prepareStatement(removeStudentSql)) {
                    removeStudentStmt.setString(1, studentId);
                    removeStudentStmt.setString(2, blockId);
                    removeStudentStmt.executeUpdate();
                    System.out.println("Student removed from student list successfully.");
                }
            } 
    		else {
                System.out.println("Please choose a valid option (1, 2, or 3).");
            }
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in removeStudent(): " + e.getMessage());
    	}
    	displayMenu();
    }
}
    
 
