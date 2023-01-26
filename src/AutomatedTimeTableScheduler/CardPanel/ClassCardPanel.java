package AutomatedTimeTableScheduler.CardPanel;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Frames.UpdateClassFrame;
import AutomatedTimeTableScheduler.Frames.UpdateCourseFrame;
import AutomatedTimeTableScheduler.Panels.ClassPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClassCardPanel extends JPanel implements ActionListener {

    private JLabel classLabel,divisionLabel;
    private JButton updateClassButton,deleteClassButton;
    private DatabaseCon db;
    private ClassPanel parentPanel;
    private int year;
    private String division;

    public ClassCardPanel(int year, String division, ClassPanel parentPanel){
        this.year = year;
        this.division = division;
        this.parentPanel = parentPanel;
        //Initialising Members
        String yearString = "";
        switch( year ){
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
        updateClassButton = new JButton("Update");
        deleteClassButton = new JButton("Delete");

        //Editing Members
        deleteClassButton.setBackground(Constant.RED);

        //Adding Listeners
        updateClassButton.addActionListener(this);
        deleteClassButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Members to Panel
        add(classLabel, Constraint.setPosition(0,0));
        add(divisionLabel,Constraint.setPosition(0,1));
        add(updateClassButton,Constraint.setPosition(1,0));
        add(deleteClassButton,Constraint.setPosition(1,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == updateClassButton ){
            getTopLevelAncestor().setVisible(false);
            JFrame updateClassFrame = new UpdateClassFrame(year,division);
            updateClassFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    updateClassFrame.dispose();
                    getTopLevelAncestor().setVisible(true);
                    parentPanel.displayClass();
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
        }if( e.getSource() == deleteClassButton ){
            int result = JOptionPane.showConfirmDialog(getTopLevelAncestor(),"Are you sure you want to delete Class?");

            if( result == JOptionPane.YES_NO_OPTION ){
                try{
                    db = new DatabaseCon();
                    db.deleteClass(year,division);
                    parentPanel.displayClass();
                }catch (Exception excp){
                    System.out.println(excp);
                }finally {
                    db.closeConnection();
                }
            }
        }
    }
}
