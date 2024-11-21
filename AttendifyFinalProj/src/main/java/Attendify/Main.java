package Attendify;

public class Main {
    public static void main(String[] args){
        banner();
        AttendanceSystem system = new AttendanceSystem();
        system.mainMenu();
    }


    public static void banner() {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        System.out.println("\n" + "=".repeat(150));
        System.out.println("\t\t\t\t\t\t\t\t\t\tA T T E N D I F Y : Smart Attendance Tracking System");
        System.out.println("=".repeat(150));
        System.out.println(RED + "\n\t\t\t   / &&7&&&&&       / &&        / &&       W E L C O M E   T O            / && | &&&     / &&&&&" + RESET);
        System.out.println(RED + "\t\t\t  | &&&  | &&&     | &&&       | &&&                                     | &&&          | &&&  &&" + RESET);
        System.out.println(RED + "\t\t\t  | &&& _| &&& | &&&&&&&&&& | &&&&&&&&&&  / &&&&&&   | &&& / &&&&   / &&&&&&&& | &&&    | &&&     |  &&   /&&" + RESET);
        System.out.println(RED + "\t\t\t  | &&&&&&&&&&     | &&&       | &&&     | &&& | &&& | &&& & | &&& | &&&   &&& | &&& | &&&&&&&&&  | &&&  | &&&" + RESET);
        System.out.println(RED + "\t\t\t  | &&&  | &&&     | &&&       | &&&     | &&&&&&&&& | &&&&  | &&& | &&&   &&& | &&& | &&&&&&&&&  | &&&  | &&&" + RESET);
        System.out.println(RED + "\t\t\t  | &&&  | &&&     | &&&       | &&&     | &&&       | &&&   | &&& | &&&   &&& | &&&    | &&&     | &&& _| &&&" + RESET);
        System.out.println(RED + "\t\t\t  | &&&  | &&&     | &&&       | &&&      \\ &&&&&&&  | &&&   | &&&  \\ &&&&&&&  | &&&    | &&&      \\ &&&&&&&&&" + RESET);
        System.out.println(RED + "\t\t\t                                                                                                         | &&&" + RESET);
        System.out.println(RED + "\t\t\t                                  [NAME: EDRICKA MAE H. PAULOS]                                   | &&&  | &&&" + RESET);
        System.out.println(RED + "\t\t\t                                       [GITHUB: EdrickaMae]                                        \\ &&&&&&&&" + RESET);
        System.out.println("\n" + "=".repeat(150));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\tOBJECT-ORIENTED PROGRAMMING [FINAL PROJECT]");
        System.out.println("=".repeat(150) + "\n");

    }
}
