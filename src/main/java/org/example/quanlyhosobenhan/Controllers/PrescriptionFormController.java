package org.example.quanlyhosobenhan.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.quanlyhosobenhan.Dao.PrescriptionDAO;
import org.example.quanlyhosobenhan.Dao.PrescriptionDetailDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Model.Prescription;
import org.example.quanlyhosobenhan.Model.PrescriptionDetail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PrescriptionFormController {
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;

    @FXML
    private TableColumn<PrescriptionDetail, Void> actionColumn;

    @FXML
    private TextField doctorField;

    @FXML
    private TableColumn<PrescriptionDetail, String> dosageColumn;

    @FXML
    private TextArea generalNotesField;

    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> quantityColumn;

    @FXML
    private TableColumn<PrescriptionDetail, String> unitColumn;

    @FXML
    private TableColumn<PrescriptionDetail, String> noteColum;

    @FXML
    private TextField patientField;

    @FXML
    private TextField prescriptionDateField;

    @FXML
    private TableColumn<PrescriptionDetail, String> usageInstructionsColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    private final ObservableList<PrescriptionDetail> prescriptionDetails = FXCollections.observableArrayList();

    private Patient patient;

    private Prescription existingPrescription;

    private MedicalRecord medicalRecord;

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patientField != null) {
            patientField.setText(patient.getName());
        }
    }

    public void setExistingPrescription(Prescription existingPrescription) {
        this.existingPrescription = existingPrescription;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }


    @FXML
    public void initialize() {
        // Gán danh sách dữ liệu vào bảng
        prescriptionTable.setItems(prescriptionDetails);

        // Cột dữ liệu
        medicineNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicineName()));
        setupEllipsisColumn(medicineNameColumn, "Chi tiết tên thuốc");

        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<Integer>(cellData.getValue().getQuantity()));

        unitColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnit()));
        setupEllipsisColumn(unitColumn, "Chi tiết đơn vị tính");

        dosageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDosage()));
        setupEllipsisColumn(dosageColumn, "Chi tiết liều lượng");

        usageInstructionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsageInstructions()));
        setupEllipsisColumn(usageInstructionsColumn, "Chi tiết cách dùng");

        noteColum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));
        setupEllipsisColumn(noteColum, "Chi tiết ghi chú");

        addActionButtonsToTable();

        // Gán sẵn thông tin bệnh nhân, bác sĩ, ngày kê
        if(LoginController.loggedInDoctor != null) {
            doctorField.setText(LoginController.loggedInDoctor.getUserName());
            doctorField.setEditable(false);
        }
        
        Platform.runLater(() -> {
            if (patient != null) {
                patientField.setText(patient.getName());
                patientField.setEditable(false);
            }
        });

        prescriptionDateField.setEditable(false);

        Platform.runLater(() -> {
            if (existingPrescription != null) {
                generalNotesField.setText(existingPrescription.getNotes());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH'h':mm'm':ss's'");
                prescriptionDateField.setText(existingPrescription.getPrescriptionDate().format(formatter));
                prescriptionDetails.setAll(PrescriptionDetailDAO.getByPrescriptionId(existingPrescription.getId()));
            } else {
                deleteButton.setVisible(false);
            }
        });

    }

    @FXML
    void addBtn(ActionEvent event) {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/AddPrescriptionForm.fxml"));
           Parent root = loader.load();

           Stage stage = new Stage();
           stage.setTitle("Thêm đơn thuốc");
           stage.setScene(new Scene(root));
           stage.showAndWait();

           AddPrescriptionFormController controller = loader.getController();
           PrescriptionDetail newItem = controller.getResult();

           if(newItem != null) {
               prescriptionDetails.add(newItem);
           }

       } catch(Exception e){
           e.printStackTrace();
       }
    }

    @FXML
    void deleteBtn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa đơn thuốc này không?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            if(existingPrescription != null) {
                PrescriptionDAO.deletePrescription(existingPrescription);
                showAlert(Alert.AlertType.INFORMATION,"Thành công", "Đã xóa đơn thuốc!");

                closeWindow();
            }
        }
    }

    @FXML
    void cancelBtn(ActionEvent event) {
        Stage stage = (Stage)prescriptionTable.getScene().getWindow();
        stage.close();
    }

    @FXML
    void saveBtn(ActionEvent event) {
        // Lưu phần csdl của prescription
        String notes = generalNotesField.getText();

        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime consultationDate = medicalRecord.getConsultationDate();
        LocalDateTime prescriptionDate = consultationDate.withHour(now.getHour())
                .withMinute(now.getMinute())
                .withSecond(now.getSecond());


        Prescription prescription;
        if(existingPrescription != null) {
            // Cập nhật đơn thuốc cũ
            prescription = existingPrescription;
            prescription.setNotes(notes);
            prescription.setPrescriptionDate(prescriptionDate);
            PrescriptionDAO.updatePrescription(prescription);
        } else {
            // Thêm mới đơn thuốc
            prescription = new Prescription();
            prescription.setPatient(patient);
            prescription.setDoctor(LoginController.loggedInDoctor);
            prescription.setMedicalRecord(medicalRecord);
            prescription.setPrescriptionDate(prescriptionDate);
            prescription.setNotes(notes);
            PrescriptionDAO.savePrescription(prescription);
        }

        // Lấy danh sách hiện tại trong DB
        List<PrescriptionDetail> currentDetailsInDb = PrescriptionDetailDAO.getByPrescriptionId(prescription.getId());

        // Phân biệt dòng mới và dòng cũ
        for (PrescriptionDetail detail : prescriptionDetails) {
            detail.setPrescription(prescription);
            if (detail.getId() == null || detail.getId() == 0) {
                PrescriptionDetailDAO.savePrescriptionDetail(detail); // dòng mới
            } else {
                PrescriptionDetailDAO.updatePrescriptionDetail(detail); // dòng cũ
            }
        }

        // Xóa các dòng đã bị xóa trên giao diện (không còn trong prescriptionDetails)
        for (PrescriptionDetail oldDetail : currentDetailsInDb) {
            boolean stillExists = prescriptionDetails.stream()
                    .anyMatch(d -> d.getId() != null && d.getId().equals(oldDetail.getId()));
            if (!stillExists) {
                PrescriptionDetailDAO.deletePrescriptionDetail(oldDetail);
            }
        }

        // Cập nhật giao diện (hiển thị ngày giờ đẹp)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH'h':mm'm':ss's'");
        prescriptionDateField.setText(prescriptionDate.format(formatter));

        prescriptionDetails.clear();
        prescriptionDetails.addAll(PrescriptionDetailDAO.getByPrescriptionId(prescription.getId()));

        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đơn thuốc đã được lưu thành công!");
        closeWindow();
    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            final Button editButton = new Button("✎"); // biểu tượng chỉnh sửa
            final Button deleteButton = new Button("🗑"); // biểu tượng xóa
            final HBox hbox = new HBox(10, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-size: 18px;");
                deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 18px;");

                deleteButton.setOnAction(event -> {
                    PrescriptionDetail item = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa");
                    alert.setHeaderText("Bạn có chắc muốn xóa loại thuốc này?");

                    ButtonType buttonYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
                    ButtonType buttonNo = new ButtonType("Không", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(buttonYes, buttonNo);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == buttonYes) {
                            prescriptionDetails.remove(item);
                        }
                    });
                });

                editButton.setOnAction(event -> {
                    PrescriptionDetail selectedItem = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/UpdatePrescriptionForm.fxml"));
                        Parent root = loader.load();

                        // Lấy controller và truyền dữ liệu
                        UpdatePrescriptionFormController controller = loader.getController();
                        controller.setData(selectedItem); // truyền dữ liệu để hiển thị

                        Stage stage = new Stage();
                        stage.setTitle("Cập nhật loại thuốc");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        // Sau khi cập nhật xong, lấy lại dữ liệu
                        PrescriptionDetail updated = controller.getResult();
                        if(updated != null) {
                            selectedItem.setMedicineName(updated.getMedicineName());
                            selectedItem.setQuantity(updated.getQuantity());
                            selectedItem.setUnit(updated.getUnit());
                            selectedItem.setDosage(updated.getDosage());
                            selectedItem.setUsageInstructions(updated.getUsageInstructions());
                            selectedItem.setNotes(updated.getNotes());

                            // Cập nhật lại bảng
                            prescriptionTable.refresh();
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }

    private void showDetailDialog(String title, String content) {
        Stage dialog = new Stage();
        dialog.setTitle(title);

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefSize(400, 200);

        Button closeButton = new Button("Đóng");
        closeButton.setOnAction(e -> dialog.close());

        VBox vbox = new VBox(10, textArea, closeButton);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.initOwner(prescriptionTable.getScene().getWindow());
        dialog.show();
    }

    // Hàm giúp hiển thị nội dung khi nội dung vuot quá chieu dài của cột
    private void setupEllipsisColumn(TableColumn<PrescriptionDetail, String> column, String dialogTitle) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    setOnMouseClicked(null);
                    setStyle(""); // Reset lại style nếu ô rỗng
                } else {
                    // Đo chiều rộng văn bản thực tế
                    Text text = new Text(item);
                    text.setFont(getFont());
                    double textWidth = text.getLayoutBounds().getWidth();

                    //Chiều rộng cột - padding (khoảng 10px mặc định)
                    double cellWidth = column.getWidth() - 10;

                    // Nếu quá rộng, cắt dần và thêm "..."
                    if(textWidth > cellWidth) {
                        String shortened = item;
                        text.setText(shortened);
                        while(text.getLayoutBounds().getWidth() > cellWidth - 10 && shortened.length() > 0) {
                            shortened = shortened.substring(0, shortened.length() - 1);
                            text.setText(shortened + "...");
                        }
                        setText(shortened + "...");
                        setTooltip(new Tooltip("Bấm để xem chi tiết"));
                        setStyle("-fx-cursor: hand;");
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1 && !isEmpty()) {
                                showDetailDialog(dialogTitle, item);
                            }
                        });
                    } else {
                        // Nếu ngắn, hiển thị bình thường, không cần tooltip hay click
                        setText(item);
                        setTooltip(null);
                        setOnMouseClicked(null);
                        setStyle("");
                    }
                }
            }
        });
    }

    private void closeWindow(){
        Stage stage = (Stage) prescriptionTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            hideNode(addButton);
            hideNode(saveButton);
            hideNode(cancelButton);
            hideNode(deleteButton);

            // Ẩn sạch sẽ cột hành động
            actionColumn.setVisible(false);
            // Xóa ko gian của cột
            actionColumn.setPrefWidth(0);
            // Cập nhật lại độ rộng các cột còn lại
            prescriptionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    private void hideNode(Node node) {
        if (node != null) {
            node.setVisible(false);    // không hiển thị
            node.setManaged(false);    // không chiếm diện tích
        }
    }
}
