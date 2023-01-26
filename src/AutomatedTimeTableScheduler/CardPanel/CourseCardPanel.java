package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Frames.UpdateCourseFrame;
import AutomatedTimeTableScheduler.Panels.CoursePanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CourseCardPanel extends JPanel implements ActionListener {

    private JLabel courseCodeLabel,courseNameLabel,sessionDurationLabel,sessionPerWeekLabel;
    private JButton updateCourseButton,deleteCourseButton;
    private CoursePanel parentPanel;
    private String courseCode;
    private String courseName;
    private int sessionDuration,sessionPerWeek;
    private DatabaseCon db;

    public CourseCardPanel(String courseCode, String courseName, int sessionDuration, int sessionPerWeek, CoursePanel parent){
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.sessionDuration = sessionDuration;
        this.sessionPerWeek = sessionPerWeek;
        this.parentPanel = parent;
        //Initialising Member Variables
        courseCodeLabel = new JLabel("Course Code : "+courseCode);
        courseNameLabel = new JLabel("Course Name : "+courseName);
        sessionDurationLabel = new JLabel("Session Duration (Hours) : "+sessionDuration);
        sessionPerWeekLabel = new JLabel("Session/Week : "+sessionPerWeek);
        updateCourseButton = new JButton("Update");
        deleteCourseButton = new JButton("Delete");

        //Editing Members
        deleteCourseButton.setBackground(Constant.RED);

        //Adding Listeners
        updateCourseButton.addActionListener(this);
        deleteCourseButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(courseCodeLabel, Constraint.setPosition(0,0));
        add(courseNameLabel,Constraint.setPosition(1,0));
        add(sessionDurationLabel,Constraint.setPosition(0,1));
        add(sessionPerWeekLabel,Constraint.setPosition(1,1));
        add(updateCourseButton,Constraint.setPosition(2,0));
        add(deleteCourseButton,Constraint.setPosition(2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == updateCourseButton ){
            getTopLevelAncestor().setVisible(false);
            JFrame updateCourseFrame = new UpdateCourseFrame(courseCode,courseName,sessionDuration,sessionPerWeek);
            updateCourseFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    updateCourseFrame.dispose();
                    getTopLevelAncestor().setVisible(true);
                    parentPanel.displayCourse();
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
        }else if( e.getSource() == deleteCourseButton ){
            int result = JOptionPane.showConfirmDialog(getTopLevelAncestor(),"Are you sure you want to delete course?");

            if( result == JOptionPane.YES_NO_OPTION ){
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
}
