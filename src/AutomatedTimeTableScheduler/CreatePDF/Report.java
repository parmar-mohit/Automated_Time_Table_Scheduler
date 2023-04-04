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
        AddResources.addLogo(document);

        //Teacher Allocation Report
        Phrase teacherReportPhrase = new Phrase("Teacher Workload Allocation Report",new Font(Font.FontFamily.TIMES_ROMAN,16.0f,Font.BOLD,BaseColor.BLACK));
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

        //Adding Room Utilisation Report
        document.newPage();

        //Adding Logo
        AddResources.addLogo(document);

        //Teacher Allocation Report
        Phrase roomReportPhrase = new Phrase("Class Utilisation Report",new Font(Font.FontFamily.TIMES_ROMAN,16.0f,Font.BOLD,BaseColor.BLACK));
        Paragraph roomReportParagraph = new Paragraph(roomReportPhrase);
        roomReportParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(roomReportParagraph);
        document.add(new Paragraph("\n\n"));

        int totalWorkingHours = db.getTotalWorkingHours();
        Phrase workingHours = new Phrase("Total Working Hours/Week : "+totalWorkingHours);
        document.add(workingHours);
        document.add(new Paragraph("\n\n"));

        PdfPTable classTable = new PdfPTable(new float[]{0.6f,0.2f,0.2f});
        addRoomUtilisationTableHeader(classTable);

        ResultSet roomResultSet = db.getClassroomList();
        float utilisationSum = 0;
        while( roomResultSet.next() ){
            cell.setPhrase(new Phrase(roomResultSet.getString("room_name")));
            classTable.addCell(cell);

            int roomWorkingHours = db.getRoomWorkingHours(roomResultSet.getInt("room_id"));
            cell.setPhrase(new Phrase(roomWorkingHours+""));
            classTable.addCell(cell);

            float utilisation = BigDecimal.valueOf((float)roomWorkingHours/totalWorkingHours*100)
                    .setScale(2, RoundingMode.HALF_UP)
                    .floatValue();
            cell.setPhrase(new Phrase(utilisation + "%"));
            classTable.addCell(cell);

            utilisationSum += utilisation;
        }
        cell = new PdfPCell(new Phrase("Average Utilisation",new Font(Font.FontFamily.TIMES_ROMAN,15.0f,Font.BOLD,BaseColor.BLACK)));
        cell.setColspan(2);
        classTable.addCell(cell);

        float averageUtilisation = BigDecimal.valueOf(utilisationSum/db.getClassroomCount())
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
        cell = new PdfPCell(new Phrase(averageUtilisation+"%"));
        classTable.addCell(cell);

        //Adding Table to Document
        document.add(classTable);

        //Closing Connection With Database
        db.closeConnection();

        //Closing Document
        document.close();
    }

    private static void addTeacherAllocationTableHeader(PdfPTable table){
        Stream.of("Teacher Name","Lecture Load","Practical Load","Total Load")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }

    private static void addRoomUtilisationTableHeader(PdfPTable table){
        Stream.of("Room Name","Working Hours","Utilisation")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle,new Font(Font.FontFamily.TIMES_ROMAN,14.0f,Font.NORMAL,BaseColor.BLACK)));
                    table.addCell(header);
                });
    }
}
