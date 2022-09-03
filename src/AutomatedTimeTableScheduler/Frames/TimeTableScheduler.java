package AutomatedTimeTableScheduler.Frames;

import AutomatedTimeTableScheduler.Panels.TimeSlotsPanel;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeTableScheduler extends JFrame implements ActionListener {

    private JLabel appNameLabel;
    private ButtonPanel buttonPanel;
    private JPanel optionPanel;

    public TimeTableScheduler(){
        //Initialising Variables
        appNameLabel = new JLabel(Constant.APP_NAME);
        buttonPanel = new ButtonPanel();

        //Editing Members
        appNameLabel.setFont(new Font("Times New Roman",Font.BOLD,26));
        appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appNameLabel.setMinimumSize(new Dimension(Constant.SCREEN_SIZE.width, Constant.SCREEN_SIZE.height / 5));
        appNameLabel.setPreferredSize(new Dimension(Constant.SCREEN_SIZE.width, Constant.SCREEN_SIZE.height / 5));
        buttonPanel.setMinimumSize(new Dimension(Constant.SCREEN_SIZE.width / 5, Constant.SCREEN_SIZE.height * 4 / 5));
        buttonPanel.setPreferredSize(new Dimension(Constant.SCREEN_SIZE.width / 5, Constant.SCREEN_SIZE.height * 4 / 5));

        //Adding Listeners
        buttonPanel.timeSlotsButton.addActionListener(this);

        //Frame Details
        setTitle(Constant.APP_NAME);
        setSize(Constant.SCREEN_SIZE);
        setLayout(new GridBagLayout());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Constant.FRAME_BACKGROUND);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //Adding Members to Frame
        add(appNameLabel,Constraint.setPosition(0,0,2,1));
        add(buttonPanel,Constraint.setPosition(0,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (optionPanel != null) {
            remove(optionPanel);
        }

        if (e.getSource() == buttonPanel.timeSlotsButton) {
            optionPanel = new TimeSlotsPanel();
        }

        //Coloring Buttons
        buttonPanel.timeSlotsButton.setBackground(Constant.BUTTON_BACKGROUND);

        JButton buttonClicked = (JButton) e.getSource();
        buttonClicked.setBackground(Constant.SELECTED_BUTTON);

        optionPanel.setMinimumSize(new Dimension(Constant.SCREEN_SIZE.width * 4 / 5, Constant.SCREEN_SIZE.height * 4 / 5));
        optionPanel.setPreferredSize(new Dimension(Constant.SCREEN_SIZE.width * 4 / 5, Constant.SCREEN_SIZE.height * 4 / 5));
        add(optionPanel, Constraint.setPosition(1, 1));
        optionPanel.setVisible(true);
        revalidate();
        repaint();
    }
}

class ButtonPanel extends JPanel{

    public JButton timeSlotsButton;

    public ButtonPanel(){
        //Initialising Variables
        timeSlotsButton = new JButton("Time Slots");

        //Editing Components
        timeSlotsButton.setPreferredSize(Constant.BUTTON_SIZE);
        timeSlotsButton.setBackground(Constant.BUTTON_BACKGROUND);

        //Panel Details
        setLayout(new GridBagLayout());
        setBackground(Constant.BUTTON_PANEL_BACKGROUND);

        //Adding Member to Panel
        add(timeSlotsButton, Constraint.setPosition(0,0));
    }
}
