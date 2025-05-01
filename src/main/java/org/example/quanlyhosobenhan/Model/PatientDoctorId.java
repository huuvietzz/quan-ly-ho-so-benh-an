package org.example.quanlyhosobenhan.Model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PatientDoctorId implements Serializable {
    private Integer patientId;
    private Integer doctorId;
}
