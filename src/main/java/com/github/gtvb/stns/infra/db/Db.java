package com.github.gtvb.stns.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class Db {
    public static Connection connectToDatabase() {
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("MYSQL_DB_URL");
        String dbUser = dotenv.get("MYSQL_DB_USER");
        String dbPassword = dotenv.get("MYSQL_DB_PASSWORD");
        String dbName = dotenv.get("MYSQL_DB_NAME");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            createDatabaseIfNotExists(connection, dbName);
            connection.setCatalog(dbName);
            
            populateDatabase(connection);

            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void createDatabaseIfNotExists(Connection connection, String dbName) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void populateDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS User (" +
                    "id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "user_password TEXT NOT NULL UNIQUE," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Profile (" +
                    "id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                    "user_id VARCHAR(36)," +
                    "full_name VARCHAR(50) NOT NULL," +
                    "FOREIGN KEY (user_id)" +
                    "    REFERENCES User(id)" +
                    "    ON UPDATE CASCADE" +
                    "    ON DELETE CASCADE" +
                    ")");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Tag (" +
                    "id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                    "tag_name VARCHAR(20) NOT NULL UNIQUE," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Note (" +
                    "id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                    "user_id VARCHAR(36)," +
                    "title VARCHAR(50) NOT NULL," +
                    "contents TEXT," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id)" +
                    "    REFERENCES User(id)" +
                    "    ON UPDATE CASCADE" +
                    "    ON DELETE SET NULL" +
                    ")");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Note_has_Tag (" +
                    "note_id VARCHAR(36)," +
                    "tag_id VARCHAR(36)," +
                    "FOREIGN KEY (note_id)" +
                    "    REFERENCES Note(id)" +
                    "    ON UPDATE CASCADE" +
                    "    ON DELETE SET NULL," +
                    "FOREIGN KEY (tag_id)" +
                    "    REFERENCES Tag(id)" +
                    "    ON UPDATE CASCADE" +
                    "    ON DELETE SET NULL" +
                    ")");

            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM Tag");
            resultSet.next();
            int tagCount = resultSet.getInt(1);

            if (tagCount == 0) {
                statement.executeUpdate("INSERT INTO Tag (id, tag_name) VALUES " +
                        "(uuid(), 'Work')," +
                        "(uuid(), 'Personal')," +
                        "(uuid(), 'Important')," +
                        "(uuid(), 'Health')," +
                        "(uuid(), 'Finance')," +
                        "(uuid(), 'Study')," +
                        "(uuid(), 'Family')," +
                        "(uuid(), 'Travel')," +
                        "(uuid(), 'Hobby')," +
                        "(uuid(), 'Fitness')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
