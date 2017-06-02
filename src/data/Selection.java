package data;

import javafx.scene.control.IndexRange;

public class Selection {
    private int strBegin;
    private int strEnd;
    private int indexBegin;
    private int indexEnd;

    public Selection(IndexRange range, boolean isFromHex) {
        if (isFromHex) {
            this.strBegin = range.getStart() / 48;
            this.strEnd = range.getEnd() / 48;
            this.indexBegin = (1 + range.getStart() - 48 * strBegin) / 3;
            this.indexEnd = (2 + range.getEnd() - 48 * strEnd) / 3;
        } else {
            this.strBegin = range.getStart() / 17;
            this.strEnd = range.getEnd() / 17;
            this.indexBegin = (range.getStart() - 17 * strBegin);
            this.indexEnd = (range.getEnd() - 17 * strEnd);
        }
    }

    public IndexRange getRangeForString() {
        return new IndexRange((strBegin * 17 + indexBegin), (strEnd * 17 + indexEnd));
    }
    public IndexRange getRangeForHex() {
        int min = (strBegin * 48 + indexBegin * 3);
        int max = (strEnd * 48 + indexEnd * 3 - 1);
        int actualMin = (min > max) ? max : min;
        int actualMax = (min > max) ? min : max;
        return new IndexRange(actualMin, actualMax);
    }

    public int getIndexBegin() {
        return indexBegin;
    }

    public int getStrBegin() {
        return strBegin;
    }

    public int getIndexEnd() {
        return indexEnd;
    }

    public int getStrEnd() {
        return strEnd;
    }
}
