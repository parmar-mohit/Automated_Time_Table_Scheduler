package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Panels.TeacherPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;

public class TeacherCardPanel extends JPanel {

    private JLabel firstnameLabel,lastnameLabel;
    private int teacherId;

    public TeacherCardPanel(String firstname,String lastname,int teacherId){
        this.teacherId = teacherId;
        //Initialising Member Variables
        firstnameLabel = new JLabel("Firstname : "+firstname);
        lastnameLabel = new JLabel("Lastname : "+lastname);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Members to Panel
        add(firstnameLabel, Constraint.setPosition(0,0));
        add(lastnameLabel,Constraint.setPosition(1,0));
    }
}
