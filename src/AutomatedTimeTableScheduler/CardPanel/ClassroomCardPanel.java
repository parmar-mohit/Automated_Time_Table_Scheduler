package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Panels.ClassroomPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassroomCardPanel extends JPanel implements ActionListener {

    private JLabel roomNameLabel;
    private JButton deleteClassroomButton;
    private ClassroomPanel parentPanel;
    private DatabaseCon db;
    private int roomId;

    public ClassroomCardPanel(String roomName, int roomId,ClassroomPanel parent){
        this.roomId = roomId;
        this.parentPanel = parent;
        //Initialising Member Variables
        roomNameLabel = new JLabel("Room Name : "+roomName);
        deleteClassroomButton = new JButton("Delete");

        //Editing Members
        deleteClassroomButton.setBackground(Constant.RED);

        //Adding Listeners
        deleteClassroomButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding members to Panel
        add(roomNameLabel, Constraint.setPosition(0,0));
        add(deleteClassroomButton,Constraint.setPosition(1,0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == deleteClassroomButton ){
            try{
                db = new DatabaseCon();
                db.deleteClassroom(roomId);
                parentPanel.displayClassroom();
            }catch (Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }
}
