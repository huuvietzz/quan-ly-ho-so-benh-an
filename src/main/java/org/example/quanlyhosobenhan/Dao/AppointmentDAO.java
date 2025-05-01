package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDateTime;

public class AppointmentDAO {
    public Long countByDoctorId(int doctorId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long)session.createQuery("select count(m) from Appointment m where m.doctor.id = :doctorId")
                    .setParameter("doctorId", doctorId).uniqueResult();
        } catch(Exception e){
            e.printStackTrace();
            return 0L;
        }
    }
}

