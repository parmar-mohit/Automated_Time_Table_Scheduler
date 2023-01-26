package AutomatedTimeTableScheduler.Frames;

import AutomatedTimeTableScheduler.CustomComponents.CourseSelectionTable;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UpdateClassroomFrame extends JFrame implements ActionListener {
    private JLabel panelNameLabel,roomNameLabel,messageLabel;
    private JTextField roomNameTextField;
    private CourseSelectionTable courseSelectionTable;
    private JButton addClassroomButton;
    private DatabaseCon db;
    private int roomId;

    public UpdateClassroomFrame(String roomName,int roomId){
        this.roomId = roomId;
        //Initialising Member Variables
        panelNameLabel = new JLabel("Update Classroom");
        roomNameLabel = new JLabel("Room Name : ");
        roomNameTextField = new JTextField(roomName,20);
        courseSelectionTable = new CourseSelectionTable();
        messageLabel = new JLabel();
        addClassroomButton = new JButton("Update Classroom");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listeners
        addClassroomButton.addActionListener(this);

        //Filling Table
        fillTable();

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
        add(roomNameLabel,Constraint.setPosition(0,1));
        add(roomNameTextField,Constraint.setPosition(1,1,Constraint.LEFT));
        add(courseSelectionTable.getScrollPane(),Constraint.setPosition(0,2,2,1));
        add(messageLabel,Constraint.setPosition(0,3,2,1));
        add(addClassroomButton,Constraint.setPosition(0,4,2,1));
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

        ArrayList<String> courseCodeList = new ArrayList<>(5);
        for( int i = 0; i < courseSelectionTable.getRowCount(); i++ ){
            if( courseSelectionTable.getSelection(i) ){
                courseCodeList.add((String)courseSelectionTable.getValueAt(i,0) );
            }
        }
        if( courseCodeList.size() < 1 ){
            messageLabel.setText("Please select atleast 1 course for this classroom");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        try{
            db = new DatabaseCon();
            db.updateClassroom(roomId,roomName,courseCodeList);

            messageLabel.setText("Classroom Updated");
            Constraint.labelDeleteAfterTime(messageLabel);
        }catch(Exception excp){
            excp.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }

    private void fillTable(){
        try{
            db = new DatabaseCon();
            courseSelectionTable.setCourseList(db.getCourseList());
            courseSelectionTable.setSelection(db.getCourseCodeListForRoom(roomId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
