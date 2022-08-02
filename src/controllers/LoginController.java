package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

import models.ConnectionCommands;

public class LoginController {
    MainStageController mainStageController=MainStageController.getInstance();
    ConnectionCommands commands=new ConnectionCommands();
    @FXML Button buttonLogin;
    
    public void initialzixe() {
    	
    }

    public void attemptLogin() throws IOException {
    	commands.getConnectionSettings();
    	commands.testServerConnection();
        mainStageController.openScreenAdd();
        mainStageController.showLoginButton();
    }

}
