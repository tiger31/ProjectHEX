package sample;

import data.ByteAddress;
import data.DataModel;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class Controller {
    private DataModel dataModel;
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
        int length = (int)dataModel.getFileHexLength() - 1;
        ByteAddress last = new ByteAddress(length, (int)(dataModel.getFileLenght() - length * 16));

        binaryAddressTable.setText(String.join("\r\n", dataModel.getStringAddressTable(0, length + 1)));
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
    }
}
