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
        return String.format("\t[Block Name]\t%-10s\t\t[Block ID]\t%-10s\t\t[Subject ID]\t%-10s\t\t[Start Time]\t%-10s\t\t[End Time]\t%s",
                name, id, getSubjectId(), startTime, endTime);
    }
}
