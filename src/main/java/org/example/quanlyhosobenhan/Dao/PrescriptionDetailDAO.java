package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Model.PrescriptionDetail;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionDetailDAO {
    public static void savePrescriptionDetail(PrescriptionDetail prescriptionDetail) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(prescriptionDetail);
            transaction.commit();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static List<PrescriptionDetail> getByPrescriptionId(int prescriptionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from PrescriptionDetail where prescription.id = :id", PrescriptionDetail.class)
                    .setParameter("id", prescriptionId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void updatePrescriptionDetail(PrescriptionDetail prescriptionDetail) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(prescriptionDetail);
            transaction.commit();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deletePrescriptionDetail(PrescriptionDetail detail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(detail);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
