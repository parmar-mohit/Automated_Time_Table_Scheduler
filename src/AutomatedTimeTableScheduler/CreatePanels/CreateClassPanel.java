package AutomatedTimeTableScheduler.CreatePanels;

import AutomatedTimeTableScheduler.CustomComponents.ClassCourseTable;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateClassPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel, classLabel,divisionLabel,messageLabel;
    private JComboBox yearComboBox,divisionComboBox;
    private ClassCourseTable classCourseTable;
    private JButton createClassButton;
    private DatabaseCon db;

    public CreateClassPanel(){
        //Initialising Member
        panelNameLabel = new JLabel("Create Class");
        classLabel = new JLabel("Class : ");
        yearComboBox = new JComboBox(new Object[]{"FE","SE","TE","BE"});
        divisionLabel = new JLabel("Division : ");
        divisionComboBox = new JComboBox(new Object[]{"A","B","C","D"});
        classCourseTable = new ClassCourseTable();
        messageLabel = new JLabel();
        createClassButton = new JButton("Create Class");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listener
        createClassButton.addActionListener(this);

        //Filling Table
        fillTable();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,4,1));
        add(classLabel,Constraint.setPosition(0,1,Constraint.RIGHT));
        add(yearComboBox,Constraint.setPosition(1,1,Constraint.LEFT));
        add(divisionLabel,Constraint.setPosition(2,1,Constraint.RIGHT));
        add(divisionComboBox,Constraint.setPosition(3,1,Constraint.LEFT));
        add(classCourseTable.getScrollPane(),Constraint.setPosition(0,2,4,1));
        add(messageLabel,Constraint.setPosition(0,3,4,1));
        add(createClassButton,Constraint.setPosition(0,4,4,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == createClassButton ){
            if( yearComboBox.getSelectedItem() == null ){
                messageLabel.setText("Select Year");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            String yearString = yearComboBox.getSelectedItem().toString();
            int year = 0;
            if( yearString.equals("FE") ){
                year = 1;
            }else if( yearString.equals("SE") ){
                year = 2;
            }else if( yearString.equals("TE") ){
                year = 3;
            }else{
                year = 4;
            }

            if( divisionComboBox.getSelectedItem() == null ){
                messageLabel.setText("Select Division");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            String division = divisionComboBox.getSelectedItem().toString();

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

                if( db.checkClassExist(year,division) ){
                    messageLabel.setText("Class with same year and division already exist");
                    Constraint.labelDeleteAfterTime(messageLabel);
                    return;
                }

                db.createClass(year,division,courseCodeList);
                classCourseTable.reset();
                messageLabel.setText("Class Created");
                Constraint.labelDeleteAfterTime(messageLabel);
            }catch (Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }

    private void fillTable(){
        try{
            db = new DatabaseCon();
            classCourseTable.setCourseList(db.getCourseList());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
