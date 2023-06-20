package com.github.gtvb.stns.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.github.gtvb.stns.domain.model.User;

public class LoginUtils {
    public static boolean checkIfUserIsLogged(String username, String password) {
        Path path = Paths.get("login.txt");
        try {
            List<String> userData = Files.readAllLines(path);
            if(username.equals(userData.get(1)) && password.equals(userData.get(0)))  {
                return true;
            }

            return false;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(User user) {
        try {
            Path path = Paths.get("login.txt");
            Files.createFile(path);

            Files.write(path, (user.getUuid() + "\n").getBytes());
            Files.write(path, (user.getUsername() + "\n").getBytes());
            Files.write(path, (user.getUserPassword() + "\n").getBytes());
            Files.write(path, (user.getCreatedAt() + "\n").getBytes());
                 
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void logout() {
        Path path = Paths.get("login.txt");
        try {
            Files.deleteIfExists(path);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
