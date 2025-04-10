package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Button fb_btn;

    @FXML
    private Button gg_btn;

    private Preferences prefs;

    private Map<String, String> users = new HashMap<>();

    // Checkbox
    public void initialize() {
        prefs = Preferences.userNodeForPackage(LoginController.class);

        // Load thông tin tài khoản đã lưu
        loadUsersFromPreferences();

        // Kiểm tra và điền thông tin nếu có tài khoản được nhớ
        String rememberedUser = prefs.get("rememberedUser", "");
        if (!rememberedUser.isEmpty() && users.containsKey(rememberedUser)) {
            usernameField.setText(rememberedUser);
            passwordField.setText(users.get(rememberedUser));
            rememberMeCheckBox.setSelected(true);
        }
    }

    private void loadUsersFromPreferences() {
        int userCount = prefs.getInt("userCount", 0);
        for(int i = 0; i < userCount; i++) {
            String username = prefs.get("username" + i, "");
            String password = prefs.get("password" + i, "");
            if(!username.isEmpty() && !password.isEmpty()) {
                users.put(username, password);
            }
        }
    }

    private void saveUsersToPreferences(String username, String password) {
        int userCount = prefs.getInt("userCount", 0);
        prefs.put("username" + userCount, username);
        prefs.put("password" + userCount, password);
        prefs.putInt("userCount", userCount + 1);
        users.put(username, password);
    }

    // Quên mật khẩu
    @FXML
    public void handleForgotPassword(ActionEvent event) {
        SwitchScreenController.switchScreen(event, "/Fxml/ForgotPassword.fxml", "Quên mật khẩu");
    }

    // Đăng nhập
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            showAlert(Alert.AlertType.INFORMATION,"Thành công", "Đăng nhập thành công!");

            if (rememberMeCheckBox.isSelected()) {
                prefs.put("rememberedUser", username);
            } else {
                prefs.remove("rememberedUser");
            }
        } else {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        SwitchScreenController.switchScreen(event, "/Fxml/Main.fxml", "Quản lý hồ sơ bệnh án");
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

    // Đăng ký
    @FXML
    public void onSignupLinkClick(ActionEvent event) throws IOException {
           SwitchScreenController.switchScreen(event, "/Fxml/Signup.fxml", "Đăng ký tài khoản");
    }

    // Hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void registerUser(String username, String password) {
        saveUsersToPreferences(username, password);
    }
}
