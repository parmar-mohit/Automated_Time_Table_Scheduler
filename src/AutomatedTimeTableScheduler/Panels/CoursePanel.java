package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CardPanel.CourseCardPanel;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CoursePanel extends JPanel implements ActionListener {
    private JLabel panelNameLabel;
    private JButton createCourseButton,backButton;
    private JPanel courseListPanel;
    private JScrollPane scrollPane;
    private ArrayList<CourseCardPanel> coursePanelArrayList;
    private CreateCoursePanel createCoursePanel;
    private DatabaseCon db;

    public CoursePanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Course");
        Image img = new ImageIcon(Constant.ADD_ICON).getImage();
        img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        createCourseButton = new JButton("Create Course",new ImageIcon(img));
        courseListPanel = new JPanel();
        coursePanelArrayList = new ArrayList<>();
        scrollPane = new JScrollPane(courseListPanel);

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        createCourseButton.setBackground(Constant.BUTTON_BACKGROUND);
        courseListPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(1000,430));

        //Adding Listeners
        createCourseButton.addActionListener(this);

        //Displaying Class Stored in Database
        displayCourse();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(createCourseButton,Constraint.setPosition(1,1,Constraint.RIGHT));
        add(scrollPane,Constraint.setPosition(0,2,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == createCourseButton ){
            //Initialising Variables
            backButton = new JButton("Back");
            createCoursePanel = new CreateCoursePanel();

            //Editing Members
            createCoursePanel.setPreferredSize(new Dimension(1000,400));

            //Adding Listeners
            backButton.addActionListener(this);

            //Making Components Invisible
            createCourseButton.setVisible(false);
            scrollPane.setVisible(false);

            //Adding newly created components
            add(backButton,Constraint.setPosition(1,1,Constraint.RIGHT));
            add(createCoursePanel,Constraint.setPosition(0,2,2,1));
        }else if( e.getSource() == backButton ){
            //Removing Components
            remove(backButton);
            remove(createCoursePanel);

            //Displaying Class Stored in Database
            displayCourse();

            //Making Components Visible
            createCourseButton.setVisible(true);
            scrollPane.setVisible(true);
        }
    }

    public void displayCourse(){
        coursePanelArrayList = new ArrayList<>();
        courseListPanel.removeAll();

        try{
            db = new DatabaseCon();

            ResultSet courseResultSet = db.getCourseList();
            while( courseResultSet.next() ){
                String courseCode = courseResultSet.getString(1);
                String courseName = courseResultSet.getString(2);
                int sessionPerWeek = courseResultSet.getInt(3);
                int sessionDuration = courseResultSet.getInt(4);
                CourseCardPanel courseCardPanel = new CourseCardPanel(courseCode,courseName,sessionDuration,sessionPerWeek,this);
                courseCardPanel.setPreferredSize(new Dimension(950,150));
                courseListPanel.add(courseCardPanel,Constraint.setPosition(0,coursePanelArrayList.size()));
                coursePanelArrayList.add(courseCardPanel);
                courseListPanel.revalidate();
                courseListPanel.repaint();
            }
        }catch(Exception e){
            System.out.println(e);
        }finally {
            db.closeConnection();
        }
    }
}
