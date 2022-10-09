package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Panels.TeacherPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeacherCardPanel extends JPanel implements ActionListener  {

    private JLabel firstnameLabel,lastnameLabel;
    private JButton deleteTeacherButton;
    private DatabaseCon db;
    private TeacherPanel parentPanel;
    private int teacherId;

    public TeacherCardPanel(String firstname,String lastname,int teacherId,TeacherPanel parent){
        this.teacherId = teacherId;
        this.parentPanel = parent;
        //Initialising Member Variables
        firstnameLabel = new JLabel("Firstname : "+firstname);
        lastnameLabel = new JLabel("Lastname : "+lastname);
        deleteTeacherButton = new JButton("Delete");

        //Editing Members
        deleteTeacherButton.setBackground(Constant.RED);

        //Adding Listeners
        deleteTeacherButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Members to Panel
        add(firstnameLabel, Constraint.setPosition(0,0));
        add(lastnameLabel,Constraint.setPosition(1,0));
        add(deleteTeacherButton,Constraint.setPosition(2,0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == deleteTeacherButton ){
            try{
                db = new DatabaseCon();
                db.deleteTeacher(teacherId);
                parentPanel.displayTeacher();
            }catch (Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }
}
