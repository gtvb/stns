package com.github.gtvb.stns.infra.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.github.gtvb.stns.domain.model.Tag;

public class NoteHasTagRepository {
   private Connection dbConnection;

    public NoteHasTagRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void createNoteHasTagRelationship(String noteId, ArrayList<Tag> tags) {
        String query = "INSERT INTO Note_has_Tag (note_id, tag_id) VALUES (?, ?);";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            for(Tag t : tags) {
                stmt.setString(1, noteId);
                stmt.setString(2, t.getUuid());
                stmt.executeUpdate();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }; 
}
