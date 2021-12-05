package com.cf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    static final String URL = "jdbc:mysql://localhost:3306/cf-pos";
    static final String USR = "root";
    static final String PWD = "root";

    Connection conn = null;
    Statement statement = null;


    public void initDB() throws SQLException {
        try {
            conn = DriverManager.getConnection(URL, USR, PWD);
            statement = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Error: DB connection failed");
            e.printStackTrace();
        }
    }

    public void closeDB() throws SQLException {
        statement.close();
        conn.close();
    }
}
