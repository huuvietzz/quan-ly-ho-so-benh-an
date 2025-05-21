package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.quanlyhosobenhan.Dao.StaffDAO;
import org.example.quanlyhosobenhan.Model.Staff;

import java.io.IOException;
import java.util.regex.Pattern;

public class StaffSignupController {

    @FXML
    private TextField fullNameField, userNameField, emailField, numberPhoneField, departmentField, positionField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private TextArea addressField;

    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<Staff.Gender> genderField;

    @FXML
    private Label fullNameError, userNameError, emailError, passwordError, confirmPasswordError,
            numberPhoneError, departmentError, positionError, addressError, genderError, dateError;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{3,6}$";

    @FXML
    public void initialize() {
        genderField.getItems().addAll(Staff.Gender.values());
    }

    @FXML
    public void handleSignupBtnClick(ActionEvent event) throws IOException {
        boolean isValid = true;
        resetErrorLabels();

        if (fullNameField.getText().trim().isEmpty()) {
            fullNameError.setText("Họ tên không được để trống!");
            fullNameError.setVisible(true);
            isValid = false;
        }

        if (userNameField.getText().trim().isEmpty()) {
            userNameError.setText("Tên đăng nhập không được để trống!");
            userNameError.setVisible(true);
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty()) {
            emailError.setText("Email không được để trống!");
            emailError.setVisible(true);
            isValid = false;
        } else if (!isValidEmail(emailField.getText().trim())) {
            emailError.setText("Email không hợp lệ!");
            emailError.setVisible(true);
            isValid = false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            passwordError.setText("Mật khẩu không được để trống!");
            passwordError.setVisible(true);
            isValid = false;
        } else if (passwordField.getText().length() < 6) {
            passwordError.setText("Mật khẩu phải ít nhất 6 ký tự!");
            passwordError.setVisible(true);
            isValid = false;
        }

        if (confirmPasswordField.getText().trim().isEmpty()) {
            confirmPasswordError.setText("Xác nhận mật khẩu không được để trống!");
            confirmPasswordError.setVisible(true);
            isValid = false;
        } else if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            confirmPasswordError.setText("Mật khẩu xác nhận không khớp!");
            confirmPasswordError.setVisible(true);
            isValid = false;
        }

        if (numberPhoneField.getText().trim().isEmpty()) {
            numberPhoneError.setText("Số điện thoại không được để trống!");
            numberPhoneError.setVisible(true);
            isValid = false;
        } else if (!numberPhoneField.getText().matches("^0\\d{9}$")) {
            numberPhoneError.setText("Số điện thoại không hợp lệ!");
            numberPhoneError.setVisible(true);
            isValid = false;
        }

        if (departmentField.getText().trim().isEmpty()) {
            departmentError.setText("Khoa/phòng ban không được để trống!");
            departmentError.setVisible(true);
            isValid = false;
        }

        if (positionField.getText().trim().isEmpty()) {
            positionError.setText("Chức vụ không được để trống!");
            positionError.setVisible(true);
            isValid = false;
        }

        if (addressField.getText().trim().isEmpty()) {
            addressError.setText("Địa chỉ không được để trống!");
            addressError.setVisible(true);
            isValid = false;
        }

        if (genderField.getValue() == null) {
            genderError.setText("Giới tính không được để trống!");
            genderError.setVisible(true);
            isValid = false;
        }

        if (dateField.getValue() == null) {
            dateError.setText("Ngày sinh không được để trống!");
            dateError.setVisible(true);
            isValid = false;
        }

        if (isValid) {
            Staff staff = new Staff();
            staff.setFullName(fullNameField.getText().trim());
            staff.setUserName(userNameField.getText().trim());
            staff.setEmail(emailField.getText().trim());
            staff.setPassword(passwordField.getText().trim());
            staff.setPhone(numberPhoneField.getText().trim());
            staff.setAddress(addressField.getText().trim());
            staff.setDepartment(departmentField.getText().trim());
            staff.setPosition(positionField.getText().trim());
            staff.setGender(genderField.getValue());
            staff.setBirthDate(dateField.getValue());

            try {
                StaffDAO staffDAO = new StaffDAO();
                boolean registered = staffDAO.register(staff);

                if (registered) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công!");
                    SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Đăng ký thất bại. Vui lòng thử lại.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
            }
        }
    }

    private void resetErrorLabels() {
        fullNameError.setVisible(false);
        userNameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        confirmPasswordError.setVisible(false);
        numberPhoneError.setVisible(false);
        addressError.setVisible(false);
        departmentError.setVisible(false);
        positionError.setVisible(false);
        genderError.setVisible(false);
        dateError.setVisible(false);
    }

    private void clearForm() {
        fullNameField.clear();
        userNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        numberPhoneField.clear();
        addressField.clear();
        departmentField.clear();
        positionField.clear();
        genderField.setValue(null);
        dateField.setValue(null);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
    }
}

