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
    private ByteAddress last;
    private double delta;

    public ScrollData(int length, ByteAddress last) {
        this.length = length;
        this.last = last;
        this.begin = 0;
        this.end = (length > 32) ? 32 : length;
        this.current = 0;
        //Amount of changes causes rewriting
        this.scrollValue = ((length - 16) < 0) ? 0 : length - 16;
        delta = 0;

    }

    public void collect(double change) {
        delta += change;
        recount((int)Math.floor(delta));
    }


    private void recount(int current) {
        if (current == this.current) return;
        if (current > length || current < 0)
            throw new IllegalStateException("Current string can be more then it's amount or below 0: " + current);
        int change = current - this.current;
        if (scrollValue > 16) {
            if (change > 0) {
                if ((current - begin) > 8) {
                    end = ((end + change) > length) ? length : end + change;
                    begin = end - 32;
                }
            } else {
                if ((end - current) > 8) {
                    begin = ((begin + change) < 0) ? 0 : begin + change;
                    end = begin + 32;
                }
            }
        }
        this.current = current;
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

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) return false;
        ScrollData scroll = (ScrollData) other;
        return (last.equals(scroll.last) && begin == scroll.begin && end == scroll.end && current == scroll.current
            && length == scroll.length && scrollValue == scroll.scrollValue);
    }
}