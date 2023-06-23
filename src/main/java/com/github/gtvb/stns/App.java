package com.github.gtvb.stns;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import com.github.gtvb.stns.application.gui.notesList.NotesListGui;
import com.github.gtvb.stns.domain.model.Profile;
import com.github.gtvb.stns.domain.model.Tag;
import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.db.Db;
import com.github.gtvb.stns.infra.repository.NoteHasTagRepository;
import com.github.gtvb.stns.infra.repository.NoteRepository;
import com.github.gtvb.stns.infra.repository.ProfileRepository;
import com.github.gtvb.stns.infra.repository.TagRepository;
import com.github.gtvb.stns.infra.repository.UserRepository;
import com.github.gtvb.stns.utils.LoginUtils;
import com.github.gtvb.stns.utils.PasswordHasher;

public class App {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.printf("stns - Simple Terminal Note System\n\nUSAGE: stns [COMMAND]\n\nAvailable Commands\n"
                             +"\tnew-user: Creates a new user and logs him/her in\n"
                             +"\tlogin: Attempts to log in a given user with a username and password\n"
                             +"\tlogout: Logs the user out\n"
                             +"\tprofile: If logged in, shows the current user's data\n"
                             +"\tnew-note: Creates a new note\n"
                             +"\tnotes: Opens an interactive GUI that allows the user to visualize, remove and edit notes\n");
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

                int numItems = tags.size();
                int maxItemsPerRow = 4;
                int numRows = (int) Math.ceil((double) numItems / maxItemsPerRow);

                int count = 0;
                for (int row = 0; row < numRows; row++) {
                    for (int col = 0; col < maxItemsPerRow && count < tags.size(); col++) {
                        System.out.printf("%-3d %s\t", count + 1, tags.get(count).getName());
                        count++;
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

                String createdNoteId = noteRepository.createNote(title, contents, user.getUuid());
                noteHasTagRepository.createNoteHasTagRelationship(createdNoteId, pickedTags);

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

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                String createdAt = now.format(format);

                User newUser = userRepository.createUser(username, PasswordHasher.hashPassword(password), createdAt);
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

                User user = userRepository.getUserByUsername(username);
                if(user != null) {
                    if(!PasswordHasher.checkIfPasswordMatches(password, user.getUserPassword())) {
                        System.out.println("Incorrect password!");
                        break;
                    }
                    LoginUtils.login(user);
                    System.out.println("Successfully logged in");
                } else {
                    System.out.println("User dos not exist. Create a new one!");
                }

                break;
            }
            case "profile": {
                User loggedUser = LoginUtils.getLoggedUser();
                if(loggedUser == null) {
                    System.out.println("No user logged in...");
                    break;
                }
                ProfileRepository profileRepository = new ProfileRepository(conn);
                Profile profileData = profileRepository.getProfileByUserId(loggedUser.getUuid());

                System.out.printf("Your data\nFull name: %s\nUsername: %s\nCreated at: %s\n",
                                    profileData.getFullName(), loggedUser.getUsername(), loggedUser.getCreatedAt());
                break;
            }
            case "logout": {
                if(LoginUtils.getLoggedUser() != null) {
                    LoginUtils.logout();
                }
                break;
            }
            default:
                System.out.println("This command does not exist");
                break;
        }

        sc.close();
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
