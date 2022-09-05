package AutomatedTimeTableScheduler.Panels;

import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;

public class ClassPanel extends JPanel {

    private JLabel panelNameLabel;
    private JButton createClassButton;
    private JPanel classListPanel;
    private JScrollPane scrollPane;

    public ClassPanel(){
        //Initialising Member Variables
        panelNameLabel = new JLabel("Class");
        Image img = new ImageIcon(Constant.ADD_ICON).getImage();
        img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        createClassButton = new JButton("Create Class",new ImageIcon(img));
        classListPanel = new JPanel();
        scrollPane = new JScrollPane(classListPanel);

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif", Font.PLAIN,20));
        createClassButton.setBackground(Constant.BUTTON_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(1000,430));

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,2,1));
        add(createClassButton,Constraint.setPosition(1,1,Constraint.RIGHT));
        add(scrollPane,Constraint.setPosition(0,2,2,1));
    }
}
