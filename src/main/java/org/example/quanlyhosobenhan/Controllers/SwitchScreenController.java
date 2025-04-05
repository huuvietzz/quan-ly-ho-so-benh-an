package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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


//    public static void switchScreen(ActionEvent event, String fxmlPath, String title) {
//        try {
//            FXMLLoader loader = new FXMLLoader(SwitchScreenController.class.getResource(fxmlPath));
//            Parent root = loader.load();
//
//            Stage stage = new Stage();
//            Scene scene = new Scene(root);
//
//            // Kiểm tra nếu là Dashboard thì bỏ viền cửa sổ
//            if (fxmlPath.contains("Main.fxml")) {
//                stage.initStyle(StageStyle.UNDECORATED);
//            }
//
//            stage.setTitle(title);
//            stage.setScene(scene);
//            stage.show();
//
//            // Đóng màn hình hiện tại
//            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            currentStage.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Không thể chuyển tới: " + fxmlPath);
//        }
//    }
}
