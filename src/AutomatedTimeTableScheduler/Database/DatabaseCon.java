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

    public void insertTimeSlots(ArrayList<Time> startTimeList,ArrayList<Time> endTimeList) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM time_slots;");
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("INSERT INTO time_slots(start_time,end_time,day) VALUES(?,?,?);");
        for( int i = 0; i < startTimeList.size(); i++ ) {
            for (int j = 0; j < Constant.WEEK.length; j++) {
                String day = Constant.WEEK[j];

                preparedStatement.setTime(1, startTimeList.get(i));
                preparedStatement.setTime(2,endTimeList.get(i));
                preparedStatement.setString(3,day);
                preparedStatement.executeUpdate();
            }
        }
    }

    public ResultSet getTimeSlots() throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT DISTINCT start_time,end_time FROM time_slots;");
        return preparedStatement.executeQuery();
    }

    public int getTimeSlotCount() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(DISTINCT start_time,end_time) FROM time_slots;");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
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
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM class");
        return preparedStatement.executeQuery();
    }
}
