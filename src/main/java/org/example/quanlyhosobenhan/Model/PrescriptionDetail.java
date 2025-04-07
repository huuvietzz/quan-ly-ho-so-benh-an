package org.example.quanlyhosobenhan.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prescription_details")
public class PrescriptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    private String medicineName;

    private String dosage;

    private String usageInstructions;

    private String notes;

    public PrescriptionDetail(String notes, String usageInstructions, String dosage, String medicineName, Prescription prescription) {
        this.notes = notes;
        this.usageInstructions = usageInstructions;
        this.dosage = dosage;
        this.medicineName = medicineName;
        this.prescription = prescription;
    }
}
