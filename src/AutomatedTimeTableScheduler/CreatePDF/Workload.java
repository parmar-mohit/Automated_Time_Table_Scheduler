package AutomatedTimeTableScheduler.CreatePDF;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.stream.Stream;

public class Workload {
    public static void createWorkLoadSheet() throws Exception{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/WorkLoad.pdf"));
        document.open();

        //Adding Logo
        AddResources.addLogo(document);

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        //Writing Header
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
        Phrase titlePhrase = new Phrase("WorkLoad",titleFont);
        Paragraph title = new Paragraph(titlePhrase);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n\n"));

        PdfPTable table = new PdfPTable(new float[]{0.05f,0.13f,0.47f,0.09f,0.08f,0.08f,0.09f});
        table.setWidthPercentage(100);
        addWorkLoadTableHeader(table);

        int totalLoad = 0;

        ResultSet yearResultSet = db.getDistinctYearList();
        while( yearResultSet.next() ){
            int year  = yearResultSet.getInt(1);

            String yearString;
            switch(year){
                case 1:
                    yearString = "FE";
                    break;

                case 2:
                    yearString = "SE";
                    break;

                case 3:
                    yearString = "TE";
                    break;

                case 4:
                    yearString = "BE";
                    break;

                default:
                    yearString = "Year";
            }
            PdfPCell cell = new PdfPCell(new Phrase(yearString));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setRowspan(db.getDistinctCourseCountForYear(year)+4);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Lectures") );
            cell.setRowspan(1);
            cell.setColspan(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int yearLoad = 0;

            ResultSet lectureResultSet = db.getDistinctLectureCourseListForYear(year);
            while( lectureResultSet.next() ){
                cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_code")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_name")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getInt("session_duration")+""));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getInt("session_per_week")+""));
                table.addCell(cell);

                int divisionCount = db.getDivisionCountForCourseYear(lectureResultSet.getString("course_code"),year);
                cell = new PdfPCell(new Phrase(divisionCount+""));
                table.addCell(cell);

                int courseLoad = lectureResultSet.getInt("session_duration") * lectureResultSet.getInt("session_per_week") * divisionCount;
                cell = new PdfPCell(new Phrase(courseLoad+""));
                table.addCell(cell);

                yearLoad += courseLoad;
            }

            cell = new PdfPCell(new Phrase("Practical") );
            cell.setRowspan(1);
            cell.setColspan(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            ResultSet practicalResultSet = db.getDistinctPracticalCourseListForYear(year);
            while( practicalResultSet.next() ){
                cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_code")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_name")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getInt("session_duration")+""));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getInt("session_per_week")+""));
                table.addCell(cell);

                int divisionCount = db.getDivisionCountForCourseYear(practicalResultSet.getString("course_code"),year);
                cell = new PdfPCell(new Phrase(divisionCount+""));
                table.addCell(cell);

                int courseLoad = ( practicalResultSet.getInt("session_duration") * 4 ) * practicalResultSet.getInt("session_per_week") * divisionCount;
                cell = new PdfPCell(new Phrase(courseLoad+""));
                table.addCell(cell);

                yearLoad += courseLoad;
            }

            cell = new PdfPCell(new Phrase(" ") );
            cell.setRowspan(1);
            cell.setColspan(6);
            table.addCell(cell);

            Phrase yearLoadPhrase = new Phrase("Year Load",new Font(Font.FontFamily.COURIER,12.0f,Font.BOLD,BaseColor.BLACK));
            PdfPCell yearLoadCell = new PdfPCell(yearLoadPhrase);
            yearLoadCell.setColspan(5);
            table.addCell(yearLoadCell);

            yearLoadPhrase = new Phrase(yearLoad+"",new Font(Font.FontFamily.COURIER,12.0f,Font.BOLD,BaseColor.BLACK));
            yearLoadCell = new PdfPCell(yearLoadPhrase);
            table.addCell(yearLoadCell);

            PdfPCell blankCell = new PdfPCell(new Phrase(" "));
            blankCell.setColspan(7);
            table.addCell(blankCell);

            totalLoad += yearLoad;
        }

        Phrase totalLoadPhrase = new Phrase("Total Load",new Font(Font.FontFamily.TIMES_ROMAN,15.0f,Font.BOLD,BaseColor.BLACK));
        PdfPCell totalLoadCell = new PdfPCell(totalLoadPhrase);
        totalLoadCell.setColspan(6);
        table.addCell(totalLoadCell);


        totalLoadCell = new PdfPCell(new Phrase(totalLoad+"",new Font(Font.FontFamily.TIMES_ROMAN,15.0f,Font.BOLD,BaseColor.BLACK)));
        totalLoadCell.setColspan(1);
        table.addCell(totalLoadCell);

        //Adding table to Document
        document.add(table);

        //Closing Connection With Database
        db.closeConnection();

        //Close Document
        document.close();
    }

    private static void addWorkLoadTableHeader(PdfPTable table){
        Stream.of("Year", "Course Code", "Course Name","Session Duration","Session/Week","Division Count","Course Load")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,11.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }
}
