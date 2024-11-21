package Attendify.models;

public class Subject extends BaseClass {

    public Subject(String subjectName, String subjectId) {
        super(subjectId, subjectName);
    }

    @Override
    public String toString() {
        return String.format("\t\t\tSubject ID   [%s]\tSubject Name   [%s]", id, name);
    }
}
