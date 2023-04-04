package AutomatedTimeTableScheduler.CreatePDF;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.sql.ResultSet;
import java.util.stream.Stream;

public class AddResources {
    public static void addLogo(Document document) throws Exception{
        Image logo = Image.getInstance("./Images/College Logo.png");
        logo.setAlignment(Element.ALIGN_CENTER);
        logo.scaleAbsoluteWidth(500);
        logo.scaleAbsoluteHeight(50);
        document.add(logo);
    }

    public static void addCourseAbbreviationPage(Document document) throws Exception{
        DatabaseCon db = new DatabaseCon();

        // New Page for Course Abbreviations
        document.setPageSize(PageSize.A4);
        document.newPage();

        //Adding Logo
        addLogo(document);

        //Title
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
        Phrase titlePhrase = new Phrase("Course Name Abbreviations",titleFont);
        Paragraph title = new Paragraph(titlePhrase);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n\n"));

        //Creating Course Abbreviation Table
        PdfPTable table = new PdfPTable(new float[]{0.18f,0.64f,0.18f});
        addCourseAbbreviationTableHeader(table);

        ResultSet courseResultSet = db.getCourseList();
        while( courseResultSet.next() ){
            PdfPCell cell = new PdfPCell();

            cell.setPhrase(new Phrase(courseResultSet.getString("course_code")));
            table.addCell(cell);

            cell.setPhrase(new Phrase(courseResultSet.getString("course_name")));
            table.addCell(cell);

            cell.setPhrase(new Phrase(courseResultSet.getString("c_abbreviation")));
            table.addCell(cell);
        }

        //Adding Table
        document.add(table);

        //Closing Database Connection
        db.closeConnection();
    }

    private static void addCourseAbbreviationTableHeader(PdfPTable table) throws Exception{
        Stream.of("Course Code", "Course Name", "Abbreviation")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    public static void addTeacherAbbreviationPage(Document document) throws Exception{
        DatabaseCon db = new DatabaseCon();

        // New Page for Teacher Abbreviations
        document.setPageSize(PageSize.A4);
        document.newPage();

        //Adding Logo
        addLogo(document);

        //Title
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
        Phrase titlePhrase = new Phrase("Profesor Name Abbreviations",titleFont);
        Paragraph title = new Paragraph(titlePhrase);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n\n"));

        //Creating Course Abbreviation Table
        PdfPTable table = new PdfPTable(2);
        addTeacherAbbreviationTableHeader(table);

        ResultSet teacherResultSet = db.getTeacherList();
        while( teacherResultSet.next() ){
            PdfPCell cell = new PdfPCell();

            cell.setPhrase(new Phrase(teacherResultSet.getString("firstname")+" "+teacherResultSet.getString("lastname")));
            table.addCell(cell);

            cell.setPhrase(new Phrase(teacherResultSet.getString("t_abbreviation")));
            table.addCell(cell);
        }

        //Adding Table
        document.add(table);

        //Closing Database Connection
        db.closeConnection();
    }

    private static void addTeacherAbbreviationTableHeader(PdfPTable table) throws Exception{
        Stream.of("Name", "Abbreviation")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }
}
