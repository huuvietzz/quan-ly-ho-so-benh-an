package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Model.Doctor;

import java.io.IOException;
import java.util.regex.Pattern;

public class SignupController {
    @FXML
    private Label addressError;

    @FXML
    private TextArea addressField;

    @FXML
    private Label confirmPasswordError;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label dateError;

    @FXML
    private DatePicker dateField;

    @FXML
    private Label departmentError;

    @FXML
    private TextField departmentField;

    @FXML
    private Label emailError;

    @FXML
    private TextField emailField;

    @FXML
    private Label fullNameError;

    @FXML
    private TextField fullNameField;

    @FXML
    private Label userNameError;

    @FXML
    private TextField userNameField;

    @FXML
    private Label genderError;

    @FXML
    private ComboBox<Doctor.Gender> genderField;

    @FXML
    private Label numberPhoneError;

    @FXML
    private TextField numberPhoneField;

    @FXML
    private Label passwordError;

    @FXML
    private PasswordField passwordField;


    @FXML
    private Label positionError;

    @FXML
    private TextField positionField;

    @FXML
    private Button signup_btn;

    @FXML
    private Label specializationError;

    @FXML
    private TextField specializationField;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{3,6}$";

    @FXML
    public void initialize() {
        genderField.getItems().addAll(Doctor.Gender.values());
    }

    @FXML
    public void handleSignupBtnClick(ActionEvent event) throws IOException {
        boolean isValid = true;

        resetErrorLabels();

        if(fullNameField.getText().trim().isEmpty()) {
            fullNameError.setText("Họ tên không được để trống!");
            fullNameError.setVisible(true);
            isValid = false;
        }

        if(userNameField.getText().trim().isEmpty()) {
            userNameError.setText("Tên đăng nhập không được để trống!");
            userNameError.setVisible(true);
            isValid = false;
        }

        if(emailField.getText().trim().isEmpty()) {
            emailError.setText("Email không được để trống!");
            emailError.setVisible(true);
            isValid = false;
        } else if(!isValidEmail(emailField.getText().trim())) {
            emailError.setText("Email không hợp lệ!");
            emailError.setVisible(true);
            isValid = false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            passwordError.setText("Mật khẩu không được để trống!");
            passwordError.setVisible(true);
            isValid = false;
        } else if (passwordField.getText().trim().length() < 6) {
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
        } else if(!numberPhoneField.getText().trim().matches("^0\\d{9}$")) {
            numberPhoneError.setText("Số điện thoại không hợp lệ!");
            numberPhoneError.setVisible(true);
            isValid = false;
        }

        if (addressField.getText().trim().isEmpty()) {
            addressError.setText("Địa chỉ không được để trống!");
            addressError.setVisible(true);
            isValid = false;
        }

        if (departmentField.getText().trim().isEmpty()) {
            departmentError.setText("Khoa không được để trống!");
            departmentError.setVisible(true);
            isValid = false;
        }

        if(specializationField.getText().trim().isEmpty()) {
            specializationError.setText("Chuyên môn không được để trống!");
            specializationError.setVisible(true);
            isValid = false;
        }

        if (positionField.getText().trim().isEmpty()) {
            positionError.setText("Chức vụ không được để trống!");
            positionError.setVisible(true);
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

        if(isValid) {
            Doctor doctor = new Doctor();
            doctor.setName(fullNameField.getText().trim());
            doctor.setUserName(userNameField.getText().trim());
            doctor.setEmail(emailField.getText().trim());
            doctor.setPhone(numberPhoneField.getText().trim());
            doctor.setPassword(passwordField.getText().trim());
            doctor.setGender(genderField.getValue());
            doctor.setSpecialization(specializationField.getText().trim());
            doctor.setDepartment(departmentField.getText().trim());
            doctor.setPosition(positionField.getText().trim());
            doctor.setAddress(addressField.getText().trim());
            doctor.setBirthDate(dateField.getValue());
            try{
                DoctorDAO doctorDAO = new DoctorDAO();
                boolean isRegistered = doctorDAO.register(doctor);

                if(isRegistered) {
                    showAlert(Alert.AlertType.INFORMATION, "Đăng ký thành công", "Tài khoản của bạn đã được tạo!");
                    SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Đăng ký thất bại", "Có lỗi xảy ra trong quá trình đăng ký!");
                }
            } catch(Exception e){
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
        addressError.setVisible(false);
        departmentError.setVisible(false);
        specializationError.setVisible(false);
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
        specializationField.clear();
        positionField.clear();
        genderField.setValue(null);
        dateField.setValue(null);
    }

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
        SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
    }

}
