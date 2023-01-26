package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Frames.UpdateTeacherFrame;
import AutomatedTimeTableScheduler.Panels.TeacherPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class TeacherCardPanel extends JPanel implements ActionListener  {

    private JLabel firstnameLabel,lastnameLabel;
    private JButton updateTeacherButton,deleteTeacherButton;
    private DatabaseCon db;
    private TeacherPanel parentPanel;
    private int teacherId;
    private String firstname,lastname;

    public TeacherCardPanel(String firstname,String lastname,int teacherId,TeacherPanel parent){
        this.firstname = firstname;
        this.lastname = lastname;
        this.teacherId = teacherId;
        this.parentPanel = parent;
        //Initialising Member Variables
        firstnameLabel = new JLabel("Firstname : "+firstname);
        lastnameLabel = new JLabel("Lastname : "+lastname);
        updateTeacherButton = new JButton("Update");
        deleteTeacherButton = new JButton("Delete");

        //Editing Members
        deleteTeacherButton.setBackground(Constant.RED);

        //Adding Listeners
        updateTeacherButton.addActionListener(this);
        deleteTeacherButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Members to Panel
        add(firstnameLabel, Constraint.setPosition(0,0));
        add(lastnameLabel,Constraint.setPosition(0,1));
        add(updateTeacherButton,Constraint.setPosition(1,0));
        add(deleteTeacherButton,Constraint.setPosition(1,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == updateTeacherButton ){
            getTopLevelAncestor().setVisible(false);
            JFrame updateTeacherFrame = new UpdateTeacherFrame(firstname,lastname,teacherId);
            updateTeacherFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    updateTeacherFrame.dispose();
                    getTopLevelAncestor().setVisible(true);
                    parentPanel.displayTeacher();
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
        }else if( e.getSource() == deleteTeacherButton ){
            int result = JOptionPane.showConfirmDialog(getTopLevelAncestor(),"Are you sure you want to delete course?");

            if( result == JOptionPane.YES_NO_OPTION ){
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
}
