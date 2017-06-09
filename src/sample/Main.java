package sample;

import data.ByteAddress;
import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class Main extends Application {
    Stage primary;
    DataModel data;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        BorderPane root = loader.load();
        Controller controller = loader.getController();
        DataModel data = new DataModel();
        primary = primaryStage;
        this.data = data;
        primaryStage.setTitle("ProjectHEX");
        primaryStage.setScene(new Scene(root));
        controller.linkToModel(this, data);
        primaryStage.show();
    }

    public boolean editData(String hex, String string, ByteAddress begin, ByteAddress end, Edit type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editDialog.fxml"));
            BorderPane page = loader.load();
            EditDialog controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Edit...");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(primary);
            Scene scene = new Scene(page);
            controller.linkToMain(dialog, data, begin, end, type);
            controller.showData(hex, string);
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
