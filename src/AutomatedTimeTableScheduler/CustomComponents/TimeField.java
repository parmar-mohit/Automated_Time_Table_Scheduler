package AutomatedTimeTableScheduler.CustomComponents;

import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Time;

public class TimeField extends JPanel {
    private JFormattedTextField timeTextField;
    private JComboBox comboBox;

    public TimeField(){
        //Initialising Member Variables
        try{
            MaskFormatter mask = new MaskFormatter("##hh:##mm");
            mask.setPlaceholderCharacter('#');
            timeTextField = new JFormattedTextField(mask);
        }catch( Exception e){
            System.out.println(e);
        }
        comboBox = new JComboBox(new Object[]{"AM","PM"});

        //Editing Members
        timeTextField.setMinimumSize(new Dimension(75,25));
        timeTextField.setPreferredSize(new Dimension(75,25));

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.TIME_SELECTOR_PANEL_BACKGROUND);

        //Adding Member to Panel
        add(timeTextField, Constraint.setPosition(0,0));
        add(comboBox,Constraint.setPosition(1,0));
    }

    public Time getTime(){
        String timeString = timeTextField.getText();
        int hours,minutes;
        try{
            hours = Integer.parseInt(timeString.substring(0,2));
            minutes = Integer.parseInt(timeString.substring(5,7));
            if( comboBox.getSelectedItem().equals("PM") && hours != 12 ){
                hours += 12;
            }

            Time time = new Time(hours,minutes,0);
            return time;
        }catch(Exception e){
            return null;
        }
    }

    public void setTime(Time time){
        String timeString = new String();

        int hours = time.getHours();
        if( hours > 12 ){
            timeString += "0"+(hours - 12);
            comboBox.setSelectedItem("PM");
        }else if(hours == 12){
            timeString += hours;
            comboBox.setSelectedItem("PM");
        }else if( hours < 10 ){
            timeString += "0"+hours;
        }else{
            timeString += hours;
        }
        timeString += "hh:";
        timeString += time.getMinutes();
        timeString += "mm";

        timeTextField.setText(timeString);
    }
}
