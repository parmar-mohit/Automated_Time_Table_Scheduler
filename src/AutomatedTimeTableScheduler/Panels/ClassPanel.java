package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.CardPanel.ClassCardPanel;
import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ClassPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel;
    private JButton createClassButton,backButton;
    private JPanel classListPanel;
    private JScrollPane scrollPane;
    private ArrayList<ClassCardPanel> classPanelArrayList;
    private CreateClassPanel createClassPanel;
    private DatabaseCon db;

    public ClassPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Class");
        Image img = new ImageIcon(Constant.ADD_ICON).getImage();
        img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        createClassButton = new JButton("Create Class",new ImageIcon(img));
        classListPanel = new JPanel();
        classPanelArrayList = new ArrayList<>();
        scrollPane = new JScrollPane(classListPanel);

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        createClassButton.setBackground(Constant.BUTTON_BACKGROUND);
        classListPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(1000,430));

        //Adding Listeners
        createClassButton.addActionListener(this);

        //Displaying Class Stored in Database
        displayClass();

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(createClassButton,Constraint.setPosition(1,1,Constraint.RIGHT));
        add(scrollPane,Constraint.setPosition(0,2,2,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == createClassButton ){
            //Initialising Variables
            backButton = new JButton("Back");
            createClassPanel = new CreateClassPanel();

            //Editing Members
            createClassPanel.setPreferredSize(new Dimension(1000,400));

            //Adding Listeners
            backButton.addActionListener(this);

            //Making Components Invisible
            createClassButton.setVisible(false);
            scrollPane.setVisible(false);

            //Adding newly created components
            add(backButton,Constraint.setPosition(1,1,Constraint.RIGHT));
            add(createClassPanel,Constraint.setPosition(0,2,2,1));
        }else if( e.getSource() == backButton ){
            //Removing Components
            remove(backButton);
            remove(createClassPanel);

            //Displaying Class Stored in Database
            displayClass();

            //Making Components Visible
            createClassButton.setVisible(true);
            scrollPane.setVisible(true);
        }
    }

    public void displayClass(){
        classPanelArrayList = new ArrayList<>();
        classListPanel.removeAll();

        try{
            db = new DatabaseCon();

            ResultSet classResultSet = db.getClassList();
            while( classResultSet.next() ){
                ClassCardPanel classCardPanel = new ClassCardPanel(classResultSet.getInt("year"),classResultSet.getString("division"),this);
                classCardPanel.setPreferredSize(new Dimension(950,75));
                classListPanel.add(classCardPanel,Constraint.setPosition(0,classPanelArrayList.size()));
                classPanelArrayList.add(classCardPanel);
                classListPanel.revalidate();
                classListPanel.repaint();
            }
        }catch(Exception e){
            System.out.println(e);
        }finally {
            db.closeConnection();
        }
    }
}
