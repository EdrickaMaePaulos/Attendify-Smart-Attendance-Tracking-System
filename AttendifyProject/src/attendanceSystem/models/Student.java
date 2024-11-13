package attendanceSystem.models;

public class Student {
    private String studentID;
    private String name;
    private String subject;
    private String block;

    // Constructor
    public Student(String studentID, String name, String subject, String block) {
        this.studentID = studentID;
        this.name = name;
        this.subject = subject;
        this.block = block;
    }
    // Getters and Setters
    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }
}
