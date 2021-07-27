import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/mainStage.fxml"));
        primaryStage.setTitle("Library Beta");
        primaryStage.setScene(new Scene(root, 1050, 759));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
