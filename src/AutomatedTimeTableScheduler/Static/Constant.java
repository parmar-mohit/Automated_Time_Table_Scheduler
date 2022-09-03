package AutomatedTimeTableScheduler.Static;

import java.awt.*;

public class Constant {
    //Program Constants
    public static final String APP_NAME = "Automated Time Table Scheduler";

    //Color Constants
    public static final Color FRAME_BACKGROUND = new Color(74, 224, 159);
    public static final Color BUTTON_PANEL_BACKGROUND = new Color(191, 203, 20);

    public static final Color BUTTON_BACKGROUND = new Color(255, 255, 255);
    public static final Color SELECTED_BUTTON = new Color(163, 198, 255);
    public static final Color PANEL_BACKGROUND = new Color(66, 135, 245);
    public static final Color TIME_SELECTOR_PANEL_BACKGROUND = new Color(39, 222, 195);

    //Size Constants
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final Dimension BUTTON_SIZE = new Dimension(200,25);

    //Days Constant
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String WEEK[] = {MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY};
}
