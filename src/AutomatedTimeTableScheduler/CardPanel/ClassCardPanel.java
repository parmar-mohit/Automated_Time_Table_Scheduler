package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;

public class ClassCardPanel extends JPanel {

    private JLabel yearLabel,divisionLabel;
    private int year;
    private String division;

    public ClassCardPanel(int year,String division){
        this.year = year;
        this.division = division;
        //Initialising Members
        yearLabel = new JLabel("Year : "+year);
        divisionLabel = new JLabel("Division : "+division);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Members to Panel
        add(yearLabel, Constraint.setPosition(0,0));
        add(divisionLabel,Constraint.setPosition(1,0));
    }
}
