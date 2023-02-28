package AutomatedTimeTableScheduler.CreatePDF;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import AutomatedTimeTableScheduler.Static.Constraint;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.stream.Stream;

public class SubjectAllocation {
    public static void createSubjectAllocationSheet() throws Exception{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Subject Allocation.pdf"));
        document.open();

        //Adding Logo
        AddImage.addLogo(document);

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        //Writing Header
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
        Phrase titlePhrase = new Phrase("Subject Allocation",titleFont);
        Paragraph title = new Paragraph(titlePhrase);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n\n"));

        ResultSet classResultSet = db.getClassList();
        while( classResultSet.next() ){
            String classString  = Constraint.getClassString(classResultSet.getInt("year"),classResultSet.getString("division"));
            Phrase yearPhrase = new Phrase(classString,new Font(Font.FontFamily.TIMES_ROMAN,20.0f,Font.BOLD, BaseColor.BLACK));
            Paragraph yearParagraph = new Paragraph(yearPhrase);
            yearParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(yearParagraph);
            document.add(new Paragraph("\n\n"));

            //Lecture Table
            PdfPTable lectureTable = new PdfPTable(new float[]{0.1f,0.45f,0.45f});
            lectureTable.setWidthPercentage(100);
            addLectureTableHeader(lectureTable);

            ResultSet lectureResultSet = db.getLectureSubjectAllocationForClass(classResultSet.getInt("class_id"));
            while( lectureResultSet.next() ){
                PdfPCell cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_code")));
                lectureTable.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_name")));
                lectureTable.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getString("firstname")+" "+lectureResultSet.getString("lastname")));
                lectureTable.addCell(cell);
            }
            document.add(lectureTable);

            Paragraph seperatorParagraph = new Paragraph("\n--------------------\n\n");
            seperatorParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(seperatorParagraph);

            //Practical Table
            PdfPTable practicalTable = new PdfPTable(new float[]{0.1f,0.4f,0.1f,0.4f});
            practicalTable.setWidthPercentage(100);
            addPracticalTableHeader(practicalTable);

            ResultSet practicalResultSet = db.getPracticalSubjectAllocationForClass(classResultSet.getInt("class_id"));
            while( practicalResultSet.next() ){
                PdfPCell cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_code")));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_name")));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getInt("batch")+""));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getString("firstname")+" "+practicalResultSet.getString("lastname")));
                practicalTable.addCell(cell);
            }
            document.add(practicalTable);

            if( !classResultSet.isLast() ){
                document.newPage();
            }
        }

        //Closing Connection With Database
        db.closeConnection();

        //Closing File
        document.close();
    }

    private static void addLectureTableHeader(PdfPTable table){
        PdfPCell lectureCell = new PdfPCell();
        lectureCell.setBackgroundColor(new BaseColor(66,135,245));
        lectureCell.setPhrase(new Phrase("Lectures",new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
        lectureCell.setColspan(3);
        lectureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(lectureCell);
        Stream.of("Course Code","Course Name","Alloted Teacher")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }

    private static void addPracticalTableHeader(PdfPTable table){
        PdfPCell practicalCell = new PdfPCell();
        practicalCell.setBackgroundColor(new BaseColor(66,135,245));
        practicalCell.setPhrase(new Phrase("Practicals",new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
        practicalCell.setColspan(4);
        practicalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(practicalCell);
        Stream.of("Course Code","Course Name","Batch","Alloted Teacher")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }
}
