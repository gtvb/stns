package com.github.gtvb.stns.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Db {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/<NAME>";
    private static final String DB_USER = "<USER>";
    private static final String DB_PASSWORD = "<PASSWORD>";

    public static Connection connectToDatabase() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if(!dbPopulated(conn)) {
                populateDb(conn);
                populateWithDefaultTags(conn);
            }

            return conn;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return null;
    }

     private static boolean dbPopulated(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'notes_app_db' " +
                "AND table_name IN ('User', 'Profile', 'Tag', 'Note', 'Note_has_Tag')";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 5;
            }
        }

        return false;
    }

    private static void populateDb(Connection connection) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS notes_app_db;" +
                "USE notes_app_db;" +
                "CREATE TABLE IF NOT EXISTS User (" +
                "    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                "    username VARCHAR(50) NOT NULL UNIQUE," +
                "    user_password VARCHAR(25) NOT NULL UNIQUE," +
                "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS Profile (" +
                "    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                "    user_id VARCHAR(36)," +
                "    full_name VARCHAR(50) NOT NULL," +
                "    FOREIGN KEY (user_id)" +
                "        REFERENCES User(id)" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE" +
                ");" +
                "CREATE TABLE IF NOT EXISTS Tag (" +
                "    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                "    tag_name VARCHAR(20) NOT NULL UNIQUE," +
                "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS Note (" +
                "    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                "    user_id VARCHAR(36)," +
                "    title VARCHAR(20) NOT NULL," +
                "    contents TEXT," +
                "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (user_id)" +
                "        REFERENCES User(id)" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE SET NULL" +
                ");" +
                "CREATE TABLE IF NOT EXISTS Note_has_Tag (" +
                "    note_id VARCHAR(36)," +
                "    tag_id VARCHAR(36)," +
                "    FOREIGN KEY (note_id)" +
                "        REFERENCES Note(id)" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE SET NULL," +
                "    FOREIGN KEY (tag_id)" +
                "        REFERENCES Tag(id)" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE SET NULL" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void populateWithDefaultTags(Connection connection) throws SQLException {
        String sql = "INSERT INTO Tag (id, tag_name) VALUES (?, ?)";
        String[] predefinedTags = {"tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10"};

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String tagName : predefinedTags) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, tagName);
                stmt.executeUpdate(); // Execute the statement for each tag
            }
        }

        System.out.println("Prepopulation complete.");
    }
}
