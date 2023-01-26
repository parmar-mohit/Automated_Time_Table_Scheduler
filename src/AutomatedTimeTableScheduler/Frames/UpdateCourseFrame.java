package AutomatedTimeTableScheduler.Frames;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateCourseFrame extends JFrame implements ActionListener {

    private JLabel panelNameLabel,courseCodeLabel,courseNameLabel,sessionDurationLabel,sessionPerWeekLabel,messageLabel;
    private JTextField courseNameTextField;
    private JComboBox sessionDurationComboBox,sessionPerWeekComboBox;
    private JButton updateCourseButton;
    private DatabaseCon db;
    private String courseCode;
    public UpdateCourseFrame(String courseCode, String courseName, int sessionDuration, int sessionPerWeek){
        this.courseCode = courseCode;
        //Initialising Member Variables
        panelNameLabel = new JLabel("Update Course");
        courseCodeLabel = new JLabel("Course Code : "+courseCode);
        courseNameLabel = new JLabel("Course Name : ");
        courseNameTextField = new JTextField(courseName,20);
        sessionDurationLabel = new JLabel("Session Duration (Hours) : ");
        sessionDurationComboBox = new JComboBox(new Object[]{1,2});
        sessionPerWeekLabel = new JLabel("Session/Week : ");
        sessionPerWeekComboBox = new JComboBox(new Object[]{1,2,3,4,5});
        messageLabel = new JLabel();
        updateCourseButton = new JButton("Update Course");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));
        sessionDurationComboBox.setSelectedItem(sessionDuration);
        sessionPerWeekComboBox.setSelectedItem(sessionPerWeek);

        //Adding Listeners
        updateCourseButton.addActionListener(this);

        //Editing Panel
        setTitle(Constant.APP_NAME);
        setSize(Constant.SCREEN_SIZE);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Constant.FRAME_BACKGROUND);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(courseCodeLabel,Constraint.setPosition(0,1,2,1));
        add(courseNameLabel,Constraint.setPosition(0,2,Constraint.RIGHT));
        add(courseNameTextField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(sessionDurationLabel,Constraint.setPosition(0,3,Constraint.RIGHT));
        add(sessionDurationComboBox,Constraint.setPosition(1,3,Constraint.LEFT));
        add(sessionPerWeekLabel,Constraint.setPosition(0,4,Constraint.RIGHT));
        add(sessionPerWeekComboBox,Constraint.setPosition(1,4,Constraint.LEFT));
        add(messageLabel,Constraint.setPosition(0,5,2,1));
        add(updateCourseButton,Constraint.setPosition(0,6,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String courseName = courseNameTextField.getText();
        if( courseName.equals("") ){
            messageLabel.setText("Enter Course Name");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        courseName = Constraint.getFormattedText(courseName);

        int sessionDuration = (int)sessionDurationComboBox.getSelectedItem();
        int sessionPerWeek = Integer.parseInt(sessionPerWeekComboBox.getSelectedItem()+"");

        try{
            db = new DatabaseCon();
            db.updateCourse(courseCode,courseName,sessionDuration,sessionPerWeek);
            messageLabel.setText("Course Updated");
            Constraint.labelDeleteAfterTime(messageLabel);
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }
}
