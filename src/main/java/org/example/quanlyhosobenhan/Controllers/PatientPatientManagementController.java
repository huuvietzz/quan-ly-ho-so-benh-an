package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PatientPatientManagementController {

    @FXML
    private TableColumn<?, ?> addressColumn;

    @FXML
    private TableColumn<?, ?> dobColumn;

    @FXML
    private TableColumn<?, ?> emailColumn;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<?, ?> genderColumn;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableView<?> patientTable;

    @FXML
    private TableColumn<?, ?> phoneNumberColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    void exportToExcel(ActionEvent event) {

    }

    @FXML
    void refresh(ActionEvent event) {

    }

}
