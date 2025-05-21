package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class StaffMedicalRecordController {

    @FXML
    private TableColumn<?, ?> consultationDateColumn;

    @FXML
    private TableColumn<?, ?> diagnoseColumn;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<?, ?> idRecordColumn;

    @FXML
    private TableColumn<?, ?> noteColumn;

    @FXML
    private TableColumn<?, ?> patientColumn;

    @FXML
    private TableColumn<?, ?> prescriptionColumn;

    @FXML
    private TableView<?> recordTable;

    @FXML
    private TextField searchTextField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<?, ?> symptomColumn;

    @FXML
    private TableColumn<?, ?> treatmentColumn;

    @FXML
    void exportToExcel(ActionEvent event) {

    }

    @FXML
    void printMedicalRecord(ActionEvent event) {

    }

}
