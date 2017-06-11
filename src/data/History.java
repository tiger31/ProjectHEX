package data;

import java.util.LinkedList;
import java.util.List;

public class History {
    private int pointer = -1;
    private List<HistoryRecord> history;

    public History() {
        history = new LinkedList<>();
    }

    public HistoryRecord previous() throws IllegalStateException {
        if (pointer == -1) throw new IllegalStateException("Already beginning of the history");
        return history.get(pointer--);
    }

    public HistoryRecord next() throws IllegalStateException {
        if (pointer >= history.size() - 1) throw new IllegalStateException("Already end of the history");
        return history.get(++pointer);
    }

    public void add(HistoryRecord record) {
        if (pointer == -1)
            history.clear();
        else
            history = history.subList(0, pointer + 1);
        history.add(record);
        if (history.size() == 65)
            history.remove(0);
        pointer = history.size() - 1;
    }

    public void clear() {
        pointer = -1;
        history.clear();
    }


}
