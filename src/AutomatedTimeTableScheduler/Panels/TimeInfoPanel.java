package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CustomComponents.TimeField;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.ArrayList;

public class TimeInfoPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel,startTimeLabel,endTimeLabel,breakStartTimeLabel,breakEndTimeLabel,messageLabel;
    private TimeField startTimeField,endTimeField,breakStartTimeField,breakEndTimeField;
    private JButton saveTimeInfoButton;

    private DatabaseCon db;

    public TimeInfoPanel() {
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
        saveTimeInfoButton = new JButton("Save Time Details");
        messageLabel = new JLabel();

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        saveTimeInfoButton.setBackground(Constant.BUTTON_BACKGROUND);

        //Adding Listeners
        saveTimeInfoButton.addActionListener(this);

        //Setting Available Data
        setTimeInfo();

        //Editing Panel Details
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding member to Panel
        add(panelNameLabel, Constraint.setPosition(1,0,2,1));
        add(startTimeLabel,Constraint.setPosition(0,1,Constraint.RIGHT));
        add(startTimeField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(endTimeLabel,Constraint.setPosition(2,1,Constraint.RIGHT));
        add(endTimeField,Constraint.setPosition(3,1,Constraint.LEFT));
        add(breakStartTimeLabel,Constraint.setPosition(0,2,Constraint.RIGHT));
        add(breakStartTimeField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(breakEndTimeLabel,Constraint.setPosition(2,2,Constraint.RIGHT));
        add(breakEndTimeField,Constraint.setPosition(3,2,Constraint.LEFT));
        add(saveTimeInfoButton,Constraint.setPosition(1,3,2,1));
        add(messageLabel,Constraint.setPosition(1,4,2,1));
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

        if( breakStartTime.before(collegeStartTime) ){
            messageLabel.setText("Break Start Time Should be after College Start Time");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

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

        if( breakEndTime.after(collegeEndTime) ){
            messageLabel.setText("Break End Time Should be before College End Time");
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

    private void setTimeInfo(){
        try{
            db = new DatabaseCon();
            ArrayList<Time> timeInfoList = db.getTimeInfo();
            if( timeInfoList != null ) {
                startTimeField.setTime(timeInfoList.get(0));
                endTimeField.setTime(timeInfoList.get(1));
                breakStartTimeField.setTime(timeInfoList.get(2));
                breakEndTimeField.setTime(timeInfoList.get(3));
            }
        }catch( Exception e){
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }
}
