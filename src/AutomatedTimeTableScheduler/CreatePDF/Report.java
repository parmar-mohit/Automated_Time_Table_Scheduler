package AutomatedTimeTableScheduler.CreatePDF;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constraint;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.stream.Stream;

public class Report {

    public static void createReport() throws Exception{
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Report.pdf"));
        document.open();

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        //Adding Logo
        AddImage.addLogo(document);

        //Teacher Allocation Report
        Phrase teacherReportPhrase = new Phrase("Teacher Allocation Report",new Font(Font.FontFamily.TIMES_ROMAN,16.0f,Font.BOLD,BaseColor.BLACK));
        Paragraph teacherReportParagraph = new Paragraph(teacherReportPhrase);
        teacherReportParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(teacherReportParagraph);
        document.add(new Paragraph("\n\n"));

        PdfPTable table = new PdfPTable(new float[]{0.55f,0.15f,0.15f,0.15f});
        addTeacherAllocationTableHeader(table);

        ResultSet teacherResultSet = db.getTeacherList();
        int totalLoad = 0;
        while( teacherResultSet.next() ){
            String teacherName = Constraint.getFormattedText(teacherResultSet.getString("firstname"));
            teacherName += " " + Constraint.getFormattedText(teacherResultSet.getString("lastname"));

            PdfPCell cell = new PdfPCell(new Phrase(teacherName));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(db.getTeacherLectureLoad(teacherResultSet.getInt("teacher_id"))+""));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(db.getTeacherPracticalLoad(teacherResultSet.getInt("teacher_id"))+""));
            table.addCell(cell);

            int teacherTotalLoad = db.getTeacherLectureLoad(teacherResultSet.getInt("teacher_id")) + db.getTeacherPracticalLoad(teacherResultSet.getInt("teacher_id"));
            cell = new PdfPCell(new Phrase(teacherTotalLoad+""));
            table.addCell(cell);

            totalLoad += teacherTotalLoad;
        }
        PdfPCell cell = new PdfPCell(new Phrase("Average Load",new Font(Font.FontFamily.TIMES_ROMAN,15.0f,Font.BOLD,BaseColor.BLACK)));
        cell.setColspan(3);
        table.addCell(cell);

        float averageLoad = BigDecimal.valueOf((float)totalLoad/db.getTeacherCount())
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
        cell = new PdfPCell(new Phrase(averageLoad+""));
        table.addCell(cell);

        //Adding Table
        document.add(table);


        //Closing Connection With Database
        db.closeConnection();

        //Closing Document
        document.close();
    }

    private static void addTeacherAllocationTableHeader(PdfPTable table) throws Exception{
        Stream.of("Teacher Name","Lecture Load","Practical Load","Total Load")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }
}
