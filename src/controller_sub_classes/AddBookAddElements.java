package controller_sub_classes;

import controllers.AddBookController;

public class AddBookAddElements {
    static AddBookController addBookController = new AddBookController();

    public static void printTest(){
        addBookController.textFieldTitle.getText();
    }
}
