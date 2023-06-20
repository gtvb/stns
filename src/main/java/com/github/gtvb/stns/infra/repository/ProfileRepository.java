package com.github.gtvb.stns.infra.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.github.gtvb.stns.domain.model.Profile;

public class ProfileRepository {
    private Connection dbConnection;

    public ProfileRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void createProfile(String userId, String fullName) {
        String query = "INSERT INTO Profile (id, user_id, full_name) VALUES (?, ?, ?);";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, userId);
            stmt.setString(3, fullName);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public Profile getProfileByUserId(String userId) {
        Profile profile = null;

        String query = "SELECT id, full_name FROM Profile WHERE user_id = ?";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String fullName = results.getString("full_name");

                profile = new Profile(id, userId, fullName);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return profile;
    };

    public void editProfileFullName(String profileId, String newFullName) {
        String query = "UPDATE Profile SET full_name = ? WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, newFullName);
            stmt.setString(2, profileId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };
}
