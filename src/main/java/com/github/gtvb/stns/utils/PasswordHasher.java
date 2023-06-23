package com.github.gtvb.stns.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordHasher {
    private static Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32,64,1,15*1024,2);

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public static boolean checkIfPasswordMatches(String password, String encodedPassword) {
        return encoder.matches(password, encodedPassword);
    }
}
