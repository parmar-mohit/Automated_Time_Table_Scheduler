package AutomatedTimeTableScheduler.Frames;

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

public class UpdateTeacherFrame extends JFrame implements ActionListener, KeyListener {

    private JLabel panelNameLabel,firstnameLabel,lastnameLabel,messageLabel;
    private JTextField firstnameTextField,lastnameTextField;

    private CoursePreferenceTable preferenceTable;
    private JButton updateTeacherButton;
    private DatabaseCon db;
    private int teacherId;

    public UpdateTeacherFrame(String firstname,String lastname,int teacherId){
        this.teacherId = teacherId;
        //Initialising Member Variables
        panelNameLabel = new JLabel("Update Teacher Details");
        firstnameLabel = new JLabel("Teacher's Firstname : ");
        firstnameTextField = new JTextField(firstname,20);
        lastnameLabel = new JLabel("Teacher Lastname : ");
        lastnameTextField = new JTextField(lastname,20);
        preferenceTable = new CoursePreferenceTable();
        messageLabel = new JLabel();
        updateTeacherButton = new JButton("Update Teacher");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        firstnameTextField.addKeyListener(this);
        lastnameTextField.addKeyListener(this);
        updateTeacherButton.addActionListener(this);

        //Filling Table
        fillPreferenceTable();

        //Editing Panel
        setTitle(Constant.APP_NAME);
        setSize(Constant.SCREEN_SIZE);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Constant.FRAME_BACKGROUND);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(firstnameLabel,Constraint.setPosition(0,1));
        add(firstnameTextField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(lastnameLabel,Constraint.setPosition(0,2));
        add(lastnameTextField,Constraint.setPosition(1,2,Constraint.LEFT));
        add(preferenceTable.getScrollPane(),Constraint.setPosition(0,3,2,1));
        add(messageLabel,Constraint.setPosition(0,4,2,1));
        add(updateTeacherButton,Constraint.setPosition(0,5,2,1));
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

        Dictionary<String,Integer> preferenceList = preferenceTable.getPrefenceList();
        if( preferenceList == null ){
            messageLabel.setText("Select Preference for All Courses");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        try{
            db = new DatabaseCon();
            db.updateTeacher(teacherId,firstname,lastname,preferenceList);

            messageLabel.setText("Teacher Id Updated");
            Constraint.labelDeleteAfterTime(messageLabel);
        }catch(Exception excp){
            excp.printStackTrace();
        }
    }

    private void fillPreferenceTable(){
        try{
            db = new DatabaseCon();
            preferenceTable.setCourseList(db.getCourseList());
            preferenceTable.setPreference(db.getTeacherCoursePreferenceList(teacherId));
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
