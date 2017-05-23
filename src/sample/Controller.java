package sample;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import data.FileModel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class Controller {
    private FileModel fileModel;
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

    public void linkToModel(FileModel model) {
        this.fileModel = model;
        binaryAddressTable.setText(String.join("\r\n", fileModel.getStringAddressTable()));
        hexDump.setText(String.join("\r\n", fileModel.getSplittedHexData()));
        dataString.setText(String.join("\r\n", fileModel.getSplittedStringData()));
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
