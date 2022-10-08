package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;

public class CourseCardPanel extends JPanel {

    private JLabel courseCodeLabel,courseNameLabel,sessionDurationLabel,sessionPerWeekLabel;

    public CourseCardPanel(String courseCode,String courseName, int sessionDuration, int sessionPerWeek){
        //Initialising Member Variables
        courseCodeLabel = new JLabel("Course Code : "+courseCode);
        courseNameLabel = new JLabel("Course Name : "+courseName);
        sessionDurationLabel = new JLabel("Session Duration (minutes) : "+sessionDuration);
        sessionPerWeekLabel = new JLabel("Session/Week : "+sessionPerWeek);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(courseCodeLabel, Constraint.setPosition(0,0));
        add(courseNameLabel,Constraint.setPosition(1,0));
        add(sessionDurationLabel,Constraint.setPosition(0,1));
        add(sessionPerWeekLabel,Constraint.setPosition(1,1));
    }
}
