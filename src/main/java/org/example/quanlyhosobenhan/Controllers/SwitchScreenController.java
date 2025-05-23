package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SwitchScreenController {
    public static void switchScreen(ActionEvent event, String fxmlPath, String title) {
       try{
           FXMLLoader loader = new FXMLLoader(SwitchScreenController.class.getResource(fxmlPath));
           Parent root = loader.load();
           Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
           Scene scene = new Scene(root);
           stage.setTitle(title);
           stage.setScene(scene);
           stage.show();
       }catch(Exception e){
           e.printStackTrace();
           System.err.println("Không thể chuyển tới: " + fxmlPath);
       }
    }
}
