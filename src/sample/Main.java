package sample;

import data.ByteAddress;
import data.FileModel;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        BorderPane root = loader.load();
        Controller controller = loader.getController();
        FileModel model = new FileModel();
        model.open("part3.rar");
        controller.linkToModel(model);
        primaryStage.setTitle("ProjectHEX");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        controller.bindScrollBars();
        primaryStage.setResizable(false);
        System.out.print(String.join("\r\n", model.getSplittedStringData()));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
