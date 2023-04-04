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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

import static java.sql.Types.NULL;

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

            //Adding logo
            AddResources.addLogo(document);

            //Writing Teacher Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(fileName,titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            //Lecture Allocation Table
            int totalLoad = 0; // To Store total workload of Teacher per week
            PdfPTable lectureTable = new PdfPTable(new float[]{0.2f,0.4f,0.2f,0.1f,0.1f});
            lectureTable.setWidthPercentage(100);
            addLectureTableHeader(lectureTable);
            ResultSet lectureResultSet = db.getLectureAllocationForTeacher(teacherResultSet.getInt("teacher_id"));
            if (lectureResultSet.next()) {
                 do{
                    PdfPCell cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_code")));
                    lectureTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(lectureResultSet.getString("course_name")));
                    lectureTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(Constraint.getClassString(lectureResultSet.getInt("year"), lectureResultSet.getString("division"))));
                    lectureTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(lectureResultSet.getString("session_duration")));
                    lectureTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(lectureResultSet.getString("session_per_week")));
                    lectureTable.addCell(cell);

                    totalLoad += lectureResultSet.getInt("session_duration") * lectureResultSet.getInt("session_per_week");
                }while (lectureResultSet.next());
            }else{
                PdfPCell cell = new PdfPCell(new Phrase("No Lectures Alloted"));
                cell.setColspan(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                lectureTable.addCell(cell);
            }
            document.add(lectureTable);

            Paragraph seperatorParagraph = new Paragraph("\n--------------------\n\n");
            seperatorParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(seperatorParagraph);

            //Practical Allocation Table
            PdfPTable practicalTable = new PdfPTable(new float[]{0.15f,0.37f,0.15f,0.1f,0.1f,0.1f});
            practicalTable.setWidthPercentage(100);
            addPracticalTableHeader(practicalTable);
            ResultSet practicalResultSet = db.getPracticalAllocationForTeacher(teacherResultSet.getInt("teacher_id"));
            if ( practicalResultSet.next() ) {
                 do{
                    PdfPCell cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_code")));
                    practicalTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(practicalResultSet.getString("course_name")));
                    practicalTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(Constraint.getClassString(practicalResultSet.getInt("year"), practicalResultSet.getString("division"))));
                    practicalTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(practicalResultSet.getInt("batch") + ""));
                    practicalTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(practicalResultSet.getInt("session_duration") + ""));
                    practicalTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(practicalResultSet.getInt("session_per_week") + ""));
                    practicalTable.addCell(cell);

                    totalLoad += practicalResultSet.getInt("session_duration") * practicalResultSet.getInt("session_per_week");
                }while (practicalResultSet.next());
            }else{
                PdfPCell cell = new PdfPCell(new Phrase("No Practicals Alloted"));
                cell.setColspan(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                practicalTable.addCell(cell);
            }
            document.add(practicalTable);

            //Writing Total Workload
            Font loadFont = new Font(Font.FontFamily.TIMES_ROMAN,18.0f,Font.NORMAL, BaseColor.BLACK);
            Phrase loadPhrase = new Phrase("Total WorkLoad : "+totalLoad,loadFont);
            Paragraph loadTitle = new Paragraph(loadPhrase);
            loadTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(loadTitle);
            document.add(new Paragraph("\n\n"));

            document.setPageSize(PageSize.A4.rotate());
            document.newPage();

            //Adding Logo
            AddResources.addLogo(document);
            document.add(new Phrase("\n\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+2);
            addTeacherTableHeader(table);

            boolean breakCellAdded = false;
            for(int i = 0; i < Constant.WEEK.length; i++ ){
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                cell.setPhrase(new Paragraph(Constant.WEEK[i]));
                table.addCell(cell);

                //Adding Cells for TimeSlots
                ResultSet timeSlotResultSet = db.getTimeSlotsForDay(Constant.WEEK[i]);
                timeSlotResultSet.next();
                int previousTimeSlot = timeSlotResultSet.getInt("time_id")-1;
                timeSlotResultSet = db.getTimeSlotsForDay(Constant.WEEK[i]);
                while (timeSlotResultSet.next()){
                    if( !breakCellAdded && previousTimeSlot + 1 != timeSlotResultSet.getInt("time_id")){
                        cell.setPhrase(new Phrase("Break"));
                        cell.setRowspan(Constant.WEEK.length);
                        cell.setColspan(1);
                        table.addCell(cell);
                        breakCellAdded = true;
                        cell.setRowspan(1);
                    }
                    previousTimeSlot = timeSlotResultSet.getInt("time_id");
                    ResultSet timeTable = db.getEntryForTimeTeacher(timeSlotResultSet.getInt("time_id"),teacherResultSet.getInt("teacher_id"));
                    if( timeTable.next() ){
                        if ( timeTable.getInt("batch") == NULL ){
                            String entry = "";

                            entry += timeTable.getString("c_abbreviation") + "\n";
                            entry += Constraint.getClassString(timeTable.getInt("year"),timeTable.getString("division"))+"\n";
                            entry += timeTable.getString("room_name");

                            cell.setPhrase(new Phrase(entry));
                            cell.setColspan(1);
                            table.addCell(cell);
                        }else{
                            String entry = "";

                            entry += timeTable.getString("c_abbreviation") + " - ";
                            entry += Constraint.getClassString(timeTable.getInt("year"),timeTable.getString("division"))+ " - ";
                            entry += timeTable.getString("room_name") + " - #";
                            entry += timeTable.getInt("batch");

                            cell.setPhrase(new Phrase(entry));
                            cell.setColspan(2);
                            table.addCell(cell);
                            timeSlotResultSet.next();
                        }
                    }else{
                        cell.setPhrase(new Phrase("-----"));
                        cell.setColspan(1);
                        table.addCell(cell);
                    }
                }
            }
            //Adding Table
            document.add(table);

            //Adding Course Abbreviation Table
            AddResources.addCourseAbbreviationPage(document);

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
        lectureCell.setColspan(5);
        lectureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(lectureCell);
        Stream.of("Course Code","Course Name","Class","Session Duration","Session/Week")
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
        practicalCell.setColspan(6);
        practicalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(practicalCell);
        Stream.of("Course Code","Course Name","Class","Batch","Session Duration","Session/Week")
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
        Time previousEndTime = null;
        while(timeResultSet.next()){
            String timeSlot = "";
            if( previousEndTime != null && !sdfTime.format(previousEndTime).equals(sdfTime.format(timeResultSet.getTime("start_time"))) ){
                timeSlot = sdfTime.format(previousEndTime) + "-" + sdfTime.format(timeResultSet.getTime("start_time"));
                header.setPhrase(new Phrase(timeSlot));
                table.addCell(header);
            }

            timeSlot = sdfTime.format(timeResultSet.getTime("start_time")) + "-" + sdfTime.format(timeResultSet.getTime("end_time"));
            previousEndTime = timeResultSet.getTime("end_time");

            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);
        }
        db.closeConnection();
    }
}
