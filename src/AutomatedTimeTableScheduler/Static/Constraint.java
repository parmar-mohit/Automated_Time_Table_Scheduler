package AutomatedTimeTableScheduler.Static;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Constraint {
    /* This Class provides static function that can be used to
    provide layout constraints for Gridbaglayout
    */

    public static final int LEFT = GridBagConstraints.WEST;
    public static final int RIGHT = GridBagConstraints.EAST;
    public static final int CENTER = GridBagConstraints.CENTER;

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

    public static String getFormattedText(String text){
        String[] words = text.split(" ");
        String formattedText = "";

        for( int i = 0; i < words.length; i++){
            if( !formattedText.equals("") ){
                formattedText += " ";
            }
            formattedText += Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1).toLowerCase();
        }

        return formattedText;
    }

    public static String getClassString(int year,String division){
        String classString;
        switch(year){
            case 1:
                classString = "FE";
                break;

            case 2:
                classString = "SE";
                break;

            case 3:
                classString = "TE";
                break;

            case 4:
                classString = "BE";
                break;

            default:
                classString = "Year";
        }
        classString += " " + division;

        return classString;
    }

    public static void deleteAllFileFromDirectory(File dir) {
        for (File file: dir.listFiles()) {
            file.delete();
        }
    }
}

