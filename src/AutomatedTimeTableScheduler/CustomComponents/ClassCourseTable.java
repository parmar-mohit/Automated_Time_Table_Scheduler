package AutomatedTimeTableScheduler.CustomComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;

public class ClassCourseTable {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JScrollPane scrollPane;

    public ClassCourseTable() {
        String[] columns = {"Course Code", "Course Name", "Session Duration", "Session/Week","Selection"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 3;
            }
        };
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(950, 300));

        //Editing Table
        setTableColumnWidths();
        table.setDefaultEditor(Object.class, new TableEditor());
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
            tableModel.addRow(new Object[]{courseCode,courseName,sessionDuration,sessionPerWeek,false});
        }
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public int getRowCount() {
        return table.getRowCount();
    }

    public void reset(){
        for( int i = 0; i < table.getRowCount(); i++){
            table.setValueAt(false,i,4);
        }
    }

    public boolean getSelection(int row){
        return (boolean)table.getValueAt(row,4);
    }
    public Object getValueAt(int row, int column) {
        return table.getValueAt(row, column);
    }
}

class TableEditor extends DefaultCellEditor {
    public TableEditor() {
        super(new JCheckBox());
        setClickCountToStart(1);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        final JCheckBox cellEdit = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        table.setSurrendersFocusOnKeystroke(true);

        return cellEdit;
    }
}