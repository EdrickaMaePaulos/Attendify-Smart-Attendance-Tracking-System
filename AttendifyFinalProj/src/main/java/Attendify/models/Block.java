package Attendify.models;

public class Block extends BaseClass {
    private final Subject subject;
    private final String startTime;
    private final String endTime;

    public Block(String blockId, String blockName, Subject subject, String startTime, String endTime) {
        super(blockId, blockName);
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getSubjectId() {
        return subject != null ?
                subject.getId() : null;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("\tBlock Name   [%s]\tBlock ID   [%s]\tSubject ID   [%s]\tStart Time   [%s]\tEnd Time   [%s]",
                name, id, getSubjectId(), startTime, endTime);
    }
}
