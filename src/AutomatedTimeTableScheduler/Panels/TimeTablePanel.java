package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CreatePDF.*;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;

public class TimeTablePanel extends JPanel implements ActionListener {


    private boolean status;
    private final JLabel timeLabel;
    private final JLabel classLabel;
    private final JLabel courseLabel;
    private final JLabel teacherLabel;
    private final JLabel classroomLabel;
    private final JLabel statusLabel;
    private final JButton generateTimeTableButton;
    private DatabaseCon db;

    public  TimeTablePanel(){
        //Initialising Member
        timeLabel = new JLabel();
        classLabel = new JLabel();
        courseLabel = new JLabel();
        teacherLabel = new JLabel();
        classroomLabel = new JLabel();
        statusLabel = new JLabel("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        statusLabel.setText("");
        generateTimeTableButton = new JButton("Generate Time Table");

        //Checking Status if Time Table can be built
        checkStatus();

        //Editing Members
        timeLabel.setFont(new Font("Times New Roman",Font.BOLD,18));
        classLabel.setFont(new Font("Times New Roman",Font.BOLD,18));
        courseLabel.setFont(new Font("Times New Roman",Font.BOLD,18));
        teacherLabel.setFont(new Font("Times New Roman",Font.BOLD,18));
        classroomLabel.setFont(new Font("Times New Roman",Font.BOLD,18));
        statusLabel.setFont(new Font("Times New Roman",Font.BOLD,24));
        generateTimeTableButton.setPreferredSize(Constant.BUTTON_SIZE);
        generateTimeTableButton.setBackground(Constant.BUTTON_BACKGROUND);

        //Adding Listeners
        generateTimeTableButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(timeLabel,Constraint.setPosition(0,0));
        add(courseLabel,Constraint.setPosition(0,1));
        add(classLabel,Constraint.setPosition(0,2));
        add(teacherLabel,Constraint.setPosition(0,3));
        add(classroomLabel,Constraint.setPosition(0,4));
        add(statusLabel, Constraint.setPosition(0,5));
        add(generateTimeTableButton,Constraint.setPosition(0,6));
    }

    private void checkStatus(){
        status = true;

        try{
            db = new DatabaseCon();

            //Checking for Time
            if( db.getTimeInfo() != null ){
                timeLabel.setText("Time : All Ok");
            }else{
                timeLabel.setText("Time : Time Values not Entered");
                status = false;
            }

            //Checking for Class
            if( db.getClassCount() >= 2  ){
                classLabel.setText("Class : All Ok");
            }else {
                ResultSet classResultSet = db.getClassList();
                while( classResultSet.next() ){
                    if( db.getCourseCountForClass(classResultSet.getInt("class_id")) < 5 ){
                        classLabel.setText("Less than Five courses are assigned for Class "+classResultSet.getInt("year")+ " "+classResultSet.getString("division"));
                        status = false;
                    }
                }
                classLabel.setText("Class : There should be minimum 2 Classes");
                status = false;
            }

            //Checking for course
            if( db.getCourseCount() >= 2 ){
                courseLabel.setText("Course : All Ok");
            }else{
                courseLabel.setText("Course : There should be Minimum 2 Courses");
                status = false;
            }

            //Checking for teacher
            if( db.getTeacherCount() >= 2 ){
                teacherLabel.setText("Teacher : All Ok");
            }
            if( (db.getTotalWeeklyLoad() / db.getTeacherCount() ) >= 18 ){
                teacherLabel.setText("There are not enough teachers to handle workload. Teacher workload may exceed 18 hours/week");
                status = false;
            }
            if( db.getTeacherPreferenceValidation() != null ){
                teacherLabel.setText("Teacher Preference not Selected for Course Code : " +db.getTeacherPreferenceValidation());
                status = false;
            }

            //Checking for classroom
            if( db.getClassroomCount() >= 2 ){
                classroomLabel.setText("Classroom : All Ok");
            }else{
                classroomLabel.setText("Classroom : There should be Minimum 2 Teacher");
                status = false;
            }

            ResultSet courseList = db.getCourseList();
            while( courseList.next() ){
                int classroomCount = db.getClassroomCountForCourse(courseList.getString(1));
                if( classroomCount == 0 ){
                    classroomLabel.setText("No Classroom Assigned for Course Code : "+courseList.getString(1));
                    status = false;
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }finally {
            db.closeConnection();
        }

        if( status ) {
            statusLabel.setText("All Ok to Generate Time Table");
            statusLabel.setForeground(Constant.GREEN);
        }else{
            statusLabel.setText("Error Detected : Cannot Generate Time Table");
            statusLabel.setForeground(Constant.RED);
            generateTimeTableButton.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == generateTimeTableButton ){
            try {
                statusLabel.setText("Generating Time Table");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();

                //Creating TimeTable Directory
                File file = new File("TimeTable");
                file.mkdir();
                //Deleting All Files
                Constraint.deleteAllFileFromDirectory(file);

                //Creating WorkLoad Sheet
                statusLabel.setText("Creating Workload Sheet");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                Workload.createWorkLoadSheet();
                System.out.println("Workload Sheet Created");

                //Subject Allocation
                //Executing Python Program to allocate subjects
                statusLabel.setText("Allocating Courses to Professor");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                Process process = Runtime.getRuntime().exec("python Python/SubjectAllocation.py");
                process.waitFor();
                statusLabel.setText("Generating Subject Allocation Sheet");
                SubjectAllocation.createSubjectAllocationSheet();

                //Allocating Practicals
                statusLabel.setText("Allocating Practical");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                process = Runtime.getRuntime().exec("python Python/GeneticAlgorithm.py");
                process.waitFor();

                //Allocating Lecutes
                statusLabel.setText("Allocating Lectures");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                process = Runtime.getRuntime().exec("python Python/AllocateLectures.py");
                process.waitFor();

                //Creating Master Time Table
                statusLabel.setText("Generating Master Time Table");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                MasterTimeTable.createMasterTimeTable();
                System.out.println("Master Time Table Created");

                //Creating Class Time Table
                statusLabel.setText("Generating Class Time Table");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                file = new File("TimeTable/Class");
                file.mkdir();
                ClassTimeTable.createClassTimeTable();
                System.out.println("Class Time Table Created");

                //Creating Classroom Time Table
                statusLabel.setText("Generating Classroom Time Table");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                file = new File("TimeTable/Classroom");
                file.mkdir();
                ClassroomTimeTable.createClassroomTimeTable();
                System.out.println("Classroom Time Table Created");

                //Creating Teacher Time Table
                statusLabel.setText("Generating Classroom Time Table");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
                file = new File("TimeTable/Teacher");
                file.mkdir();
                TeacherTimeTable.createTeacherTimeTable();
                System.out.println("Teacher Time Table Created");

                statusLabel.setText("Time Table Generated");
                statusLabel.paintImmediately(statusLabel.getVisibleRect());
                revalidate();
                repaint();
            }catch(Exception excp){
                System.out.println(excp);
            }
        }
    }
}
