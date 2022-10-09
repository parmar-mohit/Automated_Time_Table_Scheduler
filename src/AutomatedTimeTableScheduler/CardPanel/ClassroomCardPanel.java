package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;

public class ClassroomCardPanel extends JPanel {

    private JLabel roomNameLabel;
    private int roomId;

    public ClassroomCardPanel(String roomName, int roomId){
        this.roomId = roomId;
        //Initialising Member Variables
        roomNameLabel = new JLabel("Room Name : "+roomName);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding members to Panel
        add(roomNameLabel, Constraint.setPosition(0,0));
    }
}
