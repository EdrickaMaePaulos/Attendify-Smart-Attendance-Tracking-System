package attendanceSystem;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
    	banner();
        AttendanceSystem system = new AttendanceSystem();
        system.mainMenu();
    }
    
    
    public static void banner() {
   	 final String YELLOW = "\u001B[33m";
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        final String BLACK = "\u001B[30m";
        System.out.println("\n\t\t\t" + "=".repeat(70));
		System.out.println("\t\t\t\tA T T E N D I F Y : Smart Attendance Tracking System");
       System.out.println("\t\t\t" + "=".repeat(70));
        System.out.println(RED + "\n    / &&&&&&&&      / &&&	       "  + YELLOW + "W E L C O M E  T O :" + RED + "               / &&&  | &&&&     / &&&&&&" + RESET);
        System.out.println(RED + "\t  | &&&&&&&&&&    | &&&&					         | &&&&            | &&&& &&&&" + RESET);
        System.out.println(RED + "\t  | &&&  | &&& | &&&&&&&&&&  / &&&&&&&   /&&&/ &&&&   /&&&/ &&&&   / &&&&&&&&&  / &&&&    | &&&   &&&  / &&&    &&" + RESET);
        System.out.println(RED + "\t  | &&&&&&&&&& | &&&&&&&&&& | &&&__ &&& | &&&& | &&& | &&&& | &&& | &&&& | &&& | &&&&& | &&&&&&&&&    | &&&&    &&&" + RESET);
        System.out.println(RED + "\t  | &&&&&&&&&&    | &&&&    | &&&&&&&&  | &&&  | &&& | &&&  | &&& | &&&  | &&& | &&&&& |  &&&&&&&     | &&&&    &&&" + RESET);
        System.out.println(RED + "\t  | &&&  | &&&    | &&&&    | &&&___/ & | &&&  | &&& | &&&  | &&& | &&&&__ &&& | &&&&&    | &&&       | &&&&    &&&" + RESET);
        System.out.println(RED + "\t  | &&&  | &&&    | &&&&    \\  &&&&&&&  | &&&  | &&& | &&&  | &&&  \\  &&&&&&&& | &&&&&    | &&&       |  &&&&&&&&&&" + RESET);
        System.out.println(RED + "	                                                                                                       \\______  &&&" + RESET);
        System.out.println(BLACK + "			                [Created by: Edricka Mae H. Paulos]" + RED + "\t         	                         / &&&   &&&" + RESET);
        System.out.println(BLACK + "			                     [Github: EdrickaMaePaulos]	    " + RED + "                                    | &&&&__ &&&" + RESET);
        System.out.println(RED + "\t                                                                                                        \\ &&&&&&&&/\n" + RESET);

        }
}
