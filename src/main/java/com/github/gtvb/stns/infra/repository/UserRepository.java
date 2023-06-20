package com.github.gtvb.stns.infra.repository;

import java.net.PasswordAuthentication;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.github.gtvb.stns.domain.model.User;

public class UserRepository {
    private Connection dbConnection;

    public UserRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public User createUser(String username, String userPassword) {
        String query = "INSERT INTO User (id, username, user_password) VALUES (?, ?, ?, ?);";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            String userId = UUID.randomUUID().toString();
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, userPassword);
            stmt.executeQuery();

            return new User(userId, username, userPassword);
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    };

    public User getUserById(String userId) {
        User user = null;

        String query = "SELECT id, username, user_password, created_at FROM User WHERE id = ?";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String username = results.getString("username");
                String userPassword = results.getString("user_password");
                String createdAt = results.getString("created_at");

                user = new User(id, username, userPassword, createdAt);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return user;
    };

    public User getUserByNameAndPassword(String username, String password) {
        User user = null;

        String query = "SELECT id, username, user_password, created_at FROM User WHERE username = ? AND user_password = ?";

        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(1, password);
            ResultSet results = stmt.executeQuery();

            while(results.next()) {
                String id = results.getString("id");
                String name = results.getString("username");
                String userPassword = results.getString("user_password");
                String createdAt = results.getString("created_at");

                user = new User(id, name, userPassword, createdAt);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return user;
    };

    public void deleteUserById(String userId) {
        String query = "DELETE FROM User WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public void editUserUsername(String userId, String newUsername) throws SQLException {
        String query = "UPDATE User SET username = ? WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, userId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };

    public void editUserPassword(String userId, String newPassword) throws SQLException {
        String query = "UPDATE User SET user_password = ? WHERE id = ?";
        try(PreparedStatement stmt = this.dbConnection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, userId);
            stmt.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    };
}
