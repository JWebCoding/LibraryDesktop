package com.library.controllers;

import com.library.models.ConnectionCommands;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    MainStageController mainStageController=MainStageController.getInstance();
    ConnectionCommands commands=new ConnectionCommands();

    @FXML Button buttonLogin;
    @FXML TextField textFieldUsername;
    @FXML TextField textFieldPassword;

    
    public void initialize() {
    	
    }

    public void attemptLogin() throws IOException {
    	commands.getConnectionSettings();
    	commands.testServerConnection();
        mainStageController.openScreenAdd();
        mainStageController.showLoginButton();
    }

}
