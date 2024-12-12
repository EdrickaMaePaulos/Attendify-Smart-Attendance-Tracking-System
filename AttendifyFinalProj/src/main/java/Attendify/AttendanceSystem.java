package Attendify;

import db.DatabaseConnection;

import java.sql.Connection;
import java.util.Scanner;

public class AttendanceSystem {
    AdminMenu adminMenu;
    private final Connection conn;
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";
    boolean exit = false;


    public AttendanceSystem() {
        conn = DatabaseConnection.connect();
        this.adminMenu = new AdminMenu(conn, this);
        boolean b = conn != null && DatabaseConnection.verifyConnection(conn);
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);


        while (!exit) {
            System.out.println("\n\t\t\t" + "=".repeat(70));
            System.out.println("\n\t\t\t\t\t\t[1] Student Menu");
            System.out.println("\t\t\t\t\t\t[2] Admin Menu");
            System.out.println("\t\t\t\t\t\t[3] Exit\n");

            int choice = adminMenu.getValidatedChoice(3);

            try {
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
                            adminMenu = new AdminMenu(conn, this);
                            adminMenu.displayMenu();
                        } else {
                            System.out.println(ITALIC + "\t\t\t\t\t\tInvalid admin credentials." + RESET);
                        }
                        break;
                    case 3:
                        closeProgram();
                        break;
                    default:
                        System.out.println(ITALIC + "\n\t\t\t\t\t\t\tInvalid choice." + RESET);
                        break;
                }
            } catch (NullPointerException e) {
                System.err.println("NullPointerException: " + e.getMessage());
            } finally {
                scanner.close();
            }
        }
    }

    public void closeProgram(){
        exit = true;
        close();
        System.out.println(ITALIC + "\t\t\t\t\t\t\tExiting the system. Thank you!" + RESET);
    }
    public void close() {
        DatabaseConnection.closeConnection(conn);
    }

    private boolean adminLogin(String username, String password) {
        return username.equals("admin") && password.equals("password123");
    }
}

