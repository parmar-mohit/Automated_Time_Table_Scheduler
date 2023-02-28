package AutomatedTimeTableScheduler.Database;

import AutomatedTimeTableScheduler.Static.Constant;

import java.sql.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class DatabaseCon {

    private static final String URL = "jdbc:mysql://localhost:3306/timetable";
    private static final String USERNAME = "timetable_user";
    private static final String PASSWORD = "timetable_pass";
    private final Connection db;

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

        //Inserting Time Slots
        preparedStatement = db.prepareStatement("DELETE FROM time_slots;");
        preparedStatement.executeUpdate();

        int timeId = 1;

        for( int i = 0; i < Constant.WEEK.length; i++ ) {
            Time currentTime = startTime;
            while (currentTime.before(endTime)) {
                if (currentTime.equals(breakStartTime)) {
                    currentTime = breakEndTime;
                    timeId++;
                    continue;
                }

                preparedStatement = db.prepareStatement("INSERT INTO time_slots VALUES(?,?,?,?);");
                preparedStatement.setInt(1, timeId);
                preparedStatement.setTime(2, currentTime);
                preparedStatement.setTime(3, new Time(currentTime.getTime() + 3600000));
                preparedStatement.setString(4,Constant.WEEK[i]);
                preparedStatement.executeUpdate();

                //Increment Time by 1 hour
                currentTime = new Time(currentTime.getTime() + 3600000);  //3600000 ms = 1 hr
                timeId++;
            }
            timeId++;
        }
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

    public void createClass(int year, String division,ArrayList<String> courseCodeList) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO class(year,division) VALUES(?,?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int classId = resultSet.getInt(1);

        preparedStatement = db.prepareStatement("INSERT INTO class_course VALUES(?,?);");
        preparedStatement.setInt(1,classId);
        for( int i = 0; i < courseCodeList.size(); i++){
            preparedStatement.setString(2,courseCodeList.get(i));
            preparedStatement.executeUpdate();
        }
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
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course ORDER BY course_code;");
        return preparedStatement.executeQuery();
    }

    public void deleteCourse(String courseCode) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM course WHERE course_code = ?;");
        preparedStatement.setString(1,courseCode);
        preparedStatement.executeUpdate();
    }

    public void addTeacher(String firstname, String lastname, Dictionary<String,Integer> preferenceList) throws Exception {
        //Inserting Teacher Details
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO teacher(firstname,lastname) VALUES(?,?);");
        preparedStatement.setString(1,firstname);
        preparedStatement.setString(2,lastname);
        preparedStatement.executeUpdate();

        //Getting Teacher Id
        preparedStatement = db.prepareStatement("SELECT LAST_INSERT_ID();");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int teacherId = resultSet.getInt(1);

        //Inserting Preferences
        preparedStatement = db.prepareStatement("INSERT INTO course_teacher VALUES(?,?,?);");
        preparedStatement.setInt(2,teacherId);

        Enumeration<String> courseCodeList = preferenceList.keys();
        for( int i = 0; i < preferenceList.size(); i++){
            String courseCode = courseCodeList.nextElement();
            preparedStatement.setString(1,courseCode);
            preparedStatement.setInt(3,preferenceList.get(courseCode));
            preparedStatement.executeUpdate();
        }
    }

    public ResultSet getTeacherList() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM teacher ORDER BY firstname,lastname;");
        return preparedStatement.executeQuery();
    }

    public void deleteTeacher(int teacherId) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM teacher WHERE teacher_id = ?;");
        preparedStatement.setInt(1,teacherId);
        preparedStatement.executeUpdate();
    }

    public void addClassroom(String roomName,ArrayList<String> courseCodeList) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO room(room_name) VALUES(?);");
        preparedStatement.setString(1,roomName);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int roomId = resultSet.getInt(1);

        preparedStatement = db.prepareStatement("INSERT INTO course_rooms VALUES(?,?);");
        preparedStatement.setInt(2,roomId);
        for( int i = 0; i < courseCodeList.size(); i++){
            preparedStatement.setString(1,courseCodeList.get(i));
            preparedStatement.executeUpdate();
        }
    }

    public ResultSet getClassroomList() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM room ORDER BY room_name;");
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

    public int getTimeSlotCount() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM time_slots WHERE day = \"Monday\";");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getClassroomCountForCourse(String courseCode) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM course_rooms WHERE course_code = ?;");
        preparedStatement.setString(1,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public boolean roomNameExist(String roomName) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT EXISTS(SELECT * FROM room WHERE room_name = ?);");
        preparedStatement.setString(1,roomName);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getBoolean(1);
    }

    public int getTotalWeeklyLoad() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM class;");
        ResultSet classResultSet = preparedStatement.executeQuery();

        int load = 0;
        while( classResultSet.next() ){
            preparedStatement = db.prepareStatement("SELECT * FROM class_course WHERE class_id = ?;");
            preparedStatement.setInt(1,classResultSet.getInt("class_id"));
            ResultSet classCourseResultSet = preparedStatement.executeQuery();

            while( classCourseResultSet.next() ){
                preparedStatement = db.prepareStatement("SELECT * FROM course WHERE course_code = ?;");
                preparedStatement.setString(1,classCourseResultSet.getString("course_code"));
                ResultSet courseResultSet = preparedStatement.executeQuery();

                courseResultSet.next();
                if( courseResultSet.getInt("session_duration") == 1 ) {
                    load += (courseResultSet.getInt("session_duration") * courseResultSet.getInt("session_per_week"));
                }else{
                    load += (courseResultSet.getInt("session_duration") * courseResultSet.getInt("session_per_week")) * 4;  //Mutliplied by 4 for 4 batches
                }
            }
        }

        return load;
    }

    public ResultSet getDistinctYearList() throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT DISTINCT(year) FROM class;");
        return preparedStatement.executeQuery();
    }

    public ResultSet getDistinctLectureCourseListForYear(int year) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course WHERE course_code IN ( SELECT course_code FROM class_course WHERE class_id IN (SELECT class_id FROM class WHERE year = ?) ) AND session_duration = 1;");
        preparedStatement.setInt(1,year);
        return preparedStatement.executeQuery();
    }

    public ResultSet getDistinctPracticalCourseListForYear(int year) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course WHERE course_code IN ( SELECT course_code FROM class_course WHERE class_id IN (SELECT class_id FROM class WHERE year = ?) ) AND session_duration = 2;");
        preparedStatement.setInt(1,year);
        return preparedStatement.executeQuery();
    }

    public int getDistinctCourseCountForYear(int year) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(DISTINCT(course_code)) FROM class_course WHERE class_id IN (SELECT class_id FROM class WHERE year = ?);");
        preparedStatement.setInt(1,year);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getDivisionCountForCourseYear(String courseCode, int year) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM class WHERE year = ? AND class_id IN ( SELECT class_id FROM class_course WHERE course_code = ?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getCourseCountForClass(int classId) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(*) FROM class WHERE class_id = ?");
        preparedStatement.setInt(1,classId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public String getTeacherPreferenceValidation() throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course WHERE course_code NOT IN (SELECT DISTINCT(course_code) FROM course_teacher);");
        ResultSet resultSet = preparedStatement.executeQuery();
        if( resultSet.next() ){
            return resultSet.getString("course_code");
        }

        return null;
    }

    public void updateCourse(String courseCode,String courseName,int sessionDuration,int sessionPerWeek) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("UPDATE course SET course_name = ?, session_duration = ?, session_per_week = ? WHERE course_code = ?");
        preparedStatement.setString(1,courseName);
        preparedStatement.setInt(2,sessionDuration);
        preparedStatement.setInt(3,sessionPerWeek);
        preparedStatement.setString(4,courseCode);
        preparedStatement.executeUpdate();
    }

    public ArrayList<String> getCourseCodeListForClass(int year,String division) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT course_code FROM class_course WHERE class_id = (SELECT class_id FROM class WHERE year = ? and division = ?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        ResultSet resultSet =  preparedStatement.executeQuery();

        ArrayList<String> courseCodeList = new ArrayList<>();
        while( resultSet.next() ){
            courseCodeList.add(resultSet.getString(1));
        }

        return courseCodeList;
    }

    public void updateClass(int year,String division,ArrayList<String> courseCodeList) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("DELETE FROM class_course WHERE class_id = (SELECT class_id FROM class WHERE year = ? AND division = ?);");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("SELECT class_id FROM class WHERE year = ? AND division = ?;");
        preparedStatement.setInt(1,year);
        preparedStatement.setString(2,division);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int classId = resultSet.getInt(1);

        preparedStatement = db.prepareStatement("INSERT INTO class_course VALUES(?,?);");
        preparedStatement.setInt(1,classId);
        for( int i = 0; i < courseCodeList.size(); i++){
            preparedStatement.setString(2,courseCodeList.get(i));
            preparedStatement.executeUpdate();
        }
    }

    public Dictionary<String,Integer> getTeacherCoursePreferenceList(int teacherId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM course_teacher WHERE teacher_id = ?;");
        preparedStatement.setInt(1,teacherId);
        ResultSet resultSet = preparedStatement.executeQuery();


        Dictionary<String,Integer> preferenceList = new Hashtable<>();
        while( resultSet.next() ){
            preferenceList.put(resultSet.getString("course_code"),resultSet.getInt("preference"));
        }

        return preferenceList;
    }

    public void updateTeacher(int teacherId,String firstname,String lastname,Dictionary<String,Integer> preferenceList) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("UPDATE teacher SET firstname = ?,lastname = ? WHERE teacher_id = ?;");
        preparedStatement.setString(1,firstname);
        preparedStatement.setString(2,lastname);
        preparedStatement.setInt(3,teacherId);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("DELETE FROM course_teacher WHERE teacher_id = ?;");
        preparedStatement.setInt(1,teacherId);
        preparedStatement.executeUpdate();

        //Inserting Preferences
        preparedStatement = db.prepareStatement("INSERT INTO course_teacher VALUES(?,?,?);");
        preparedStatement.setInt(2,teacherId);

        Enumeration<String> courseCodeList = preferenceList.keys();
        for( int i = 0; i < preferenceList.size(); i++){
            String courseCode = courseCodeList.nextElement();
            preparedStatement.setString(1,courseCode);
            preparedStatement.setInt(3,preferenceList.get(courseCode));
            preparedStatement.executeUpdate();
        }
    }

    public ArrayList<String> getCourseCodeListForRoom(int roomId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT course_code FROM course_rooms WHERE room_id = ?;");
        preparedStatement.setInt(1,roomId);
        ResultSet resultSet =  preparedStatement.executeQuery();

        ArrayList<String> courseCodeList = new ArrayList<>();
        while( resultSet.next() ){
            courseCodeList.add(resultSet.getString(1));
        }

        return courseCodeList;
    }

    public void updateClassroom(int roomId,String roomName,ArrayList<String> courseCodeList) throws Exception {
        PreparedStatement preparedStatement = db.prepareStatement("UPDATE room SET room_name  = ? WHERE room_id = ?;");
        preparedStatement.setString(1,roomName);
        preparedStatement.setInt(2,roomId);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("DELETE FROM course_rooms WHERE room_id = ?;");
        preparedStatement.setInt(1,roomId);
        preparedStatement.executeUpdate();

        preparedStatement = db.prepareStatement("INSERT INTO course_rooms VALUES(?,?);");
        preparedStatement.setInt(2,roomId);
        for( int i = 0; i < courseCodeList.size(); i++){
            preparedStatement.setString(1,courseCodeList.get(i));
            preparedStatement.executeUpdate();
        }
    }

    public ResultSet getLectureSubjectAllocationForClass(int classId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement(" SELECT subject.course_code,course.course_name,teacher.firstname,teacher.lastname FROM subject JOIN course ON subject.course_code = course.course_code JOIN teacher on subject.teacher_id = teacher.teacher_id WHERE subject.class_id = ? AND course.session_duration=1 ORDER BY course_code;");
        preparedStatement.setInt(1,classId);
        return preparedStatement.executeQuery();
    }


    public ResultSet getPracticalSubjectAllocationForClass(int classId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement(" SELECT subject.course_code,course.course_name,batch,teacher.firstname,teacher.lastname FROM subject JOIN course ON subject.course_code = course.course_code JOIN teacher on subject.teacher_id = teacher.teacher_id WHERE subject.class_id = ? AND course.session_duration=2 ORDER BY course_code,batch;");
        preparedStatement.setInt(1,classId);
        return preparedStatement.executeQuery();
    }

    public ResultSet getTimeList() throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM time_slots WHERE day = \"Monday\" ORDER BY start_time,end_time;");
        return preparedStatement.executeQuery();
    }

    public ResultSet getLectureAllocationForTeacher(int teacherId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT subject.course_code,course.course_name,class.year,class.division,course.session_duration,course.session_per_week FROM subject JOIN course ON subject.course_code = course.course_code JOIN class ON subject.class_id = class.class_id WHERE teacher_id = ? AND course.session_duration = 1 ORDER BY subject.course_code,class.year,class.division;");
        preparedStatement.setInt(1,teacherId);
        return preparedStatement.executeQuery();
    }

    public ResultSet getPracticalAllocationForTeacher(int teacherId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT subject.course_code,batch,course.course_name,class.year,class.division,course.session_duration,course.session_per_week FROM subject JOIN course ON subject.course_code = course.course_code JOIN class ON subject.class_id = class.class_id WHERE teacher_id = ? AND course.session_duration = 2 ORDER BY subject.course_code,class.year,class.division;");
        preparedStatement.setInt(1,teacherId);
        return preparedStatement.executeQuery();
    }

    public int getTeacherLectureLoad(int teacherId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT course.session_duration,course.session_per_week FROM subject JOIN course ON subject.course_code = course.course_code WHERE teacher_id = ? AND course.session_duration = 1;");
        preparedStatement.setInt(1,teacherId);
        int load = 0;
        ResultSet lectureLoadResultSet = preparedStatement.executeQuery();
        while( lectureLoadResultSet.next() ){
            load += lectureLoadResultSet.getInt("session_duration") * lectureLoadResultSet.getInt("session_per_week");
        }

        return load;
    }

    public int getTeacherPracticalLoad(int teacherId) throws Exception{
        PreparedStatement preparedStatement = db.prepareStatement("SELECT course.session_duration,course.session_per_week FROM subject JOIN course ON subject.course_code = course.course_code WHERE teacher_id = ? AND course.session_duration = 2;");
        preparedStatement.setInt(1,teacherId);
        int load = 0;
        ResultSet practicalLoadResultSet = preparedStatement.executeQuery();
        while( practicalLoadResultSet.next() ){
            load += practicalLoadResultSet.getInt("session_duration") * practicalLoadResultSet.getInt("session_per_week");
        }

        return load;
    }
}