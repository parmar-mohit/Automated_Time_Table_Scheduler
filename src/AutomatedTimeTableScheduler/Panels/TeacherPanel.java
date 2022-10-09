package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CardPanel.TeacherCardPanel;
import AutomatedTimeTableScheduler.CreatePanels.AddTeacherPanel;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TeacherPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel;
    private JButton addTeacherButton,backButton;
    private JPanel teacherListPanel;
    private JScrollPane scrollPane;
    private ArrayList<TeacherCardPanel> teacherPanelArrayList;
    private AddTeacherPanel addTeacherPanel;
    private DatabaseCon db;

    public TeacherPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Teacher");
        Image img = new ImageIcon(Constant.ADD_ICON).getImage();
        img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        addTeacherButton = new JButton("Add Teacher",new ImageIcon(img));
        teacherListPanel = new JPanel();
        teacherPanelArrayList = new ArrayList<>();
        scrollPane = new JScrollPane(teacherListPanel);

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        addTeacherButton.setBackground(Constant.BUTTON_BACKGROUND);
        teacherListPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(1000,430));

        //Adding Listeners
        addTeacherButton.addActionListener(this);

        //Displaying Class Stored in Database
        displayTeacher();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(addTeacherButton,Constraint.setPosition(1,1,Constraint.RIGHT));
        add(scrollPane,Constraint.setPosition(0,2,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == addTeacherButton){
            //Initialising Variables
            backButton = new JButton("Back");
            addTeacherPanel = new AddTeacherPanel();

            //Editing Members
            addTeacherPanel.setPreferredSize(new Dimension(1000,400));

            //Adding Listeners
            backButton.addActionListener(this);

            //Making Components Invisible
            addTeacherButton.setVisible(false);
            scrollPane.setVisible(false);

            //Adding newly created components
            add(backButton,Constraint.setPosition(1,1,Constraint.RIGHT));
            add(addTeacherPanel,Constraint.setPosition(0,2,2,1));
        }else if( e.getSource() == backButton ){
            //Removing Components
            remove(backButton);
            remove(addTeacherPanel);

            //Displaying Class Stored in Database
            displayTeacher();

            //Making Components Visible
            addTeacherButton.setVisible(true);

            scrollPane.setVisible(true);
        }
    }

    public void displayTeacher(){
        teacherPanelArrayList = new ArrayList<>();
        teacherListPanel.removeAll();
        teacherListPanel.revalidate();
        teacherListPanel.repaint();

        try{
            db = new DatabaseCon();

            ResultSet teacherResultSet = db.getTeacherList();
            while( teacherResultSet.next() ){
                int teacherId = teacherResultSet.getInt("teacher_id");
                String firstname = teacherResultSet.getString("firstname");
                String lastname = teacherResultSet.getString("lastname");
                TeacherCardPanel teacherCardPanel = new TeacherCardPanel(firstname,lastname,teacherId,this);
                teacherCardPanel.setPreferredSize(new Dimension(950,75));
                teacherListPanel.add(teacherCardPanel,Constraint.setPosition(0, teacherPanelArrayList.size()));
                teacherPanelArrayList.add(teacherCardPanel);
                teacherListPanel.revalidate();
                teacherListPanel.repaint();
            }
        }catch(Exception e){
            System.out.println(e);
        }finally {
            db.closeConnection();
        }
    }
}
