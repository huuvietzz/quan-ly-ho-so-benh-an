package org.example.quanlyhosobenhan.Controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class StaffDashboardController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label avatarLabel;

    @FXML
    private TableColumn<?, ?> idAppointmentColumn;

    @FXML
    private FontAwesomeIconView messageIcon;

    @FXML
    private FontAwesomeIconView notificationIcon;

    @FXML
    private TableColumn<?, ?> patientColumn;

    @FXML
    private PieChart pieChart;

    @FXML
    private TableColumn<?, ?> reasonColumn;

    @FXML
    private TableColumn<?, ?> roomNumberColumn;

    @FXML
    private TableColumn<?, ?> statusColumn;

    @FXML
    private TableColumn<?, ?> timeColumn;

    @FXML
    private Label totalAppointments;

    @FXML
    private Label totalMedicalRecords;

    @FXML
    private Label totalPatients;

    @FXML
    private StackPane userAvatar;

}
