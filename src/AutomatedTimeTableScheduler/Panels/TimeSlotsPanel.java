package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;

public class TimeSlotsPanel extends JPanel implements ActionListener  {

    private JLabel panelNameLabel,messageLabel;
    private JPanel timeSlotListPanel;
    private JScrollPane scrollPane;
    private ArrayList<TimeSelectorPanel> timeSelectorPanelArrayList;
    private JButton addTimeSlotButton,removeTimeSlotButton,saveTimeSlotButton;
    private DatabaseCon db;

    public TimeSlotsPanel() {
        //Initialising Member Variables
        panelNameLabel = new JLabel("Time Slots");
        timeSlotListPanel = new JPanel();
        scrollPane = new JScrollPane(timeSlotListPanel);
        timeSelectorPanelArrayList = new ArrayList<>();
        addTimeSlotButton = new JButton("Add Time Slot");
        removeTimeSlotButton = new JButton("Remove Time Slot");
        saveTimeSlotButton = new JButton("Save Time Slots");
        messageLabel = new JLabel();

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        timeSlotListPanel.setLayout(new GridBagLayout());
        scrollPane.setMinimumSize(new Dimension(1000, 400));
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        addTimeSlotButton.setBackground(Constant.BUTTON_BACKGROUND);
        addTimeSlotButton.setPreferredSize(Constant.BUTTON_SIZE);
        removeTimeSlotButton.setBackground(Constant.BUTTON_BACKGROUND);
        removeTimeSlotButton.setPreferredSize(Constant.BUTTON_SIZE);
        saveTimeSlotButton.setBackground(Constant.BUTTON_BACKGROUND);
        saveTimeSlotButton.setPreferredSize(Constant.BUTTON_SIZE);

        //Adding Listeners
        addTimeSlotButton.addActionListener(this);
        removeTimeSlotButton.addActionListener(this);
        saveTimeSlotButton.addActionListener(this);

        //Getting and Displaying Time Slots
        getAndDisplayTimeSlots();

        //Editing Panel Details
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,3,1));
        add(scrollPane,Constraint.setPosition(0,1,3,1));
        add(addTimeSlotButton,Constraint.setPosition(0,2));
        add(removeTimeSlotButton,Constraint.setPosition(1,2));
        add(saveTimeSlotButton,Constraint.setPosition(2,2));
        add(messageLabel,Constraint.setPosition(0,3,3,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == addTimeSlotButton ) {
            TimeSelectorPanel timeSelectorPanel = new TimeSelectorPanel(timeSelectorPanelArrayList.size() + 1);
            timeSelectorPanel.setPreferredSize(new Dimension(950, 75));
            timeSelectorPanelArrayList.add(timeSelectorPanel);
            timeSlotListPanel.add(timeSelectorPanel, Constraint.setPosition(0, timeSelectorPanelArrayList.size()));
            timeSelectorPanel.revalidate();
            timeSelectorPanel.repaint();
        }else if( e.getSource() == removeTimeSlotButton ){
            if( timeSelectorPanelArrayList.size() > 0 ) {
                TimeSelectorPanel timeSelectorPanel = timeSelectorPanelArrayList.get(timeSelectorPanelArrayList.size() - 1);
                timeSelectorPanelArrayList.remove(timeSelectorPanel);
                timeSlotListPanel.remove(timeSelectorPanel);
                timeSlotListPanel.revalidate();
                timeSlotListPanel.repaint();
            }
        }else if( e.getSource() == saveTimeSlotButton ){
            ArrayList<Time> startTimeList = new ArrayList<>();
            ArrayList<Time> endTimeList = new ArrayList<>();

            for( int i = 0; i < timeSelectorPanelArrayList.size(); i++){
                TimeSelectorPanel timeSelectorPanel = timeSelectorPanelArrayList.get(i);

                Time startTime = timeSelectorPanel.getStartTime();
                if( startTime == null ){
                    messageLabel.setText("Enter Correct Start Time for Slot "+(i+1));
                    Constraint.labelDeleteAfterTime(messageLabel);
                    return;
                }

                Time endTime = timeSelectorPanel.getEndTime();
                if( endTime == null ) {
                    messageLabel.setText("Enter Correct End Time for Slot " + (i + 1));
                    Constraint.labelDeleteAfterTime(messageLabel);
                    return;
                }

                if( startTime.after(endTime) ){
                    messageLabel.setText("Invalid Start Time and End Time Value for Slot "+i);
                    Constraint.labelDeleteAfterTime(messageLabel);
                    return;
                }

                for( int j = 0; j < startTimeList.size(); j++ ){
                    Time existingStartTime = startTimeList.get(j);
                    Time existingEndTime = endTimeList.get(j);

                    if( existingStartTime.before(startTime) ){
                        if( existingEndTime.after(startTime) ){
                            messageLabel.setText("Overlapping Time Slots "+(i+1)+" and "+(j+1));
                            Constraint.labelDeleteAfterTime(messageLabel);
                            return;
                        }
                    }else if( existingStartTime.after(startTime) ) {
                        if( endTime.after(existingStartTime) ){
                            messageLabel.setText("Overlapping Time Slots "+(i+1)+" and "+(j+1));
                            Constraint.labelDeleteAfterTime(messageLabel);
                            return;
                        }
                    }else{
                        messageLabel.setText("Overlapping Time Slots "+(i+1)+" and "+(j+1));
                        Constraint.labelDeleteAfterTime(messageLabel);
                        return;
                    }
                }

                startTimeList.add(startTime);
                endTimeList.add(endTime);
            }

            try{
                db = new DatabaseCon();
                db.insertTimeSlots(startTimeList,endTimeList);

                messageLabel.setText("Saved Time Slots");
                Constraint.labelDeleteAfterTime(messageLabel);
            }catch(Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }

    private void getAndDisplayTimeSlots(){
        try{
            db = new DatabaseCon();
            ResultSet timeSlots = db.getTimeSlots();

            while( timeSlots.next() ){
                TimeSelectorPanel timeSelectorPanel = new TimeSelectorPanel(timeSelectorPanelArrayList.size()+1);
                timeSelectorPanel.setPreferredSize(new Dimension(950,75));
                timeSelectorPanel.setTime(timeSlots.getTime("start_time"),timeSlots.getTime("end_time"));
                timeSlotListPanel.add(timeSelectorPanel,Constraint.setPosition(0,timeSelectorPanelArrayList.size()));
                timeSlotListPanel.revalidate();
                timeSlotListPanel.repaint();
                timeSelectorPanelArrayList.add(timeSelectorPanel);
            }
        }catch(Exception e){
            System.out.println(e);
            return;
        }finally {
            db.closeConnection();
        }
    }
}
