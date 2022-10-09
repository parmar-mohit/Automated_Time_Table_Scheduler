package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTeacherPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel,firstnameLabel,lastnameLabel,messageLabel;
    private JTextField firstnameTextField,lastnameTextField;
    private JButton addTeacherButton;
    private DatabaseCon db;

    public AddTeacherPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Add Teacher");
        firstnameLabel = new JLabel("Teacher's Firstname : ");
        firstnameTextField = new JTextField(20);
        lastnameLabel = new JLabel("Teacher Lastname : ");
        lastnameTextField = new JTextField(20);
        messageLabel = new JLabel();
        addTeacherButton = new JButton("Add Teacher");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        addTeacherButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(firstnameLabel,Constraint.setPosition(0,1));
        add(firstnameTextField,Constraint.setPosition(1,1));
        add(lastnameLabel,Constraint.setPosition(0,2));
        add(lastnameTextField,Constraint.setPosition(1,2));
        add(messageLabel,Constraint.setPosition(0,3,2,1));
        add(addTeacherButton,Constraint.setPosition(0,4,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String firstname = firstnameTextField.getText();
        if( firstname.equals("") ){
            messageLabel.setText("Enter Firstname");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        firstname = Constraint.getFormattedText(firstname);

        String lastname = lastnameTextField.getText();
        if( lastname.equals("") ){
            messageLabel.setText("Enter Lastname");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }
        lastname = Constraint.getFormattedText(lastname);

        try{
            db = new DatabaseCon();
            db.addTeacher(firstname,lastname);

            messageLabel.setText("Teacher Id Added");
            Constraint.labelDeleteAfterTime(messageLabel);

            //Clearing Input Fields
            firstnameTextField.setText("");
            lastnameTextField.setText("");
        }catch(Exception excp){
            excp.printStackTrace();
        }
    }
}
