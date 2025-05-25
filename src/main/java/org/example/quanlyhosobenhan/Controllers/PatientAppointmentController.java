package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;
import org.example.quanlyhosobenhan.Model.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PatientAppointmentController {

    @FXML
    private DatePicker dayField;

    @FXML
    private ComboBox<String> doctorCombobox;

    @FXML
    private TextArea reasonField;

    @FXML
    private ComboBox<String> specializationCombobox;

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

        if (doctorName == null || appointmentDate == null || reason.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        Doctor doctor = doctorDAO.getDoctorByFullName(doctorName);
        if (doctor == null) {
            System.out.println("Không tìm thấy bác sĩ!");
            return;
        }

        LocalDateTime appointmentDateTime = appointmentDate.atTime(9, 0);
        appointmentDAO.createAppointment(LoginController.loggedInPatient, doctor, appointmentDateTime, reason);

        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đặt lịch thành công!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
