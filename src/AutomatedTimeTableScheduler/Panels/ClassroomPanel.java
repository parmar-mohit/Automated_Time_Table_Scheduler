package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CardPanel.ClassroomCardPanel;
import AutomatedTimeTableScheduler.CreatePanels.AddClassroomPanel;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ClassroomPanel extends JPanel implements ActionListener {
    private JLabel panelNameLabel;
    private JButton addClassroomButton,backButton;
    private JPanel classroomListPanel;
    private JScrollPane scrollPane;
    private ArrayList<ClassroomCardPanel> classroomPanelArrayList;
    private AddClassroomPanel addClassroomPanel;
    private DatabaseCon db;

    public ClassroomPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Classroom");
        Image img = new ImageIcon(Constant.ADD_ICON).getImage();
        img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        addClassroomButton = new JButton("Add Classroom",new ImageIcon(img));
        classroomListPanel = new JPanel();
        classroomPanelArrayList = new ArrayList<>();
        scrollPane = new JScrollPane(classroomListPanel);

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        addClassroomButton.setBackground(Constant.BUTTON_BACKGROUND);
        classroomListPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(1000,550));

        //Adding Listeners
        addClassroomButton.addActionListener(this);

        //Displaying Class Stored in Database
        displayClassroom();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(addClassroomButton,Constraint.setPosition(1,1,Constraint.RIGHT));
        add(scrollPane,Constraint.setPosition(0,2,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == addClassroomButton){
            //Initialising Variables
            backButton = new JButton("Back");
            addClassroomPanel = new AddClassroomPanel();

            //Editing Members
            addClassroomPanel.setPreferredSize(new Dimension(1000,550));

            //Adding Listeners
            backButton.addActionListener(this);

            //Making Components Invisible
            addClassroomButton.setVisible(false);
            scrollPane.setVisible(false);

            //Adding newly created components
            add(backButton,Constraint.setPosition(1,1,Constraint.RIGHT));
            add(addClassroomPanel,Constraint.setPosition(0,2,2,1));
        }else if( e.getSource() == backButton ){
            //Removing Components
            remove(backButton);
            remove(addClassroomPanel);

            //Displaying Class Stored in Database
            displayClassroom();

            //Making Components Visible
            addClassroomButton.setVisible(true);
            scrollPane.setVisible(true);
        }
    }

    public void displayClassroom(){
        classroomPanelArrayList = new ArrayList<>();
        classroomListPanel.removeAll();
        classroomListPanel.revalidate();
        classroomListPanel.repaint();

        try{
            db = new DatabaseCon();

            ResultSet classroomResultSet = db.getClassroomList();
            while( classroomResultSet.next() ){
                String roomName = classroomResultSet.getString("room_name");
                int roomId = classroomResultSet.getInt("room_id");
                ClassroomCardPanel classroomCardPanel = new ClassroomCardPanel(roomName,roomId,this);
                classroomCardPanel.setPreferredSize(new Dimension(950,75));
                classroomListPanel.add(classroomCardPanel,Constraint.setPosition(0, classroomPanelArrayList.size()));
                classroomPanelArrayList.add(classroomCardPanel);
                classroomListPanel.revalidate();
                classroomListPanel.repaint();
            }
        }catch(Exception e){
            System.out.println(e);
        }finally {
            db.closeConnection();
        }
    }

}
