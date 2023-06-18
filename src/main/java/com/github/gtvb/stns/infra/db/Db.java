package com.github.gtvb.stns.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    public static Connection connectToDatabase() {
        Connection conn = null;

        try {
            // get data from env file and assemble connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" +
                                           "user=minty&password=greatsqldb");

            // populate tables

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
            
        return conn;
    }
}
