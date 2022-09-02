package AutomatedTimeTableScheduler.Database;

import java.sql.*;

public class DatabaseCon {

    private static final String URL = "jdbc:mysql://localhost:3306/school_app";
    private static final String USERNAME = "timetable_user";
    private static final String PASSWORD = "timetable_pass";
    private Connection db;

    public DatabaseCon() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Loading SQL Connector Driver
        db = DriverManager.getConnection(URL, USERNAME, PASSWORD); //Creating Connection with database
    }

    public void closeConnection() {
        try {
            db.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
