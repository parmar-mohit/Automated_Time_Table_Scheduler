package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CustomComponents.TimeField;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;

public class TimeSlotsPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel,startTimeLabel,endTimeLabel,breakStartTimeLabel,breakEndTimeLabel,messageLabel;
    private TimeField startTimeField,endTimeField,breakStartTimeField,breakEndTimeField;
    private JButton generateTimeSlotsButton;

    private DatabaseCon db;

    public TimeSlotsPanel() {
        //Initialising Member Variables
        panelNameLabel = new JLabel("Time Slots");
        startTimeLabel = new JLabel("College Start Time : ");
        startTimeField = new TimeField();
        endTimeLabel = new JLabel("College End Time : ");
        endTimeField = new TimeField();
        breakStartTimeLabel = new JLabel("Break Start Time : ");
        breakStartTimeField = new TimeField();
        breakEndTimeLabel = new JLabel("Break End Time : ");
        breakEndTimeField = new TimeField();
        generateTimeSlotsButton = new JButton("Generate Time Slots");
        messageLabel = new JLabel();

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        generateTimeSlotsButton.setBackground(Constant.BUTTON_BACKGROUND);

        //Adding Listeners
        generateTimeSlotsButton.addActionListener(this);

        //Editing Panel Details
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(startTimeLabel,Constraint.setPosition(0,1,Constraint.RIGHT));
        add(startTimeField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(endTimeLabel,Constraint.setPosition(2,1,Constraint.RIGHT));
        add(endTimeField,Constraint.setPosition(3,1,Constraint.LEFT));
        add(breakStartTimeLabel,Constraint.setPosition(0,2,Constraint.RIGHT));
        add(breakStartTimeField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(breakEndTimeLabel,Constraint.setPosition(2,2,Constraint.RIGHT));
        add(breakEndTimeField,Constraint.setPosition(3,2,Constraint.LEFT));
        add(generateTimeSlotsButton,Constraint.setPosition(1,3,2,1));
        add(messageLabel,Constraint.setPosition(0,4,3,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( startTimeField.getTime() == null ){
            messageLabel.setText("Enter College Start Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        Time collegeStartTime = startTimeField.getTime();

        if( endTimeField.getTime() == null ){
            messageLabel.setText("Enter College End Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        Time collegeEndTime = endTimeField.getTime();

        if( collegeEndTime.before(collegeStartTime) ){
            messageLabel.setText("College End Time cannot be before Start Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        if( breakStartTimeField.getTime() == null ){
            messageLabel.setText("Enter Break Start Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        Time breakStartTime = breakStartTimeField.getTime();

        if( breakEndTimeField.getTime() == null ){
            messageLabel.setText("Enter Break End Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        Time breakEndTime = breakEndTimeField.getTime();

        if( breakEndTime.before(breakStartTime) ){
            messageLabel.setText("Break End Time cannot be before Start Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        try{
            db = new DatabaseCon();
            db.insertTimeInfo(collegeStartTime,collegeEndTime,breakStartTime,breakEndTime);
            messageLabel.setText("Time Info Updated");
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }
}
