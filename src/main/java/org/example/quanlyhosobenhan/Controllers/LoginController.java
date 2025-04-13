package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Model.Doctor;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.prefs.Preferences;


public class LoginController {

    @FXML
    private Button fb_btn;

    @FXML
    private Button gg_btn;

    @FXML
    private Button login_btn;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    // Biến lưu thông tin bác sĩ đã đăng nhập
    public static Doctor loggedInDoctor;

    @FXML
    public void initialize() {
        loadLoginInfo();
        passwordField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                login_btn.fire();
            }
        });

        userNameField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                login_btn.fire();
            }
        });
    }

    @FXML
    void handleCheckbox(ActionEvent event) {
        if(rememberMeCheckBox.isSelected()){
            saveLoginInfo();
        } else {
            clearLoginInfo();
        }
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        SwitchScreenController.switchScreen(event, "/Fxml/ForgotPassword.fxml", "Quên mật khẩu");
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = userNameField.getText();
        String plainPassword = passwordField.getText();

        if(username.isEmpty() || plainPassword.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.login(username, plainPassword);

        if(doctor != null){
            // Lưu bác sĩ đã đăng nhập vào biến toàn cục
            loggedInDoctor = doctor;

            if (rememberMeCheckBox.isSelected()) {
                saveLoginInfo();
            } else {
                clearLoginInfo();
            }

            showAlert(Alert.AlertType.INFORMATION, "Đăng Nhập Thành Công", "Chào mừng " + doctor.getName() + "!");
            SwitchScreenController.switchScreen(event, "/Fxml/Main.fxml", "Quản lý hồ sơ bệnh án");
        } else{
            showAlert(Alert.AlertType.ERROR, "Đăng Nhập Thất Bại", "Sai tên đăng nhập hoặc mật khẩu. Vui lòng thử lại!");
        }
    }

    public void loginWithFacebook() {
        openBrowser("http://localhost:8080/oauth2/authorization/facebook");
    }

    public void loginWithGoogle() {
        openBrowser("http://localhost:8080/oauth2/authorization/google");
    }

    public void openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onSignupLinkClick(ActionEvent event) throws IOException {
        SwitchScreenController.switchScreen(event, "/Fxml/Signup.fxml", "Đăng ký");
    }

    // Lưu userName và password vao trong máy người dùng
    private void saveLoginInfo() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("username", userNameField.getText());
        prefs.put("password", passwordField.getText());
    }

    private void clearLoginInfo(){
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("username");
        prefs.remove("password");
    }

    private void loadLoginInfo(){
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        String savedUsername = prefs.get("username", "");
        String savedPassword = prefs.get("password", "");

        if(!savedUsername.isEmpty() && !savedPassword.isEmpty()){
            userNameField.setText(savedUsername);
            passwordField.setText(savedPassword);
            rememberMeCheckBox.setSelected(true); // checkbox nếu có dữ liệu
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
