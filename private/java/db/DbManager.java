package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utilities.LogKeeper;


public class DbManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/representatives";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConn() {
        Connection conn = null;
        LogKeeper logKeeper = LogKeeper.getInstance();
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        }

        return conn;
    }
}
