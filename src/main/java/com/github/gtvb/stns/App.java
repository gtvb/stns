package com.github.gtvb.stns;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.github.gtvb.stns.application.gui.notesList.NotesListGui;
import com.github.gtvb.stns.domain.model.Tag;
import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.db.Db;
import com.github.gtvb.stns.infra.repository.NoteHasTagRepository;
import com.github.gtvb.stns.infra.repository.NoteRepository;
import com.github.gtvb.stns.infra.repository.ProfileRepository;
import com.github.gtvb.stns.infra.repository.TagRepository;
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
            case "notes": {
                User user = LoginUtils.getLoggedUser();
                if(user == null) {
                    System.out.println("You need to login first!");
                    break;
                }

                NotesListGui gui = new NotesListGui(conn);
                gui.init();

                break;
            }
            case "new-note": {
                User user = LoginUtils.getLoggedUser();
                if(user == null) {
                    System.out.println("You need to login first!");
                    break;
                }

                TagRepository tagRepository = new TagRepository(conn);
                NoteRepository noteRepository = new NoteRepository(conn);
                NoteHasTagRepository noteHasTagRepository = new NoteHasTagRepository(conn);

                System.out.printf("Title: ");
                String title = sc.nextLine();

                System.out.printf("Initial contents: ");
                String contents = sc.nextLine();

                ArrayList<Tag> tags = tagRepository.getAllTags();
                System.out.println(tags);

                int numItems = tags.size();
                int maxItemsPerRow = 4;
                int numRows = (int) Math.ceil((double) numItems / maxItemsPerRow);

                for (int row = 0; row < numRows; row++) {
                    for (int col = 0; col < maxItemsPerRow; col++) {
                        System.out.printf("%d) %s\t", col + 1, tags.get(col).getName());
                    }
                    System.out.println();
                }

                ArrayList<Tag> pickedTags = new ArrayList<>();

                System.out.println("Enter the indices of the tags you want to pick (separated by spaces): ");
                String input = sc.nextLine();
                String[] indices = input.split(" ");

                for (String index : indices) {
                    int itemIndex = Integer.parseInt(index);
                    if (itemIndex >= 1 && itemIndex <= numItems) {
                        pickedTags.add(tags.get(itemIndex - 1));
                    }
                }

                // String createdNoteId = noteRepository.createNote(title, contents, user.getUuid());
                // noteHasTagRepository.createNoteHasTagRelationship(createdNoteId, pickedTags);

                System.out.println("Note created!");
                break;
            }
            case "new-user": {
                UserRepository userRepository = new UserRepository(conn);
                ProfileRepository profileRepository = new ProfileRepository(conn);

                System.out.printf("Your username: ");
                String username = sc.nextLine();

                System.out.printf("Your password: ");
                String password = sc.nextLine();

                System.out.printf("Your full name: ");
                String fullName = sc.nextLine();

                User newUser = userRepository.createUser(username, password);
                profileRepository.createProfile(newUser.getUuid(), fullName);

                LoginUtils.login(newUser);

                break;
            }
            case "login": {
                UserRepository userRepository = new UserRepository(conn);

                System.out.printf("Your username: ");
                String username = sc.nextLine();

                System.out.printf("Your password: ");
                String password = sc.nextLine();

                User user = userRepository.getUserByNameAndPassword(username, password);
                if(user != null) {
                    LoginUtils.login(user);
                    System.out.println("Successfully logged in");
                } else {
                    System.out.println("User dos not exist. Create a new one!");
                }
                break;
            }
            case "logout": {
                LoginUtils.logout();
                break;
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
