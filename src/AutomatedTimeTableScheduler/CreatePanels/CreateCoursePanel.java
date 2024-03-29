package AutomatedTimeTableScheduler.CreatePanels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateCoursePanel extends JPanel implements ActionListener {

    private final JLabel panelNameLabel;
    private final JLabel courseCodeLabel;
    private final JLabel courseNameLabel;
    private final JLabel sessionDurationLabel;
    private final JLabel sessionPerWeekLabel;
    private final JLabel messageLabel;
    private final JTextField courseCodeTextField;
    private final JTextField courseNameTextField;
    private final JComboBox sessionDurationComboBox;
    private final JComboBox sessionPerWeekComboBox;
    private final JButton addCourseButton;
    private DatabaseCon db;
    public CreateCoursePanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Enter Course Details");
        courseCodeLabel = new JLabel("Course Code : ");
        courseCodeTextField = new JTextField(20);
        courseNameLabel = new JLabel("Course Name : ");
        courseNameTextField = new JTextField(20);
        sessionDurationLabel = new JLabel("Session Duration (Hours) : ");
        sessionDurationComboBox = new JComboBox(new Object[]{1,2});
        sessionPerWeekLabel = new JLabel("Session/Week : ");
        sessionPerWeekComboBox = new JComboBox(new Object[]{1,2,3,4,5});
        messageLabel = new JLabel();
        addCourseButton = new JButton("Add Course");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        addCourseButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(courseCodeLabel,Constraint.setPosition(0,1,Constraint.RIGHT));
        add(courseCodeTextField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(courseNameLabel,Constraint.setPosition(0,2,Constraint.RIGHT));
        add(courseNameTextField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(sessionDurationLabel,Constraint.setPosition(0,3,Constraint.RIGHT));
        add(sessionDurationComboBox,Constraint.setPosition(1,3,Constraint.LEFT));
        add(sessionPerWeekLabel,Constraint.setPosition(0,4,Constraint.RIGHT));
        add(sessionPerWeekComboBox,Constraint.setPosition(1,4,Constraint.LEFT));
        add(messageLabel,Constraint.setPosition(0,5,2,1));
        add(addCourseButton,Constraint.setPosition(0,6,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String courseCode = courseCodeTextField.getText();
        if( courseCode.equals("") ){
            messageLabel.setText("Enter Course Code");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        courseCode = courseCode.toUpperCase();

        String courseName = courseNameTextField.getText();
        if( courseName.equals("") ){
            messageLabel.setText("Enter Course Name");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        courseName = Constraint.getFormattedText(courseName);

        int sessionDuration = (int)sessionDurationComboBox.getSelectedItem();
        int sessionPerWeek = Integer.parseInt(sessionPerWeekComboBox.getSelectedItem()+"");
        String abbreviation = Constraint.generateAbbreviation(courseName,"course");

        try{
            db = new DatabaseCon();

            if( db.checkCourseExist(courseCode) ){
                messageLabel.setText("A Course with same Course Code Exists");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }

            db.addCourse(courseCode,courseName,sessionDuration,sessionPerWeek,abbreviation);
            messageLabel.setText("Course Added");
            Constraint.labelDeleteAfterTime(messageLabel);

            //Clearing TextFields
            courseCodeTextField.setText("");
            courseNameTextField.setText("");
            sessionDurationComboBox.setSelectedIndex(0);
            sessionPerWeekComboBox.setSelectedIndex(0);
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }
}