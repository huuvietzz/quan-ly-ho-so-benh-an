package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.StaffDAO;
import org.example.quanlyhosobenhan.Model.Staff;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StaffProfileController {

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
    private ComboBox<Staff.Gender> genderField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField numberPhoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField userNameField;

    private StaffDAO staffDAO = new StaffDAO();

    @FXML
    public void initialize() {
        getNameAccount();

        genderField.getItems().addAll(Staff.Gender.values());

        genderField.setConverter(new StringConverter<Staff.Gender>() {
            @Override
            public String toString(Staff.Gender gender) {
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
            public Staff.Gender fromString(String string) {
                switch (string) {
                    case "Nam":
                        return Staff.Gender.Male;
                    case "Nữ":
                        return Staff.Gender.Female;
                    case "Khác":
                        return Staff.Gender.Other;
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

        loadStaffInfo();
    }

    private void loadStaffInfo() {
        Staff staff = LoginController.loggedInStaff;
        if (staff == null) return;

        fullNameField.setText(staff.getFullName());
        userNameField.setText(staff.getUserName());
        emailField.setText(staff.getEmail());
        numberPhoneField.setText(staff.getPhone());
        addressField.setText(staff.getAddress());
        dateField.setValue(staff.getBirthDate());
        genderField.setValue(staff.getGender());
        departmentField.setText(staff.getDepartment());
        positionField.setText(staff.getPosition());
    }

    @FXML
    void updatePasswordBtn(ActionEvent event) {
        String currentPassword = passwordField.getText();
        String newPassword = newPasswordField.getText();

        Staff staff = LoginController.loggedInStaff;
        if (staff == null) return;

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới.");
            return;
        }

        if (!org.example.quanlyhosobenhan.Controllers.PasswordEncoder.checkPassword(currentPassword, staff.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng.");
            return;
        }

        boolean updated = staffDAO.updatePassword(staff.getUserName(), newPassword);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công.");
            // Cập nhật lại mật khẩu mới trong phiên đăng nhập hiện tại
            staff.setPassword(org.example.quanlyhosobenhan.Controllers.PasswordEncoder.hashPassword(newPassword));
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại.");
        }
    }


    @FXML
    void updateProfileBtn(ActionEvent event) {
        Staff staff = LoginController.loggedInStaff;
        if (staff == null) return;

        staff.setFullName(fullNameField.getText());
        staff.setUserName(userNameField.getText());
        staff.setEmail(emailField.getText());
        staff.setPhone(numberPhoneField.getText());
        staff.setAddress(addressField.getText());
        staff.setBirthDate(dateField.getValue());
        staff.setGender(genderField.getValue());
        staff.setDepartment(departmentField.getText());
        staff.setPosition(positionField.getText());

        try (var session = org.example.quanlyhosobenhan.Util.HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(staff);
            session.getTransaction().commit();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật hồ sơ thất bại!");
        }
    }

    private void getNameAccount() {
        Staff staff = LoginController.loggedInStaff;
        String userName = staff.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(staff.getUserName()));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
