package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class LoginController {
    MainStageController mainStageController=MainStageController.getInstance();
    @FXML Button buttonLogin;

    public void attemptLogin() throws IOException {
        mainStageController.openScreenAdd();
    }

}
