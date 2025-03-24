package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.regex.Pattern;

public class SignupController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField userNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @FXML
    public void onSignupBtnClick(ActionEvent event) {
        String fullName = fullNameField.getText();
        String userName = userNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Kiểm tra trường trống
        if(fullName.isEmpty() || userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Vui lòng điền đầy đủ tất cả các trường");
            return;
        }

        // Kiểm tra định dạng email và khớp mật khẩu
        if(!isValidEmail(email) && !password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Email không hợp lệ, mật khẩu và xác nhận mật khẩu không khớp. Vui lòng điền lại !");
            return;
        }

        // Kiểm tra định dạng email
        if(!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Email không hợp lệ. Vui lòng đăng nhập đúng định dạng email");
            return;
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if(!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Mật khẩu và xác nhận mật khẩu không khớp.");
            return;
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        try {
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.registerUser(userName, password);
            showAlert(Alert.AlertType.INFORMATION,"Thành công", "Đăng ký thành công! Hãy đăng nhập.");
            backToLogin(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,"Lỗi", "Không thể quay lại màn hình đăng nhập.");
        }
    }

//    // Mã hóa mật khẩu bằng BCrypt
//    private String hashPassword(String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt());
//    }

    // Kiểm tra định dang email
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    // Hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        // Lay man hinh Login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        Parent root = loader.load();

        // Lấy stage hiện tại và chuyển màn hình
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
