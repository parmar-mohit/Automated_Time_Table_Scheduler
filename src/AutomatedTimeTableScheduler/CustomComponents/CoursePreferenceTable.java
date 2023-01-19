package AutomatedTimeTableScheduler.CustomComponents;

import com.mysql.cj.xdevapi.JsonArray;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.Dictionary;
import java.util.Hashtable;

public class CoursePreferenceTable implements MouseListener {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JScrollPane scrollPane;

    private int preference;

    public CoursePreferenceTable() {
        preference = 1;
        String[] columns = {"Course Code", "Course Name", "Session Duration", "Session/Week","Selection"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel){
            @Override
            public boolean isCellSelected(int row, int column) {
                return false;
            }
        };
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(950, 300));

        //Editing Table
        setTableColumnWidths();
        table.addMouseListener(this);
    }

    private void setTableColumnWidths() {
        //Editing Table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
    }

    public void setCourseList(ResultSet resultSet) throws Exception{
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        while(resultSet.next()){
            String courseCode = resultSet.getString("course_code");
            String courseName = resultSet.getString("course_name");
            int sessionDuration = resultSet.getInt("session_duration");
            int sessionPerWeek = resultSet.getInt("session_per_week");
            tableModel.addRow(new Object[]{courseCode,courseName,sessionDuration,sessionPerWeek});
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JTable target = (JTable)e.getSource();
        int row = target.getSelectedRow();
        int column = target.getSelectedColumn();

        if( column != 4 ){
            return;
        }


        if( table.getValueAt(row,column) == null ){
            table.setValueAt(preference, row, column);
            preference++;
        }else{
            int value = (int)table.getValueAt(row,column);

            for( int i = 0; i < table.getRowCount(); i++){
                if( table.getValueAt(i,4) == null ){
                    continue;
                }
                if( (int)table.getValueAt(i,4) > value ){
                    table.setValueAt(((int)table.getValueAt(i,4))-1,i,4);
                }
            }

            table.setValueAt(null,row,column);
            preference--;
        }
    }

    public Dictionary<String,Integer> getPrefenceList(){
        Dictionary<String,Integer> preferenceList = new Hashtable<>();

        for( int i = 0; i < table.getRowCount(); i++){
            if(table.getValueAt(i,4) == null ){
                return null;
            }else{
                int value = (int)table.getValueAt(i,4);
                String courseCode = (String)table.getValueAt(i,0);

                preferenceList.put(courseCode,value);
            }
        }

        return preferenceList;
    }

    public void resetPreferences(){
        for( int i = 0; i < table.getRowCount(); i++){
            table.setValueAt(null,i,4);
        }
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
