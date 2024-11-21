package Attendify;

import Attendify.models.Block;
import Attendify.models.Subject;

import java.sql.*;
import java.util.Scanner;

class BlockManager implements Displayables {
    private final Connection conn;
    private final Scanner scanner;
    final String ITALIC = "\033[3m";
    final String RESET = "\u001B[0m";

    public BlockManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    @Override
    public void displayOptions() {
        System.out.println("\n\t\t\t\t\t\tChoose Display Option:");
        System.out.println("\n\t\t\t\t\t\t[1] List all blocks");
        System.out.println("\t\t\t\t\t\t[2] Display block by ID");
        System.out.println("\t\t\t\t\t\t[3] Return");
    }

    @Override
    public void listAll() {
        String query = "SELECT * FROM blocks";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n\t\t\t\t\t\t\tAll Blocks:");
            while (rs.next()) {
                String blockId = rs.getString("block_id");
                String blockName = rs.getString("block_name");
                String subjectId = rs.getString("subject_id");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                Block block = new Block(blockId, blockName, new Subject("", subjectId), startTime, endTime);
                System.out.println(block.toString());
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in listAllBlocks(): " + e.getMessage());
        }    }

    @Override
    public void displayById() {
        System.out.print("\n\t\t\t\t\tEnter Block ID: ");
        String blockId = scanner.nextLine().toUpperCase().trim();
        System.out.println("\n\t\t\t\t\t\t\tBlock:\n");

        String query = "SELECT * FROM blocks WHERE block_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, blockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String blockName = rs.getString("block_name");
                    String subjectId = rs.getString("subject_id");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");
                    Block block = new Block(blockId, blockName, new Subject("", subjectId), startTime, endTime);
                    System.out.println(block.toString());
                } else {
                    System.out.println(ITALIC + "\n\t\t\t\t\tNo block found with ID: " + blockId + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println("\t\t\tError in displayBlockById(): " + e.getMessage());
        }    }
}
