package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Frames.UpdateClassroomFrame;
import AutomatedTimeTableScheduler.Panels.ClassroomPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClassroomCardPanel extends JPanel implements ActionListener {

    private JLabel roomNameLabel;
    private JButton updateClassroomButton,deleteClassroomButton;
    private ClassroomPanel parentPanel;
    private DatabaseCon db;
    private int roomId;
    private String roomName;

    public ClassroomCardPanel(String roomName, int roomId,ClassroomPanel parent){
        this.roomName = roomName;
        this.roomId = roomId;
        this.parentPanel = parent;
        //Initialising Member Variables
        roomNameLabel = new JLabel("Room Name : "+roomName);
        updateClassroomButton = new JButton("Update");
        deleteClassroomButton = new JButton("Delete");

        //Editing Members
        deleteClassroomButton.setBackground(Constant.RED);

        //Adding Listeners
        updateClassroomButton.addActionListener(this);
        deleteClassroomButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding members to Panel
        add(roomNameLabel, Constraint.setPosition(0,0));
        add(updateClassroomButton,Constraint.setPosition(1,0));
        add(deleteClassroomButton,Constraint.setPosition(2,0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == updateClassroomButton ){
            getTopLevelAncestor().setVisible(false);
            JFrame updateClassroomFrame = new UpdateClassroomFrame(roomName,roomId);
            updateClassroomFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    updateClassroomFrame.dispose();
                    getTopLevelAncestor().setVisible(true);
                    parentPanel.displayClassroom();
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
        }else if( e.getSource() == deleteClassroomButton ){
            int result = JOptionPane.showConfirmDialog(getTopLevelAncestor(),"Are you sure you want to delete course?");

            if( result == JOptionPane.YES_NO_OPTION ){
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
}
