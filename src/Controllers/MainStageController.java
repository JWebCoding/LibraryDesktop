package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainStageController {
    static MainStageController instance=null;

    @FXML Button buttonAddBooks;
    @FXML Button buttonLibraryStats;
    @FXML Button buttonSearchEdit;
    @FXML Button buttonLogout;
    @FXML Pane panePrimary;
    @FXML Label labelTitle;

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
        buttonLibraryStats.setVisible(true);
        buttonLogout.setVisible(true);
        labelTitle.setVisible(true);

        buttonAddBooks.setDisable(true);
        buttonSearchEdit.setDisable(false);
        buttonLibraryStats.setDisable(false);

        Pane paneAdd=FXMLLoader.load(getClass().getResource("/FXML/add.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneAdd);
    }

    public void openScreenSearch() throws IOException {
        buttonAddBooks.setDisable(false);
        buttonSearchEdit.setDisable(true);
        buttonLibraryStats.setDisable(false);
        Pane paneSearch=FXMLLoader.load(getClass().getResource("/FXML/search.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneSearch);
    }

    public void openScreenLogin() throws IOException {
        buttonAddBooks.setVisible(false);
        buttonSearchEdit.setVisible(false);
        buttonLibraryStats.setVisible(false);
        buttonLogout.setVisible(false);
        labelTitle.setVisible(false);
        Pane paneLogin=FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
        panePrimary.getChildren().clear();
        panePrimary.getChildren().add(paneLogin);
    }
}
