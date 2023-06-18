package com.github.gtvb.stns.infra.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import com.github.gtvb.stns.domain.model.Note;

public class NoteRepository {
    private Connection dbConnection;

    public NoteRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void createNote(String title, String contents, String userId) {
        String query = "INSERT INTO Note (id, user_id, title, contents) VALUES (?, ?, ?, ?);";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, userId);
            stmt.setString(3, title);
            stmt.setString(4, contents);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public ArrayList<Note> getNotesByUserId(String userId) {
        ArrayList<Note> notes = new ArrayList<>();

        String query = "SELECT id, title, contents, created_at"
                      +"FROM Note"
                      +"WHERE Note.user_id = ?";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String title = results.getString("title");
                String contents = results.getString("contents");
                String createdAt = results.getString("created_at");

                notes.add(new Note(id, userId, title, contents, createdAt));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return notes;
    };

    public void deleteNoteById(String noteId) {
        String query = "DELETE FROM Note WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, noteId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public void editNoteTitle(String noteId, String newTitle) throws SQLException {
        String query = "UPDATE Note SET title = ? WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, newTitle);
            stmt.setString(2, noteId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public void editNoteContents(String noteId, String newContents) throws SQLException {
        String query = "UPDATE Note SET contents = ? WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, newContents);
            stmt.setString(2, noteId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };
}
