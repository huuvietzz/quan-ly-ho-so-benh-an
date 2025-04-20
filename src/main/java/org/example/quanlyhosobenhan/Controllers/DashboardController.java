package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;


public class DashboardController {

    @FXML
    private Label totalMedicalRecords;

    @FXML
    private Label totalPatients;

    @FXML
    private Label totalAppointments;

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData(){
        PatientDAO patientDAO = new PatientDAO();
        MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

        Long patientCount = patientDAO.countByDoctorId(LoginController.loggedInDoctor.getId());
        Long recordCount = medicalRecordDAO.countByDoctorId(LoginController.loggedInDoctor.getId());

        totalPatients.setText(String.valueOf(patientCount));
        totalMedicalRecords.setText(String.valueOf(recordCount));
    }
    @FXML
    void search(ActionEvent event) {

    }
}
