package com.github.gtvb.stns.infra.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import com.github.gtvb.stns.domain.model.Tag;

public class TagRepository {
    private static int maxTagsForSelectStatement = 15;
    private Connection dbConnection;

    public TagRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void createTag(String name) {
        String query = "INSERT INTO Tag (id, tag_name) VALUES (?, ?);";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, name);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public ArrayList<Tag> getAllTags() {
        ArrayList<Tag> tags = new ArrayList<>();

        String query = "SELECT id, tag_name, created_at FROM Tag;";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String tagName = results.getString("tag_name");
                String createdAt = results.getString("created_at");

                tags.add(new Tag(id, tagName, createdAt));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return tags;
    };

    public ArrayList<Tag> getTagsInsideNote(String noteUuid) {
        ArrayList<Tag> tags = new ArrayList<>();

        String query = "SELECT id, tag_name, created_at FROM Tag;";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String tagName = results.getString("tag_name");
                String createdAt = results.getString("created_at");

                tags.add(new Tag(id, tagName, createdAt));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return tags;
    };

    public void deleteTagById(String tagId) {
        String query = "DELETE FROM Tag WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, tagId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };
}
