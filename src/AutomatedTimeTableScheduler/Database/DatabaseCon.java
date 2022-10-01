package AutomatedTimeTableScheduler.Database;

import AutomatedTimeTableScheduler.Static.Constant;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseCon {

    private static final String URL = "jdbc:mysql://localhost:3306/timetable";
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

    public void insertTimeInfo(Time startTime,Time endTime,Time breakStartTime, Time breakEndTime) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM time_info;");
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("INSERT INTO time_info VALUES(?,?,?,?);");
        preparedStatement.setTime(1,startTime);
        preparedStatement.setTime(2,endTime);
        preparedStatement.setTime(3,breakStartTime);
        preparedStatement.setTime(4,breakEndTime);
        preparedStatement.executeUpdate();
    }

    public boolean checkClassExist(int year,String divison) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT EXISTS(SELECT * FROM class WHERE year = ? AND division=?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,divison);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getBoolean(1);
    }

    public void createClass(int year, String division) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO class(year,division) VALUES(?,?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        preparedStatement.executeUpdate();
    }
    public ResultSet getClassList() throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM class ORDER BY year,division;");
        return preparedStatement.executeQuery();
    }

    public void deleteClass(int year,String division) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM class WHERE year = ? AND division =?;");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        preparedStatement.executeUpdate();
    }

    public int getClassCount() throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM class");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }
}
