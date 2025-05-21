package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Dao.StaffDAO;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Model.Staff;

import java.io.IOException;
import java.util.prefs.Preferences;


public class LoginController {

    @FXML
    private Button login_btn;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private ComboBox<String> role;

    // Biến lưu thông tin bác sĩ đã đăng nhập
    public static Doctor loggedInDoctor;

    // Biến lưu thông tin bệnh nhân đã đăng nhập
    public static Patient loggedInPatient;

    // Biến lưu thông tin nhan viên đã đăng nhập
    public static Staff loggedInStaff;

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

        role.getItems().addAll("Bác sĩ", "Nhân viên","Bệnh nhân");
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

        String selectedRole = role.getValue();
        if (selectedRole == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn vai trò đăng nhập!");
            return;
        }

        switch (selectedRole) {
            case "Bác sĩ" -> {
                Doctor doctor = new DoctorDAO().login(username, plainPassword);
                if (doctor != null) {
                    loggedInDoctor = doctor;
                    showSuccess(event, doctor.getUserName(), "/Fxml/DoctorMain.fxml");
                } else {
                    showFail();
                }
            }

            case "Nhân viên" -> {
                Staff staff = new StaffDAO().login(username, plainPassword);
                if (staff != null) {
                     loggedInStaff = staff;
                    showSuccess(event, staff.getUserName(), "/Fxml/StaffMain.fxml");
                } else {
                    showFail();
                }
            }

            case "Bệnh nhân" -> {
                Patient patient = new PatientDAO().login(username, plainPassword);
                if (patient != null) {
                    loggedInPatient = patient;
                    showSuccess(event, patient.getUserName(), "/Fxml/PatientMain.fxml");
                } else {
                    showFail();
                }
            }

            default -> {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vai trò không hợp lệ!");
            }
        }
    }

    private void showSuccess(ActionEvent event, String username, String fxmlPath) {
        if (rememberMeCheckBox.isSelected()) {
            saveLoginInfo();
        } else {
            clearLoginInfo();
        }

        showAlert(Alert.AlertType.INFORMATION, "Đăng Nhập Thành Công", "Chào mừng " + username + "!");
        SwitchScreenController.switchScreen(event, fxmlPath, "Hồ sơ bệnh án");
    }

    private void showFail() {
        showAlert(Alert.AlertType.ERROR, "Đăng Nhập Thất Bại", "Sai tên đăng nhập hoặc mật khẩu. Vui lòng thử lại!");
    }

    @FXML
    public void onSignupLinkClick(ActionEvent event) throws IOException {
        String selectedRole = role.getValue();
        if (selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn vai trò", "Vui lòng chọn vai trò trước khi đăng ký.");
            return;
        }

        switch (selectedRole) {
            case "Bác sĩ" -> {
                SwitchScreenController.switchScreen(event, "/Fxml/DoctorSignup.fxml", "Đăng ký Bác sĩ");
            }
            case "Nhân viên" -> {
                SwitchScreenController.switchScreen(event, "/Fxml/StaffSignup.fxml", "Đăng ký Nhân viên");
            }
            case "Bệnh nhân" -> {
                SwitchScreenController.switchScreen(event, "/Fxml/PatientSignup.fxml", "Đăng ký Bệnh nhân");
            }
            default -> {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vai trò không hợp lệ!");
            }
        }
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
