package lk.ijse.dep8;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/CustomerManagementForm.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Customer Management System : Home");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
