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

public class ClassTimeTable {
    public static void createClassTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet resultSet = db.getClassList();

        while( resultSet.next() ){
            int year = resultSet.getInt("year");
            String division = resultSet.getString("division");
            String fileName = Constraint.getClassString(year,division);

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Class/"+fileName+".pdf"));
            document.open();

            //Writing Class Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(fileName,titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addClassTableHeader(table);

            for(int i = 0; i < Constant.WEEK.length; i++ ){
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(Constant.WEEK[i]));
                table.addCell(cell);

                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nTeacher\nClassroom"));
                    table.addCell(cell);
                }
            }
            //Adding Table
            document.add(table);

            //Closing Document
            document.close();
        }

        //Closing Connection With Database
        db.closeConnection();
    }

    private static void addClassTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Day / Time"));
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
