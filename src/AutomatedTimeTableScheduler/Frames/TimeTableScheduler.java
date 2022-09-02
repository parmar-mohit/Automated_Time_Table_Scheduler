package AutomatedTimeTableScheduler.Frames;

import AutomatedTimeTableScheduler.Static.Constant;

import javax.swing.*;
import java.awt.*;

public class TimeTableScheduler extends JFrame {

    public TimeTableScheduler(){
        //Frame Details
        setTitle(Constant.APP_NAME);
        setSize(Constant.SCREEN_SIZE);
        setLayout(new GridBagLayout());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Constant.FRAME_BACKGROUND);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
