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
import java.util.stream.Stream;

public class TeacherTimeTable {
    public static void createTeacherTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet teacherResultSet = db.getTeacherList();


        while( teacherResultSet.next() ){
            Document document = new Document(PageSize.A4);

            String fileName = Constraint.getFormattedText(teacherResultSet.getString("firstname"));
            fileName += " " + Constraint.getFormattedText(teacherResultSet.getString("lastname"));
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Teacher/"+fileName+".pdf"));
            document.open();

            //Writing Teacher Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(fileName,titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            //Lecture Allocation Table
            PdfPTable lectureTable = new PdfPTable(new float[]{0.3f,0.4f,0.3f});
            lectureTable.setWidthPercentage(100);
            addLectureTableHeader(lectureTable);
            ResultSet lectureResultSet = db.getLectureAllocationForTeacher(teacherResultSet.getInt("teacher_id"));
            while( lectureResultSet.next() ){
                PdfPCell cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_code")));
                lectureTable.addCell(cell);

                cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_name")));
                lectureTable.addCell(cell);

                cell = new PdfPCell(new Phrase(Constraint.getClassString(lectureResultSet.getInt("year"),lectureResultSet.getString("division"))));
                lectureTable.addCell(cell);
            }
            document.add(lectureTable);

            Paragraph seperatorParagraph = new Paragraph("\n--------------------\n\n");
            seperatorParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(seperatorParagraph);

            //Practical Allocation Table
            PdfPTable practicalTable = new PdfPTable(new float[]{0.25f,0.4f,0.1f,0.25f});
            practicalTable.setWidthPercentage(100);
            addPracticalTableHeader(practicalTable);
            ResultSet practicalResultSet = db.getPracticalAllocationForTeacher(teacherResultSet.getInt("teacher_id"));
            while( practicalResultSet.next() ){
                PdfPCell cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_code")));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_name")));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(practicalResultSet.getInt("batch")+""));
                practicalTable.addCell(cell);

                cell = new PdfPCell(new Phrase(Constraint.getClassString(practicalResultSet.getInt("year"),practicalResultSet.getString("division"))));
                practicalTable.addCell(cell);
            }
            document.add(practicalTable);

            document.newPage();
            document.setPageSize(PageSize.A4.rotate());

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addTeacherTableHeader(table);

            for(int i = 0; i < Constant.WEEK.length; i++ ){
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(Constant.WEEK[i]));
                table.addCell(cell);

                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nClass\nClassroom"));
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

    private static void addLectureTableHeader(PdfPTable table) throws Exception{
        PdfPCell lectureCell = new PdfPCell();
        lectureCell.setBackgroundColor(new BaseColor(66,135,245));
        lectureCell.setPhrase(new Phrase("Lectures",new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
        lectureCell.setColspan(3);
        lectureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(lectureCell);
        Stream.of("Course Code","Course Name","Class")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }

    private static void addPracticalTableHeader(PdfPTable table) throws Exception{
        PdfPCell practicalCell = new PdfPCell();
        practicalCell.setBackgroundColor(new BaseColor(66,135,245));
        practicalCell.setPhrase(new Phrase("Practicals",new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
        practicalCell.setColspan(4);
        practicalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(practicalCell);
        Stream.of("Course Code","Course Name","Batch","Class")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }

    private static void addTeacherTableHeader(PdfPTable table) throws Exception{
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
