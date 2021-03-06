package data;

import java.util.ArrayList;
import java.util.List;

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
        if (shift < 0 || shift > 15) throw new IllegalArgumentException("Shift must be in range from 0 to 15: " + shift);
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

    public static List<ByteAddress> getTable(long from, long to) {
        List<ByteAddress> list = new ArrayList<>();
        for (long i = from; i < to; i++) {
            list.add(new ByteAddress(i));
        }
        return list;
    }

    public ByteAddress plus(int number) {
        if (number < 0) throw new IllegalArgumentException("number should be below 0: " + number);
        return new ByteAddress(pointer + ((shift + number) / 16), (shift + number) % 16);
    }
    public ByteAddress minus(int number) {
        if (number < 0) throw new IllegalArgumentException("number should be below 0: " + number);
        if (number <= shift)
            return new ByteAddress(pointer, shift - number);
        else {
            int val = (int)pointer * 16 + shift - number;
            return new ByteAddress(val / 16, val % 16);
        }
    }


    public long toLong() {
        return this.pointer * 16 + shift;
    }
    public int toInt() {
        return (int) this.toLong();
    }
}
