package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Model.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientAppointmentController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private DatePicker dayField;

    @FXML
    private ComboBox<String> doctorCombobox;

    @FXML
    private TextArea reasonField;

    @FXML
    private ComboBox<String> specializationCombobox;

    private ToggleGroup timeGroup;

    private DoctorDAO doctorDAO = new DoctorDAO();

    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML
    public void initialize() {
        loadSpecializations();

        specializationCombobox.setOnAction(event -> {
            String selectedSpecialization = specializationCombobox.getValue();
            if (selectedSpecialization != null) {
                loadDoctorsBySpecialization(selectedSpecialization);
            }
        });

        // Định dạng ngày hiển thị theo dd-MM-yyyy
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

        dayField.setConverter(converter);

        // 1) Tạo ToggleGroup
        timeGroup = new ToggleGroup();

        // 2) Gán tất cả ToggleButton có styleClass="time-button" vào cùng nhóm
        rootPane.lookupAll(".time-button").stream()
                .filter(node -> node instanceof ToggleButton)
                .map(node -> (ToggleButton) node)
                .forEach(tb -> {
                    tb.setToggleGroup(timeGroup);
                });
    }

    private void loadSpecializations() {
        List<String> specializations = doctorDAO.getAllSpecializations();
        specializationCombobox.getItems().setAll(specializations);
    }

    private void loadDoctorsBySpecialization(String specialization) {
        List<String> doctors = doctorDAO.getDoctorsBySpecialization(specialization);
        doctorCombobox.getItems().setAll(doctors);
    }


    @FXML
    void makeAnAppointment(ActionEvent event) {
        String doctorName = doctorCombobox.getValue();
        LocalDate appointmentDate = dayField.getValue();
        String reason = reasonField.getText();
        String selectedTime = getSelectedTime();

        if (doctorName == null || appointmentDate == null || reason.isBlank() || selectedTime == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin và chọn giờ khám!");
            return;
        }

        Doctor doctor = doctorDAO.getDoctorByFullName(doctorName);
        if (doctor == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy bác sĩ!");
            return;
        }

        // Chuyển giờ được chọn thành LocalTime
        LocalTime time = LocalTime.parse(selectedTime); // ví dụ "09:30"
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, time);

        appointmentDAO.createAppointment(LoginController.loggedInPatient, doctor, appointmentDateTime, reason);
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đặt lịch thành công!");

        resetForm();
    }

    private String getSelectedTime() {
        Toggle selected = timeGroup.getSelectedToggle();
        return selected != null
                ? ((ToggleButton) selected).getText()
                : null;
    }

    private void resetForm() {
        specializationCombobox.setValue(null);
        doctorCombobox.getItems().clear();
        dayField.setValue(null);
        reasonField.clear();

        rootPane.lookupAll(".time-button").forEach(node -> {
            if (node instanceof ToggleButton) {
                ((ToggleButton) node).setSelected(false);
            }
        });
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
