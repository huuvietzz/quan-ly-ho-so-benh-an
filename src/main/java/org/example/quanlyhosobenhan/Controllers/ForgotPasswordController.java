package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    public void handleContinueBtn(ActionEvent event) throws IOException {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if(userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Vui lòng điền đầy đủ tất cả các trường!");
            return;
        }

        if(!password.equals(confirmPassword)){
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Mật khẩu và xác nhận mật khẩu không đúng!");
            return;
        }

        try {
            showAlert(Alert.AlertType.INFORMATION,"Thành công", "Mật khẩu của bạn đã được đặt lại thành công. Vui lòng đăng nhập lại!");
            backToLogin(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Không thể tải lại màn hình đăng nhập!");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
       SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
