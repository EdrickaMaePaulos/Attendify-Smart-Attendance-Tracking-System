package Attendify.models;

public class Subject extends BaseClass {

    public Subject(String subjectName, String subjectId) {
        super(subjectId, subjectName);
    }

    @Override
    public String toString() {
        return String.format("\t\t\t[Subject ID]\t%10s\t\t[Subject Name]\t%s", id, name);
    }
}
