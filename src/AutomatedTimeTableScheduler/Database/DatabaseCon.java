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
}
