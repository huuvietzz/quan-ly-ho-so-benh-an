package org.example.quanlyhosobenhan.Controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordEncoder {

    // Mã hóa mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    // Kiểm tra mật khẩu
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword.toCharArray());
        return result.verified;
    }
}
