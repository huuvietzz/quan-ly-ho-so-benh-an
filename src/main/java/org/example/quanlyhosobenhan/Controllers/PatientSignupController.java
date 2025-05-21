package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Patient;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatientSignupController {

    @FXML private Label fullNameError;
    @FXML private TextField fullNameField;

    @FXML private Label userNameError;
    @FXML private TextField userNameField;

    @FXML private Label emailError;
    @FXML private TextField emailField;

    @FXML private Label passwordError;
    @FXML private PasswordField passwordField;

    @FXML private Label confirmPasswordError;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label numberPhoneError;
    @FXML private TextField numberPhoneField;

    @FXML private Label nationalIdError;
    @FXML private TextField nationalIdField;

    @FXML private Label healthInsuranceIdError;
    @FXML private TextField healthInsuranceIdField;

    @FXML private Label addressError;
    @FXML private TextArea addressField;

    @FXML private Label genderError;
    @FXML private ComboBox<Patient.Gender> genderField;

    @FXML private Label dateError;
    @FXML private DatePicker dateField;

    @FXML private Button signup_btn;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @FXML
    public void initialize() {
        genderField.getItems().addAll(Patient.Gender.values());
    }

    @FXML
    void handleSignupBtnClick(ActionEvent event) throws IOException {
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

        if (nationalIdField.getText().trim().isEmpty()) {
            nationalIdError.setText("CMND/CCCD không được để trống!");
            nationalIdError.setVisible(true);
            isValid = false;
        }

        if (healthInsuranceIdField.getText().trim().isEmpty()) {
            healthInsuranceIdError.setText("Số BHYT không được để trống!");
            healthInsuranceIdError.setVisible(true);
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
            Patient patient = new Patient();
            patient.setFullName(fullNameField.getText().trim());
            patient.setUserName(userNameField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setPassword(passwordField.getText().trim());
            patient.setPhone(numberPhoneField.getText().trim());
            patient.setGender(genderField.getValue());
            patient.setBirthdate(dateField.getValue());
            patient.setAddress(addressField.getText().trim());
            patient.setNationalId(nationalIdField.getText().trim());
            patient.setHealthInsuranceId(healthInsuranceIdField.getText().trim());

            try {
                PatientDAO patientDAO = new PatientDAO();
                boolean isRegistered = patientDAO.register(patient);

                if (isRegistered) {
                    showAlert(Alert.AlertType.INFORMATION, "Đăng ký thành công", "Tài khoản của bạn đã được tạo!");
                    SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Đăng ký thất bại", "Tên đăng nhập đã tồn tại hoặc có lỗi xảy ra!");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        nationalIdError.setVisible(false);
        healthInsuranceIdError.setVisible(false);
        addressError.setVisible(false);
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
        nationalIdField.clear();
        healthInsuranceIdField.clear();
        addressField.clear();
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
    void backToLogin(ActionEvent event) throws IOException {
        SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
    }
}
