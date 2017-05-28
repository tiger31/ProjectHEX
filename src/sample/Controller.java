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
        updateForms();


    }
    public void bindScrollBars() {
        ScrollPane first = (ScrollPane) binaryAddressTable.getChildrenUnmodifiable().get(0);
        ScrollPane second = (ScrollPane) hexDump.getChildrenUnmodifiable().get(0);
        ScrollPane third = (ScrollPane) dataString.getChildrenUnmodifiable().get(0);
        first.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        second.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        third.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMin(0);
        scroll.setMax(scrollData.getValue());

        scroll.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number> ov, Number old_val, Number new_val) {
                int min = scrollData.getBegin();
                int max = scrollData.getEnd();
                int current = scrollData.getCurrent();
                scrollData.collect((new_val.doubleValue() - old_val.doubleValue()));
                if (current != scrollData.getCurrent()) {
                    if (min != scrollData.getBegin() || max != scrollData.getEnd()) {
                        updateForms();
                    }
                }
            }
        });
    }
    private void updateForms() {
        binaryAddressTable.setText(String.join("\r\n",
                dataModel.getStringAddressTable(scrollData.getBegin(), scrollData.getEnd())));
        hexDump.setText(String.join("\r\n",
                dataModel.getHEXValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr())));
        dataString.setText(dataModel.getStringValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr()));
    }
}
