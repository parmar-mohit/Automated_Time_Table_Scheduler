package AutomatedTimeTableScheduler.CreatePanels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;

public class CreateCoursePanel extends JPanel implements ActionListener,KeyListener {

    private JLabel panelNameLabel,courseCodeLabel,courseNameLabel,sessionDurationLabel,sessionPerWeekLabel,messageLabel;
    private JTextField courseCodeTextField,courseNameTextField,sessionDurationTextField,sessionPerWeekTextField;
    private JButton addCourseButton;
    private DatabaseCon db;
    public CreateCoursePanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Create Course");
        courseCodeLabel = new JLabel("Course Code : ");
        courseCodeTextField = new JTextField(20);
        courseNameLabel = new JLabel("Course Name : ");
        courseNameTextField = new JTextField(20);
        sessionDurationLabel = new JLabel("Session Duration (minutes) : ");
        sessionDurationTextField = new JTextField(20);
        sessionPerWeekLabel = new JLabel("Session/Week : ");
        sessionPerWeekTextField = new JTextField(20);
        messageLabel = new JLabel();
        addCourseButton = new JButton("Add Course");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        sessionDurationTextField.addKeyListener(this);
        sessionPerWeekTextField.addKeyListener(this);
        addCourseButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(courseCodeLabel,Constraint.setPosition(0,1));
        add(courseCodeTextField,Constraint.setPosition(1,1));
        add(courseNameLabel,Constraint.setPosition(0,2));
        add(courseNameTextField,Constraint.setPosition(1,2));
        add(sessionDurationLabel,Constraint.setPosition(0,3));
        add(sessionDurationTextField,Constraint.setPosition(1,3));
        add(sessionPerWeekLabel,Constraint.setPosition(0,4));
        add(sessionPerWeekTextField,Constraint.setPosition(1,4));
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

        if( sessionDurationTextField.getText().equals("") ){
            messageLabel.setText("Enter Session Duration");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        int sessionDuration = Integer.parseInt(sessionDurationTextField.getText());

        if( sessionDuration < 60 ){
            messageLabel.setText("Session Duration cannot be less than 60 minutes");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        if( sessionPerWeekTextField.getText().equals("") ){
            messageLabel.setText("Enter Session/Week");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        int sessionPerWeek = Integer.parseInt(sessionPerWeekTextField.getText());

        try{
            db = new DatabaseCon();

            if( db.checkCourseExist(courseCode) ){
                messageLabel.setText("A Course with same Course Code Exists");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }

            db.addCourse(courseCode,courseName,sessionDuration,sessionPerWeek);
            messageLabel.setText("Course Added");
            Constraint.labelDeleteAfterTime(messageLabel);

            //Clearing TextFields
            courseCodeTextField.setText("");
            courseNameTextField.setText("");
            sessionDurationTextField.setText("");
            sessionPerWeekTextField.setText("");
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if( !Character.isDigit(e.getKeyChar()) ){
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
