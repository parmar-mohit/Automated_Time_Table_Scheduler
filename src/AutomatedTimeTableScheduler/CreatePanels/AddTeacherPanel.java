package AutomatedTimeTableScheduler.CreatePanels;

import AutomatedTimeTableScheduler.CustomComponents.CoursePreferenceTable;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Dictionary;

public class AddTeacherPanel extends JPanel implements ActionListener, KeyListener {

    private final JLabel panelNameLabel;
    private final JLabel firstnameLabel;
    private final JLabel lastnameLabel;
    private final JLabel messageLabel;
    private final JTextField firstnameTextField;
    private final JTextField lastnameTextField;

    private final CoursePreferenceTable preferenceTable;
    private final JButton clearPreferencesButton;
    private final JButton addTeacherButton;
    private DatabaseCon db;

    public AddTeacherPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Enter Teacher Details");
        firstnameLabel = new JLabel("Teacher's Firstname : ");
        firstnameTextField = new JTextField(20);
        lastnameLabel = new JLabel("Teacher Lastname : ");
        lastnameTextField = new JTextField(20);
        preferenceTable = new CoursePreferenceTable();
        clearPreferencesButton = new JButton("Clear Preferences");
        messageLabel = new JLabel();
        addTeacherButton = new JButton("Add Teacher");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        firstnameTextField.addKeyListener(this);
        lastnameTextField.addKeyListener(this);
        clearPreferencesButton.addActionListener(this);
        addTeacherButton.addActionListener(this);

        //Filling Table
        fillPreferenceTable();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(firstnameLabel,Constraint.setPosition(0,1));
        add(firstnameTextField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(lastnameLabel,Constraint.setPosition(0,2));
        add(lastnameTextField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(preferenceTable.getScrollPane(),Constraint.setPosition(0,3,2,1));
        add(clearPreferencesButton,Constraint.setPosition(0,4,2,1));
        add(messageLabel,Constraint.setPosition(0,5,2,1));
        add(addTeacherButton,Constraint.setPosition(0,6,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == addTeacherButton) {
            String firstname = firstnameTextField.getText();
            if (firstname.equals("")) {
                messageLabel.setText("Enter Firstname");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            firstname = Constraint.getFormattedText(firstname);

            String lastname = lastnameTextField.getText();
            if (lastname.equals("")) {
                messageLabel.setText("Enter Lastname");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            lastname = Constraint.getFormattedText(lastname);

            Dictionary<String, Integer> preferenceList = preferenceTable.getPrefenceList();
            if (preferenceList == null) {
                messageLabel.setText("Select a Minimum of 3 Preferences");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }

            try {
                db = new DatabaseCon();
                db.addTeacher(firstname, lastname, preferenceList);

                messageLabel.setText("Teacher Id Added");
                Constraint.labelDeleteAfterTime(messageLabel);

                //Clearing Input Fields
                firstnameTextField.setText("");
                lastnameTextField.setText("");
                preferenceTable.resetPreferences();
            } catch (Exception excp) {
                excp.printStackTrace();
            }
        }else if( e.getSource() == clearPreferencesButton ){
            preferenceTable.resetPreferences();
        }
    }

    private void fillPreferenceTable(){
        try{
            db = new DatabaseCon();
            preferenceTable.setCourseList(db.getCourseList());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if( !Character.isAlphabetic(e.getKeyChar()) ){
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
