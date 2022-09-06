package AutomatedTimeTableScheduler.Static;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class CreatePDF extends Thread {

    @Override
    public void run() {
        try{
            createTimeTable();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void createTimeTable() throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document,new FileOutputStream("Time Table.pdf"));
        document.open();

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        for( int i = 0; i < Constant.WEEK.length; i++ ) {
            if( i != 0 ){
                document.newPage();
            }

            //Writing Day of Week in Title
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD,BaseColor.BLACK);
            Phrase titlePhrase = new Phrase("Time Table : "+Constant.WEEK[i],titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount() + 1); //Creating Table
            addTableHeader(table);  //adding header to tables

            ResultSet classResultSet = db.getClassList();
            while( classResultSet.next() ){
                int year = classResultSet.getInt("year");
                String division = classResultSet.getString("division");

                String className = new String();
                switch ( year ){
                    case 1:
                        className = "FE";
                        break;

                    case 2:
                        className = "SE";
                        break;

                    case 3:
                        className = "TE";
                        break;

                    case 4:
                        className = "BE";
                        break;
                }
                className += " "+division;

                table.addCell(className);

                //Adding Cells for TimeSlots
                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    table.addCell("");
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

    private void addTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Class / Time"));
        table.addCell(header);

        ResultSet timeSlotResultSet = db.getTimeSlots();

        while( timeSlotResultSet.next() ){
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String timeSlot = sdfTime.format(timeSlotResultSet.getTime("start_time"));
            timeSlot += " - ";
            timeSlot += sdfTime.format(timeSlotResultSet.getTime("end_time"));

            header = new PdfPCell();
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);
        }

        //Closing Connection with Database
        db.closeConnection();
    }
}
