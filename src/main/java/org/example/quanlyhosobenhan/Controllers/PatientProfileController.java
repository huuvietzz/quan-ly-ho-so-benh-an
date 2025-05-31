package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientProfileController {

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
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<Patient.Gender> genderField;

    @FXML
    private TextField healthInsuranceIdField;

    @FXML
    private TextField nationalIdField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField numberPhoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField userNameField;

    private PatientDAO patientDAO = new PatientDAO();

    @FXML
    public void initialize() {
        // 1. Hiển thị ký tự đầu của userName lên avatarLabel
        getNameAccount();

        // 2. Thiết lập ComboBox gender với enum Patient.Gender
        genderField.getItems().addAll(Patient.Gender.values());
        genderField.setConverter(new StringConverter<Patient.Gender>() {
            @Override
            public String toString(Patient.Gender gender) {
                if (gender == null) return "";
                switch (gender) {
                    case Male:   return "Nam";
                    case Female: return "Nữ";
                    case Other:  return "Khác";
                    default:     return gender.name();
                }
            }
            @Override
            public Patient.Gender fromString(String string) {
                if (string == null) return null;
                switch (string) {
                    case "Nam":  return Patient.Gender.Male;
                    case "Nữ":   return Patient.Gender.Female;
                    case "Khác": return Patient.Gender.Other;
                    default:     return null;
                }
            }
        });

        // 3. Thiết lập định dạng cho DatePicker: dd-MM-yyyy
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

        // 4. Nạp dữ liệu thông tin bệnh nhân đang đăng nhập lên form
        loadPatientInfo();
    }


    private void loadPatientInfo() {
        Patient patient = LoginController.loggedInPatient;
        if (patient == null) return;

        fullNameField.setText(patient.getFullName());
        userNameField.setText(patient.getUserName());
        emailField.setText(patient.getEmail());
        numberPhoneField.setText(patient.getPhone());
        addressField.setText(patient.getAddress());
        dateField.setValue(patient.getBirthdate());
        genderField.setValue(patient.getGender());
        healthInsuranceIdField.setText(patient.getHealthInsuranceId());
        nationalIdField.setText(patient.getNationalId());
    }


    private void getNameAccount() {
        Patient patient = LoginController.loggedInPatient;
        if (patient == null) return;

        String userName = patient.getUserName();
        if (userName != null && !userName.trim().isEmpty()) {
            String initial = userName.trim().substring(0, 1).toUpperCase();
            avatarLabel.setText(initial);
        }
        Tooltip.install(userAvatar, new Tooltip(patient.getUserName()));
    }


    @FXML
    void updatePasswordBtn(ActionEvent event) {
        String currentPassword = passwordField.getText();
        String newPassword = newPasswordField.getText();

        Patient patient = LoginController.loggedInPatient;
        if (patient == null) return;

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới.");
            return;
        }

        if (!org.example.quanlyhosobenhan.Controllers.PasswordEncoder.checkPassword(
                currentPassword, patient.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng.");
            return;
        }


        boolean updated = patientDAO.updatePassword(patient.getUserName(), newPassword);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công.");

            // Cập nhật lại mật khẩu mới trong phiên đăng nhập hiện tại
            patient.setPassword(org.example.quanlyhosobenhan.Controllers.PasswordEncoder.hashPassword(newPassword));
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }
    }


    @FXML
    void updateProfileBtn(ActionEvent event) {
        Patient patient = LoginController.loggedInPatient;
        if (patient == null) return;

        // Lấy dữ liệu mới từ các field
        patient.setFullName(fullNameField.getText());
        patient.setUserName(userNameField.getText());
        patient.setEmail(emailField.getText());
        patient.setPhone(numberPhoneField.getText());
        patient.setAddress(addressField.getText());
        patient.setBirthdate(dateField.getValue());
        patient.setGender(genderField.getValue());
        patient.setHealthInsuranceId(healthInsuranceIdField.getText());
        patient.setNationalId(nationalIdField.getText());

        // Cập nhật vào CSDL qua Hibernate
        try (var session = org.example.quanlyhosobenhan.Util.HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(patient);
            session.getTransaction().commit();

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật hồ sơ thất bại!");
        }
    }

    /**
     * Hiển thị hộp thoại Alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
