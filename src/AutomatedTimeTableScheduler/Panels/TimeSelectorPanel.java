package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CustomComponents.TimeField;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;

public class TimeSelectorPanel extends JPanel {

    private JLabel panelNameLabel,startTimeLabel,endTimeLabel;
    public TimeField startTimeField,endTimeField;

    public TimeSelectorPanel(int slotNo){
        panelNameLabel = new JLabel("Time Slot : "+slotNo);
        startTimeLabel = new JLabel("Start Time : ");
        startTimeField = new TimeField();
        endTimeLabel = new JLabel("End Time : ");
        endTimeField = new TimeField();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.TIME_SELECTOR_PANEL_BACKGROUND);

        //Adding Members to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,4,1));
        add(startTimeLabel,Constraint.setPosition(0,1));
        add(startTimeField,Constraint.setPosition(1,1));
        add(endTimeLabel,Constraint.setPosition(2,1));
        add(endTimeField,Constraint.setPosition(3,1));
    }

    public Time getStartTime(){
        return startTimeField.getTime();
    }

    public Time getEndTime(){
        return endTimeField.getTime();
    }

    public void setTime(Time startTime,Time endTime){
        startTimeField.setTime(startTime);
        endTimeField.setTime(endTime);
    }
}
