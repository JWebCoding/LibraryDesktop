import Models.ConnectionCommands;
import Models.SQLCommands;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/mainStage.fxml"));
        primaryStage.setTitle("Library TEST");
        primaryStage.setScene(new Scene(root, 1050, 680));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
