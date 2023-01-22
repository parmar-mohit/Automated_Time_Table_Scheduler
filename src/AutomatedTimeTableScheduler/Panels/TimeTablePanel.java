package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;
import AutomatedTimeTableScheduler.Static.CreatePDF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;

public class TimeTablePanel extends JPanel implements ActionListener {


    private boolean status;
    private JLabel timeLabel,classLabel,courseLabel,teacherLabel,classroomLabel,statusLabel;
    private JButton generateTimeTableButton;
    private DatabaseCon db;

    public  TimeTablePanel(){
        //Initialising Member
        timeLabel = new JLabel();
        classLabel = new JLabel();
        courseLabel = new JLabel();
        teacherLabel = new JLabel();
        classroomLabel = new JLabel();
        statusLabel = new JLabel();
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
            }else{
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
            }else if( (db.getTotalWeeklyLoad() / db.getTeacherCount() ) >= 18 ){
                teacherLabel.setText("There are not enough teachers to handle workload. Teacher workload may exceed 18 hours/week");
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
                revalidate();
                repaint();

                //Creating TimeTable Directory
                File file = new File("TimeTable");
                file.mkdir();

                //Creating WorkLoad Sheet
                CreatePDF.createWorkLoadSheet();

                //Creating Master Time Table
                CreatePDF.createMasterTimeTable();

                //Creating Class Time Table
                file = new File("TimeTable/Class");
                file.mkdir();
                CreatePDF.createClassTimeTable();

                //Creating Classroom Time Table
                file = new File("TimeTable/Classroom");
                file.mkdir();
                CreatePDF.createClassroomTimeTable();

                //Creating Teacher Time Table
                file = new File("TimeTable/Teacher");
                file.mkdir();
                CreatePDF.createTeacherTimeTable();

                statusLabel.setText("Time Table Generated");
                revalidate();
                repaint();
            }catch(Exception excp){
                System.out.println(excp);
            }
        }
    }
}
