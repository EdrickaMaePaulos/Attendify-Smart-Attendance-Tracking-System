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

    @Override
    public String toString() {
        return String.format("ID   [%s]\tName   [%s]", id, name);
    }
}

