package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Appointment;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientDashboardController {

    @FXML
    private Label totalAppointments;

    @FXML
    private Label totalMedicalRecords;

    @FXML
    private Label avatarLabel;

    @FXML
    private StackPane userAvatar;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> doctorColumn;

    @FXML
    private TableColumn<Appointment, Integer> idAppointmentColumn;

    @FXML
    private TableColumn<Appointment, String> reasonColumn;

    @FXML
    private TableColumn<Appointment, String> roomNumberColumn;

    @FXML
    private TableColumn<Appointment, String> statusColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> timeColumn;

    PatientDAO patientDAO = new PatientDAO();
    MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML
    public void initialize() {
        loadCard();

        getNameAccount();

        appointmentTable.setPlaceholder(new Label("❌ Không có cuộc hẹn nào."));
        appointmentTable.setEditable(true);

        idAppointmentColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getId()));


        doctorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDoctor().getFullName()));

        reasonColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReason()));

        roomNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoomNumber()));

        roomNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roomNumberColumn.setOnEditCommit(event -> {
            Appointment appointment = event.getRowValue();
            appointment.setRoomNumber(event.getNewValue());
        });

        statusColumn.setCellValueFactory(cellData -> {
            Appointment.Status status = cellData.getValue().getStatus();
            String displayText;

            switch (status) {
                case Pending -> displayText = "Đang chờ";
                case Confirmed -> displayText = "Đã xác nhận";
                case Cancelled -> displayText = "Đã hủy";
                default -> displayText = "Đã hoàn thành";
            }

            return new SimpleStringProperty(displayText);
        });

        timeColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAppointmentTime()));

        timeColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        refreshTable();
    }

    private void loadCard(){
        int patientId = LoginController.loggedInPatient.getId();

        Long recordCount = medicalRecordDAO.countByPatientId(patientId);
        Long appointmentCount = appointmentDAO.countByPatientId(patientId);

        totalMedicalRecords.setText(String.valueOf(recordCount));
        totalAppointments.setText(String.valueOf(appointmentCount));
    }

    private void getNameAccount() {
        Patient patient = LoginController.loggedInPatient;
        String userName = patient.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(patient.getUserName()));
    }

    private void refreshTable() {
        appointmentTable.getItems().clear();
        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(LoginController.loggedInPatient.getId());
        appointmentTable.getItems().addAll(appointments);
    }

}
