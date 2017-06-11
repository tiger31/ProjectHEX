package sample;

import data.ByteAddress;
import data.DataModel;
import data.Edit;
import data.Hex;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EditDialog {
    @FXML
    private Button save;
    @FXML
    private Button cancel;
    @FXML
    private TextArea string;
    @FXML
    private TextArea hex;

    private Stage stage;
    private DataModel model;
    private ByteAddress begin;
    private ByteAddress end;
    private Edit type;

    public EditDialog() {}

    public void linkToMain(Stage stage, DataModel model, ByteAddress begin, ByteAddress end, Edit type) {
        this.stage = stage;
        this.model = model;
        this.begin = begin;
        this.end = end;
        this.type = type;
    }
    public void showData(String hex, String string) {
        this.hex.setText(hex.replaceAll("\n", " "));
        this.string.setText(string.replaceAll("\n", ""));
    }

    @FXML
    private void initialize() {
        cancel.setOnAction((ActionEvent event) -> {
            stage.close();
        });
        save.setOnAction((ActionEvent event) -> {
            if (type == Edit.CHANGE)
                model.changeData(begin, end, Hex.hexStringToByteArray(Hex.normalizeHexString(hex.getText())));
            else
                model.insertData(begin, Hex.hexStringToByteArray(Hex.normalizeHexString(hex.getText())));
            stage.close();
        });
        hex.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.matches("[0-9a-fA-F ]*")) {
                hex.setText(newValue);
                string.setText(Hex.hexStringToString(newValue));
            } else {
                hex.setText(oldValue);
            }
        });
    }
}
