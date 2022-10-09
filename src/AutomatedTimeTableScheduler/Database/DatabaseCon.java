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

    public ArrayList<Time> getTimeInfo() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM time_info;");
        ResultSet resultSet = preparedStatement.executeQuery();
        if( resultSet.next() ) {
            ArrayList<Time> timeInfoList = new ArrayList<>();
            timeInfoList.add(resultSet.getTime("college_start_time"));
            timeInfoList.add(resultSet.getTime("college_end_time"));
            timeInfoList.add(resultSet.getTime("break_start_time"));
            timeInfoList.add(resultSet.getTime("break_end_time"));
            return timeInfoList;
        }else {
            return null;
        }
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

    public boolean checkCourseExist(String courseCode) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT EXISTS(SELECT * FROM course WHERE course_code = ?);");
        preparedStatement.setString(1,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getBoolean(1);
    }

    public void addCourse(String courseCode,String courseName,int sessionDuration,int sessionPerWeek) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO course VALUES(?,?,?,?);");
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseName);
        preparedStatement.setInt(3,sessionPerWeek);
        preparedStatement.setInt(4,sessionDuration);
        preparedStatement.executeUpdate();
    }

    public ResultSet getCourseList() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course;");
        return preparedStatement.executeQuery();
    }

    public void deleteCourse(String courseCode) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM course WHERE course_code = ?;");
        preparedStatement.setString(1,courseCode);
        preparedStatement.executeUpdate();
    }

    public void addTeacher(String firstname,String lastname) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO teacher(firstname,lastname) VALUES(?,?);");
        preparedStatement.setString(1,firstname);
        preparedStatement.setString(2,lastname);
        preparedStatement.executeUpdate();
    }

    public ResultSet getTeacherList() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM teacher;");
        return preparedStatement.executeQuery();
    }

    public void deleteTeacher(int teacherId) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM teacher WHERE teacher_id = ?;");
        preparedStatement.setInt(1,teacherId);
        preparedStatement.executeUpdate();
    }

    public void addClassroom(String roomName) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO room(room_name) VALUES(?);");
        preparedStatement.setString(1,roomName);
        preparedStatement.executeUpdate();
    }

    public ResultSet getClassroomList() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM room");
        return preparedStatement.executeQuery();
    }

    public void deleteClassroom(int roomId) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM room WHERE room_id = ?;");
        preparedStatement.setInt(1,roomId);
        preparedStatement.executeUpdate();
    }

    public int getCourseCount() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM course;");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getTeacherCount() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM teacher;");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getClassroomCount() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM room;");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }
}
