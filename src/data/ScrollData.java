package data;

/*
So, the idea is to show 16 lines of file and keep 32 drawn, it should help not to lose to much RAM and FPS
That's why we calc the current top line, and keep 8 under and 24 lines below
Scrolling is still should be smooth
Methods of dynamic data loading is still in progress
 */
public class ScrollData {
    private int begin;
    private int end;
    private int current;
    private int length;
    private int scrollValue;
    private int lines;
    private ByteAddress last;
    private double delta;

    public ScrollData(int length, ByteAddress last, int lines) {
        this.length = length;
        this.last = last;
        this.lines = lines;
        this.begin = 0;
        this.end = (length > lines) ? lines : length;
        this.current = 0;
        //Amount of changes causes rewriting
        this.scrollValue = ((length - lines) < 0) ? 0 : length - lines;
        delta = 0;


    }

    public void onChange(int length, ByteAddress last) {
        this.length = length;
        this.last = last;
        this.end = (end > length) ? length : end;
        recount(0, true);
    }

    public void collect(double change) {
        delta += change;
        recount((int)Math.floor(delta), false);
    }

    public void setLinesAmount(int lines) {
        this.lines = lines;
        this.scrollValue = ((length - lines) < 0) ? 0 : length - lines;
        recount(current, true);
    }

    private void recount(int current, boolean force) {
        if (current == this.current && !force) return;
        if (current > length || current < 0) return;
        int change = current - this.current;
        if (scrollValue >= 0) {
            if (change > 0) {
                end = ((end + change) >= length) ? length : end + change;
                begin = end - lines;
            } else {
                if (end == length)
                    begin = ((end - lines + change) <= 0) ? 0 : end - lines + change;
                else
                    begin = ((begin + change) <= 0) ? 0 : begin + change;
                end = ((begin + lines) > length) ? length : begin + lines;
            }
        }
        this.current = begin;
    }

    public ByteAddress getLast() {
        return last;
    }
    public int getBegin() {
        return begin;
    }
    public int getCurrent() {
        return current;
    }
    public int getEnd() {
        return end;
    }
    public int getLength() {
        return length;
    }
    public int getValue() {
        return scrollValue;
    }
    public int getLines() {
        return lines;
    }
    public double getVisibleAmount() {
        return (double) scrollValue * ((double) lines / length);
    }

    public ByteAddress getBeginAddr() {
        return new ByteAddress(begin);
    }
    public ByteAddress getEndAddr() {
        return (end == length) ? last : new ByteAddress(end);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) return false;
        ScrollData scroll = (ScrollData) other;
        return (last.equals(scroll.last) && begin == scroll.begin && end == scroll.end && current == scroll.current
            && length == scroll.length && scrollValue == scroll.scrollValue);
    }
}