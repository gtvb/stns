package com.github.gtvb.stns.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.github.gtvb.stns.domain.model.User;

public class LoginUtils {
    public static User getLoggedUser() {
        try {
            Path path = Paths.get("login.txt");
            List<String> userData = Files.readAllLines(path);
            if(userData.size() == 0) {
                return null;
            }

            User user = new User();
            user.setUuid(userData.get(0));
            user.setUsername(userData.get(1));
            user.setUserPassword(userData.get(2));
            user.setCreatedAt(userData.get(3));

            return user;
        } catch(IOException e){
            System.out.println("Could not get logged user " + e.getMessage());
            return null;
        }
    }

    public static boolean login(User user) {
        try {
            Path path = Paths.get("login.txt");
            Files.createFile(path);

            String loginData = user.getUuid() + "\n" +
                        user.getUsername() + "\n" +
                        user.getUserPassword() + "\n" +
                        user.getCreatedAt() + "\n";

            Files.write(path, loginData.getBytes());
                 
            return true;
        } catch (IOException e) {
            System.out.println("Could not execute login " + e.getMessage());
            return false;
        }
    }

    public static void logout() {
        Path path = Paths.get("login.txt");
        try {
            Files.deleteIfExists(path);
        } catch(IOException e){
            System.out.println("Could not execute logout " + e.getMessage());
        }
    }
}
