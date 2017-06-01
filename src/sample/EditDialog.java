package sample;

import data.DataModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    public EditDialog() {}

    public void linkToMain(Stage stage, DataModel model) {
        this.stage = stage;
        this.model = model;
    }
    public void showData(String hex, String string) {
        this.hex.setText(hex.replaceAll("\n", " "));
        this.string.setText(string.replaceAll("\n", ""));
    }

    @FXML
    private void initialize() {
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Pressed");
            }
        });
    }
}
