package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Panels.CoursePanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseCardPanel extends JPanel implements ActionListener {

    private JLabel courseCodeLabel,courseNameLabel,sessionDurationLabel,sessionPerWeekLabel;
    private JButton deleteCourseButton;
    private CoursePanel parentPanel;
    private String courseCode;
    private DatabaseCon db;

    public CourseCardPanel(String courseCode, String courseName, int sessionDuration, int sessionPerWeek, CoursePanel parent){
        this.courseCode = courseCode;
        this.parentPanel = parent;
        //Initialising Member Variables
        courseCodeLabel = new JLabel("Course Code : "+courseCode);
        courseNameLabel = new JLabel("Course Name : "+courseName);
        sessionDurationLabel = new JLabel("Session Duration (Hours) : "+sessionDuration);
        sessionPerWeekLabel = new JLabel("Session/Week : "+sessionPerWeek);
        deleteCourseButton = new JButton("Delete");

        //Editing Members
        deleteCourseButton.setBackground(Constant.RED);

        //Adding Listeners
        deleteCourseButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(courseCodeLabel, Constraint.setPosition(0,0));
        add(courseNameLabel,Constraint.setPosition(1,0));
        add(sessionDurationLabel,Constraint.setPosition(0,1));
        add(sessionPerWeekLabel,Constraint.setPosition(1,1));
        add(deleteCourseButton,Constraint.setPosition(2,0,1,2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == deleteCourseButton ){
            try{
                db = new DatabaseCon();
                db.deleteCourse(courseCode);
                parentPanel.displayCourse();
            }catch (Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }
}
