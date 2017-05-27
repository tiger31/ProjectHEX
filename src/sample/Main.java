package sample;

import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Date;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        BorderPane root = loader.load();
        Controller controller = loader.getController();
        DataModel d = new DataModel();
        d.open("ProjectHEX.iml");
        controller.linkToModel(d);
        primaryStage.setTitle("ProjectHEX");
        primaryStage.setScene(new Scene(root));
        System.out.println("Start showing" + new Date());
        primaryStage.show();
        System.out.println("Showed" + new Date());
        controller.bindScrollBars();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
