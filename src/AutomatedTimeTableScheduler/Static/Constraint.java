package AutomatedTimeTableScheduler.Static;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constraint {
    /* This Class provides static function that can be used to
    provide layout constraints for Gridbaglayout
    */

    public static final int LEFT = GridBagConstraints.WEST;
    public static final int RIGHT = GridBagConstraints.EAST;

    public static GridBagConstraints setPosition(int x, int y) {
        return new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5);
    }

    public static GridBagConstraints setPosition(int x, int y, int xwidth, int ywidth) {
        return new GridBagConstraints(x, y, xwidth, ywidth, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5);
    }

    public static GridBagConstraints setPosition(int x, int y, int xwidth, int ywidth, int anchor) {
        return new GridBagConstraints(x, y, xwidth, ywidth, 0, 0, anchor, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5);
    }

    public static GridBagConstraints setPosition(int x, int y, int anchor) {
        return new GridBagConstraints(x, y, 1, 1, 0, 0, anchor, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5);
    }

    public static void labelDeleteAfterTime(JLabel label) {
        Timer t = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText(null);
            }
        });
        t.setRepeats(false);
        t.start();
    }
}

