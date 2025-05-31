package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Model.Doctor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DoctorProfileController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label avatarLabel;

    @FXML
    private StackPane userAvatar;

    @FXML
    private TextArea addressField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<Doctor.Gender> genderField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField numberPhoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField specializationField;

    @FXML
    private TextField userNameField;

    private DoctorDAO doctorDAO = new DoctorDAO();

    @FXML
    public void initialize() {
        getNameAccount();

        genderField.getItems().addAll(Doctor.Gender.values());

        genderField.setConverter(new StringConverter<Doctor.Gender>() {
            @Override
            public String toString(Doctor.Gender gender) {
                if (gender == null) return "";
                switch (gender) {
                    case Male:
                        return "Nam";
                    case Female:
                        return "Nữ";
                    case Other:
                        return "Khác";
                    default:
                        return gender.name();
                }
            }

            @Override
            public Doctor.Gender fromString(String string) {
                switch (string) {
                    case "Nam":
                        return Doctor.Gender.Male;
                    case "Nữ":
                        return Doctor.Gender.Female;
                    case "Khác":
                        return Doctor.Gender.Other;
                    default:
                        return null;
                }
            }
        });

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(string, dateFormatter);
            }
        };

        dateField.setConverter(converter);

        loadDoctorInfo();
    }

    private void loadDoctorInfo() {
        Doctor doctor = LoginController.loggedInDoctor;
        if (doctor == null) return;

        fullNameField.setText(doctor.getFullName());
        userNameField.setText(doctor.getUserName());
        emailField.setText(doctor.getEmail());
        numberPhoneField.setText(doctor.getPhone());
        addressField.setText(doctor.getAddress());
        dateField.setValue(doctor.getBirthDate());
        genderField.setValue(doctor.getGender());
        departmentField.setText(doctor.getDepartment());
        specializationField.setText(doctor.getSpecialization());
        positionField.setText(doctor.getPosition());
    }

    @FXML
    void updatePasswordBtn(ActionEvent event) {
        String currentPassword = passwordField.getText();
        String newPassword = newPasswordField.getText();

        Doctor doctor = LoginController.loggedInDoctor;
        if (doctor == null) return;

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới.");
            return;
        }

        if (!org.example.quanlyhosobenhan.Controllers.PasswordEncoder.checkPassword(currentPassword, doctor.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng.");
            return;
        }

        boolean updated = doctorDAO.updatePassword(doctor.getUserName(), newPassword);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công.");
            // Cập nhật lại mật khẩu mới trong phiên đăng nhập hiện tại
            doctor.setPassword(org.example.quanlyhosobenhan.Controllers.PasswordEncoder.hashPassword(newPassword));
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại.");
        }
    }


    @FXML
    void updateProfileBtn(ActionEvent event) {
        Doctor doctor = LoginController.loggedInDoctor;
        if (doctor == null) return;

        doctor.setFullName(fullNameField.getText());
        doctor.setUserName(userNameField.getText());
        doctor.setEmail(emailField.getText());
        doctor.setPhone(numberPhoneField.getText());
        doctor.setAddress(addressField.getText());
        doctor.setBirthDate(dateField.getValue());
        doctor.setGender(genderField.getValue());
        doctor.setDepartment(departmentField.getText());
        doctor.setSpecialization(specializationField.getText());
        doctor.setPosition(positionField.getText());

        try (var session = org.example.quanlyhosobenhan.Util.HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(doctor);
            session.getTransaction().commit();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật hồ sơ thất bại!");
        }
    }

    private void getNameAccount() {
        Doctor doctor = LoginController.loggedInDoctor;
        String userName = doctor.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(doctor.getUserName()));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
