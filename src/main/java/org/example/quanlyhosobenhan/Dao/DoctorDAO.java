package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Controllers.PasswordEncoder;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class DoctorDAO {
    public Doctor login(String userName, String plainPassword){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "from Doctor where userName=:userName";
            Query<Doctor> query = session.createQuery(hql, Doctor.class);
            query.setParameter("userName", userName);

            Doctor doctor = query.uniqueResult();

            if(doctor != null) {
                // So sánh password nhập vào voi password đã mã hóa trong database
                if(PasswordEncoder.checkPassword(plainPassword, doctor.getPassword())){
                    return doctor;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null; // Sai email hoac password
    }

    public boolean register(Doctor doctor){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();

            // Hash password trước khi lưu vào database
            String hashedPassword = PasswordEncoder.hashPassword(doctor.getPassword());
            doctor.setPassword(hashedPassword);

            session.save(doctor);
            session.getTransaction().commit();

            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String userName, String newPassword){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();

            String hql = "from Doctor where userName = :userName";
            Query<Doctor> query = session.createQuery(hql, Doctor.class);
            query.setParameter("userName", userName);
            Doctor doctor = query.uniqueResult();

            if(doctor != null){
                // Ma hoa mat khau moi
                String hashedPassword = PasswordEncoder.hashPassword(newPassword);
                doctor.setPassword(hashedPassword);
                session.update(doctor);
                session.getTransaction().commit();
                return true;
            } else {
                return false; // Ko tìm thấy user
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra xem username có tồn tại không
    public boolean existByUserName(String userName){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<Doctor> query = session.createQuery("from Doctor where userName = :userName", Doctor.class);
            query.setParameter("userName", userName);
            return query.uniqueResult() != null;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
