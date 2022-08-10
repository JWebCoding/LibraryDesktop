import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainStage.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Library Beta");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1075, 855));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
