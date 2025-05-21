package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.example.quanlyhosobenhan.Dao.DoctorDAO;

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

    @FXML
    public void initialize() {
        specializationCombobox.getItems().addAll("Tim mạch", "Nhi khoa", "Da liễu", "Tai Mũi Họng");

        specializationCombobox.setOnAction(event -> {
            String selectedSpecialization = specializationCombobox.getValue();
            loadDoctorsBySpecialization(selectedSpecialization);
        });
    }

    private void loadDoctorsBySpecialization(String specialization) {
        List<String> doctors = doctorDAO.getDoctorsBySpecialization(specialization);
        doctorCombobox.getItems().setAll(doctors);
    }



    @FXML
    void makeAnAppointment(ActionEvent event) {

    }
}
