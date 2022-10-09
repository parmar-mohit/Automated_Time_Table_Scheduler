package AutomatedTimeTableScheduler.CreatePanels;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateClassPanel extends JPanel implements ActionListener {

    private JLabel panelNameLabel,yearLabel,divisionLabel,messageLabel;
    private JComboBox yearComboBox,divisionComboBox;
    private JButton createClassButton;
    private DatabaseCon db;

    public CreateClassPanel(){
        //Initialising Member
        panelNameLabel = new JLabel("Create Class");
        yearLabel = new JLabel("Year : ");
        yearComboBox = new JComboBox(new Object[]{1,2,3,4});
        divisionLabel = new JLabel("Division : ");
        divisionComboBox = new JComboBox(new Object[]{"A","B","C","D"});
        messageLabel = new JLabel();
        createClassButton = new JButton("Create Class");

        //Editing Member Variables
        panelNameLabel.setFont(new Font("SansSerif",Font.PLAIN,18));

        //Adding Listener
        createClassButton.addActionListener(this);

        //Editing Panel
        setLayout(new GridBagLayout());
        setBackground(Constant.PANEL_BACKGROUND);

        //Adding Member to Panel
        add(panelNameLabel, Constraint.setPosition(0,0,4,1));
        add(yearLabel,Constraint.setPosition(0,1,Constraint.RIGHT));
        add(yearComboBox,Constraint.setPosition(1,1,Constraint.LEFT));
        add(divisionLabel,Constraint.setPosition(2,1,Constraint.RIGHT));
        add(divisionComboBox,Constraint.setPosition(3,1,Constraint.LEFT));
        add(messageLabel,Constraint.setPosition(0,2,4,1));
        add(createClassButton,Constraint.setPosition(0,3,4,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == createClassButton ){
            if( yearComboBox.getSelectedItem() == null ){
                messageLabel.setText("Select Year");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            int year = (int)yearComboBox.getSelectedItem();

            if( divisionComboBox.getSelectedItem() == null ){
                messageLabel.setText("Select Division");
                Constraint.labelDeleteAfterTime(messageLabel);
                return;
            }
            String division = divisionComboBox.getSelectedItem().toString();

            try{
                db = new DatabaseCon();

                if( db.checkClassExist(year,division) ){
                    messageLabel.setText("Class with same year and division already exist");
                    Constraint.labelDeleteAfterTime(messageLabel);
                    return;
                }

                db.createClass(year,division);
                messageLabel.setText("Class Created");
                Constraint.labelDeleteAfterTime(messageLabel);
            }catch (Exception excp){
                System.out.println(excp);
            }finally {
                db.closeConnection();
            }
        }
    }
}
