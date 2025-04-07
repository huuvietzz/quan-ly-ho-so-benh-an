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

    private String symptoms;

    private String diagnosis;

    private LocalDate consultationDate;

    private String treamentMethod;

    private String notes;

    public MedicalRecord(Patient patient, Doctor doctor, String symptoms, String diagnosis,
                         LocalDate consultationDate, String treamentMethod, String notes) {
        this.patient = patient;
        this.doctor = doctor;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.consultationDate = consultationDate;
        this.treamentMethod = treamentMethod;
        this.notes = notes;
    }
}
