package data;

public class ByteAddress {
    //pointer is a number of "row"
    //pointer = 1 == "10" in HEX
    private long pointer;
    //Shift may be only in range from 0 to 15
    private int shift;

    public ByteAddress(long pointer) {
        this.pointer = pointer;
        this.shift = 0;
    }
    public ByteAddress(long pointer, int shift) {
        if (shift < 0 || shift > 15) throw new IllegalArgumentException("Shift must be in range from 0 to 15");
        this.pointer = pointer;
        this.shift = shift;
    }
    public ByteAddress(String addr) {
        String hex = (addr.startsWith("0x")) ? addr.substring(2) : addr;
        if (hex.matches("[0-9a-fA-F]")) throw new IllegalArgumentException("Forbidden chars in string");
        //Take prefix, except last symbol
        this.pointer = Long.parseLong(hex.substring(0, hex.length() - 1), 16);
        //Last symbol if shift
        this.shift = Integer.parseInt(hex.substring(hex.length() - 1), 16);
    }

    @Override
    public String toString() {
        return String.format("%010X", pointer) + String.format("%01X", shift);
    }
    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) return false;
        ByteAddress address = (ByteAddress) other;
        return (pointer == address.pointer && shift == address.shift);
    }
    public long toLong() {
        return this.pointer * 16 + shift;
    }
    public int toInt() {
        return (int) this.toLong();
    }
}
