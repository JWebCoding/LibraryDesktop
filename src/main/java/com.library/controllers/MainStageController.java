package com.library.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainStageController {
    static MainStageController instance=null;

    @FXML Button buttonAddBooks;
    @FXML Button buttonSearchEdit;
    @FXML Button buttonLogout;
    @FXML Button buttonClose;
    @FXML Pane panePrimary;
    @FXML Label labelTitle;

    FXMLLoader loader=new FXMLLoader();

    public void initialize() throws IOException {
        instance=this;
        openScreenLogin();
    }

    public static MainStageController getInstance(){
        return instance;
    }

    public void openScreenAdd() throws IOException {
        buttonAddBooks.setVisible(true);
        buttonSearchEdit.setVisible(true);
        buttonLogout.setVisible(true);
        labelTitle.setVisible(true);

        buttonAddBooks.setDisable(true);
        buttonSearchEdit.setDisable(false);

        Pane paneAdd=FXMLLoader.load(getClass().getResource("/fxml/ManualAddBookPane.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneAdd);
    }

    public void openScreenSearch() throws IOException {
        buttonAddBooks.setDisable(false);
        buttonSearchEdit.setDisable(true);
//        loader.setLocation(getClass().getResource("/fxml/SearchPane.fxml"));
//        Pane paneSearch=loader.load();

        Pane paneSearch=FXMLLoader.load(getClass().getResource("/fxml/SearchPane.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneSearch);
    }

    public void openScreenLogin() throws IOException {
        buttonAddBooks.setVisible(false);
        buttonSearchEdit.setVisible(false);
        buttonLogout.setVisible(false);
        labelTitle.setVisible(false);
//        loader.setLocation(getClass().getResource("/fxml/LoginPane.fxml"));
//        Pane paneLogin=
        Pane paneLogin=FXMLLoader.load(getClass().getResource("/fxml/LoginPane.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneLogin);
    }
    
    public void showLoginButton() {
    	buttonClose.setVisible(false);
    	buttonClose.setDisable(true);
    	buttonLogout.setVisible(true);
    	buttonLogout.setDisable(false);
    }
    
    
    public void closeProgram() {
    	Platform.exit();
    }
    
}
