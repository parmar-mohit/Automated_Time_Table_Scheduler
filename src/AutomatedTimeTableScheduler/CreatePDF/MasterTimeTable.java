package AutomatedTimeTableScheduler.CreatePDF;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constant;
import AutomatedTimeTableScheduler.Static.Constraint;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class MasterTimeTable {
    public static void createMasterTimeTable() throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Time Table.pdf"));
        document.open();

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        for(int i = 0; i < Constant.WEEK.length; i++ ) {
            if( i != 0 ){
                document.newPage();
            }

            //Writing Day of Week in Title
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
            Phrase titlePhrase = new Phrase("Time Table : "+Constant.WEEK[i],titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addMasterTableHeader(table);  //adding header to tables

            ResultSet classResultSet = db.getClassList();
            while( classResultSet.next() ){
                int year = classResultSet.getInt("year");
                String division = classResultSet.getString("division");

                String className = Constraint.getClassString(year,division);

                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(className));
                table.addCell(cell);

                //Adding Cells for TimeSlots
                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nTeacher\nClassroom"));
                    table.addCell(cell);
                }
            }

            //Adding Table to Document
            document.add(table);
        }

        //Closing Connection With Database
        db.closeConnection();

        //Closing Document
        document.close();
    }

    private static void addMasterTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Class / Time"));
        table.addCell(header);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        ResultSet timeResultSet = db.getTimeList();
        while(timeResultSet.next()){
            String timeSlot = sdfTime.format(timeResultSet.getTime("start_time")) + "-" + sdfTime.format(timeResultSet.getTime("end_time"));
            header = new PdfPCell();
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);
        }
        db.closeConnection();
    }
}
