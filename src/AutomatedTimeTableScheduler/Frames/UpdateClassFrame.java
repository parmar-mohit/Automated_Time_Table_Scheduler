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

public class UpdateClassFrame extends JFrame implements ActionListener {
    private JLabel panelNameLabel, classLabel,divisionLabel,messageLabel;
    private CourseSelectionTable classCourseTable;
    private JButton updateClassButton;
    private DatabaseCon db;
    private int year;
    private String division;
    public UpdateClassFrame(int year,String division){
        this.year = year;
        this.division = division;
        //Initialising Member
        panelNameLabel = new JLabel("Update Class");
        String yearString = "";
        switch(year){
            case 1:
                yearString = "FE";
                break;

            case 2:
                yearString = "SE";
                break;

            case 3:
                yearString = "TE";
                break;

            case 4:
                yearString = "BE";
                break;
        }
        classLabel = new JLabel("Class : "+yearString);
        divisionLabel = new JLabel("Division : "+division);
        classCourseTable = new CourseSelectionTable();
        messageLabel = new JLabel();
        updateClassButton = new JButton("Update Class");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listener
        updateClassButton.addActionListener(this);

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
        add(classLabel,Constraint.setPosition(0,1));
        add(divisionLabel,Constraint.setPosition(1,1));
        add(classCourseTable.getScrollPane(),Constraint.setPosition(0,2,2,1));
        add(messageLabel,Constraint.setPosition(0,3,2,1));
        add(updateClassButton,Constraint.setPosition(0,4,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<String> courseCodeList = new ArrayList<>(5);
        for( int i = 0; i < classCourseTable.getRowCount(); i++ ){
            if( classCourseTable.getSelection(i) ){
                courseCodeList.add((String)classCourseTable.getValueAt(i,0) );
            }
        }
        if( courseCodeList.size() < 5 ){
            messageLabel.setText("Please select atleast 5 courses for this class");
            Constraint.labelDeleteAfterTime(messageLabel);
            return;
        }

        try{
            db = new DatabaseCon();
            db.updateClass(year,division,courseCodeList);
            messageLabel.setText("Class Updated");
            Constraint.labelDeleteAfterTime(messageLabel);
        }catch (Exception excp){
            System.out.println(excp);
        }finally {
            db.closeConnection();
        }
    }

    private void fillTable(){
        try{
            db = new DatabaseCon();
            classCourseTable.setCourseList(db.getCourseList());
            ArrayList<String> courseCodeList = db.getCourseCodeListForClass(year,division);
            classCourseTable.setSelection(courseCodeList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
