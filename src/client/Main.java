package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 460, 560);
        stage.setTitle("Joe Market");

        stage.getIcons().add(new javafx.scene.image.Image(
                getClass().getResourceAsStream("/images/icon.png")));

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
