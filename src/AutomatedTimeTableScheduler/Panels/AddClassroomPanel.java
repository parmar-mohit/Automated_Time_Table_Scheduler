package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddClassroomPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel,roomNameLabel,messageLabel;
    private JTextField roomNameTextField;
    private JButton addClassroomButton;
    private DatabaseCon db;

    public AddClassroomPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Add Classroom");
        roomNameLabel = new JLabel("Room Name : ");
        roomNameTextField = new JTextField(20);
        messageLabel = new JLabel();
        addClassroomButton = new JButton("Add Classroom");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        addClassroomButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(roomNameLabel,Constraint.setPosition(0,1));
        add(roomNameTextField,Constraint.setPosition(1,1));
        add(messageLabel,Constraint.setPosition(0,2,2,1));
        add(addClassroomButton,Constraint.setPosition(0,3,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String roomName = roomNameTextField.getText();
        if( roomName.equals("") ){
            messageLabel.setText("Enter Room Name");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        roomName = roomName.toUpperCase();

        try{
            db = new DatabaseCon();
            db.addClassroom(roomName);

            messageLabel.setText("Classroom Added");
            Constraint.labelDeleteAfterTime(messageLabel);

            //Clearing input fields
            roomNameTextField.setText("");
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }
}
