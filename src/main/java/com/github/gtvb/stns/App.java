package com.github.gtvb.stns;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.github.gtvb.stns.application.gui.notesList.NotesListGui;
import com.github.gtvb.stns.domain.model.Note;
import com.github.gtvb.stns.domain.model.Profile;
import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.db.Db;
import com.github.gtvb.stns.infra.repository.ProfileRepository;
import com.github.gtvb.stns.infra.repository.UserRepository;
import com.github.gtvb.stns.utils.LoginUtils;

public class App {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Help here");
            return;
        }

        Connection conn = Db.connectToDatabase();
        Scanner sc = new Scanner(System.in);
        switch(args[0]) {
            case "notes":
                ArrayList<Note> notes = new ArrayList<>();
                // notes.add(new Note("123", "456", "Title Cool", "Something really really cool", "30/06"));
                NotesListGui gui = new NotesListGui(notes);
                gui.init();
                break;
            case "new-note": {
                break;
            }
            case "new-user": {
                UserRepository ur = new UserRepository(conn);
                ProfileRepository pr = new ProfileRepository(conn);

                System.out.printf("Your username: ");
                String username = sc.next();

                System.out.printf("Your password: ");
                String password = sc.next();

                System.out.printf("Your full name: ");
                String fullName = sc.next();

                User newUser = ur.createUser(username, password);
                pr.createProfile(newUser.getUuid(), fullName);

                LoginUtils.login(newUser);

                break;
            }
            case "login": {
                UserRepository ur = new UserRepository(conn);

                System.out.printf("Your username: ");
                String username = sc.next();

                System.out.printf("Your password: ");
                String password = sc.next();

                User user = ur.getUserByNameAndPassword(username, password);
                if(user != null) {
                    LoginUtils.login(user);
                }
            }
        }

        sc.close();
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
