package org.example.quanlyhosobenhan.Model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_doctor")
public class PatientDoctor {
    @EmbeddedId
    private PatientDoctorId id = new PatientDoctorId(); // Khóa chính kết hợp

    @ManyToOne
    @MapsId("patientId") // Liên kết với cột patient_id trong bảng patient_doctor
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @MapsId("doctorId") // Liên kết với cột doctor_id trong bảng patient_doctor
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
