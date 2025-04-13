package org.example.quanlyhosobenhan.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    private String symptoms;

    private String diagnosis;

    private LocalDate consultationDate;

    private String treatmentMethod;

    private String notes;

    public MedicalRecord(Patient patient, Doctor doctor, String symptoms, String diagnosis,
                         LocalDate consultationDate, String treatmentMethod, String notes, Prescription prescription) {
        this.patient = patient;
        this.doctor = doctor;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.consultationDate = consultationDate;
        this.treatmentMethod = treatmentMethod;
        this.notes = notes;
        this.prescription = prescription;
    }
}
