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
        }

        //Closing Connection With Database
        db.closeConnection();

        //Closing Document
        document.close();
    }

    private void addTableHeader(PdfPTable table) throws Exception{
    }
}
