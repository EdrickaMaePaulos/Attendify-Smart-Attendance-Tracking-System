package Attendify.models;

public class Student extends BaseClass {
    public static final String STATUS_PRESENT = "P";
    public static final String STATUS_LATE = "L";
    public static final String STATUS_ABSENT = "A";

    private final Block block;

    public Student(String studentId, String studentName, Block block) {
        super(studentId, studentName);
        this.block = block;
    }

    public String getBlockId() {
        return block != null ? block.getId() : null;
    }

    @Override
    public String toString() {
        return String.format("\t\t\t%s\tBlock ID   [%s]",
                super.toString(), getBlockId());
    }
}
