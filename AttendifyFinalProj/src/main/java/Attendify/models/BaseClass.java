package Attendify.models;

public class BaseClass {

    protected String id;
    protected String name;

    public BaseClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return String.format("[ID]\t%-10s\t\t[Name]\t%-30s", id, name);
    }
}

