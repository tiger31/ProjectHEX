package data;

public class HistoryRecord {
    private ByteAddress begin;
    private ByteAddress end;
    private byte[] oldData;
    private byte[] newData;
    private Edit operation;

    public HistoryRecord(ByteAddress begin, ByteAddress end, byte[] oldData, byte[] newData, Edit operation) {
        this.begin = begin;
        this.end = end;
        this.oldData = oldData;
        this.newData = newData;
        this.operation = operation;
    }

    public ByteAddress getBegin() {
        return begin;
    }

    public ByteAddress getEnd() {
        return end;
    }

    public byte[] getOldData() {
        return oldData;
    }

    public byte[] getNewData() {
        return newData;
    }

    public Edit getOperation() {
        return operation;
    }
}
