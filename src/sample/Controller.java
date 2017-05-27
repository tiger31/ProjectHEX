package sample;

import data.ByteAddress;
import data.DataModel;
import data.ScrollData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class Controller {
    private DataModel dataModel;
    private ScrollData scrollData;
    @FXML
    private TextArea binaryAddressTable;
    @FXML
    private TextArea hexDump;
    @FXML
    private TextArea dataString;
    @FXML
    private ScrollBar scroll;


    public Controller() {

    }

    @FXML
    private void initialize() {

    }

    public void linkToModel(DataModel dataModel) {
        this.dataModel = dataModel;
        int length = (int)dataModel.getFileHexLength();
        ByteAddress last = dataModel.getLastAddress();
        scrollData = new ScrollData(length, last);
        scroll.setVisibleAmount(16.0 / length);
        binaryAddressTable.setText(String.join("\r\n", dataModel.getStringAddressTable(0, length)));
        hexDump.setText(String.join("\r\n",dataModel.getHEXValueGrouped(new ByteAddress(0), last)));
        dataString.setText(dataModel.getStringValueGrouped(new ByteAddress(0), last));


    }
    public void bindScrollBars() {
        ScrollPane first = (ScrollPane) binaryAddressTable.getChildrenUnmodifiable().get(0);
        ScrollPane second = (ScrollPane) hexDump.getChildrenUnmodifiable().get(0);
        ScrollPane third = (ScrollPane) dataString.getChildrenUnmodifiable().get(0);
        scroll.setMin(first.getVmin());
        scroll.setMax(first.getVmax());
        scroll.valueProperty().bindBidirectional(first.vvalueProperty());
        scroll.valueProperty().bindBidirectional(second.vvalueProperty());
        scroll.valueProperty().bindBidirectional(third.vvalueProperty());
        scroll.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number> ov, Number old_val, Number new_val) {
                int current = scrollData.getCurrent();
                scrollData.collect((new_val.doubleValue() - old_val.doubleValue()) * scrollData.getValue());
                if (current != scrollData.getCurrent()) {
                    printScroller();
                }
            }
        });
    }
    //DEBUG
    private void printScroller() {
        System.out.print("ScrollValue = " + scrollData.getValue());
        System.out.print(" Length = " + scrollData.getLength());
        System.out.print(" Begin = " + scrollData.getBegin());
        System.out.print(" End = " + scrollData.getEnd());
        System.out.print(" Current = " + scrollData.getCurrent());
        System.out.println(" Last = " + scrollData.getLast().toString() + "/" + scrollData.getLast().toInt());
    }
}
