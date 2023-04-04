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

import static java.sql.Types.NULL;

public class ClassroomTimeTable {
    public static void createClassroomTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet roomResultSet = db.getClassroomList();

        while( roomResultSet.next() ){
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Classroom/"+roomResultSet.getString("room_name")+".pdf"));
            document.open();

            //Adding Logo
            AddResources.addLogo(document);

            //Writing Class Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD, BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(roomResultSet.getString("room_name"),titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+2);
            addClassroomTableHeader(table);

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
                    ResultSet timeTable = db.getEntryForTimeRoom(timeSlotResultSet.getInt("time_id"),roomResultSet.getInt("room_id"));
                    if( timeTable.next() ){
                        if ( timeTable.getInt("batch") == NULL ){
                            String entry = "";

                            entry += timeTable.getString("c_abbreviation") + "\n";
                            entry += timeTable.getString("t_abbreviation") + "\n";
                            entry += Constraint.getClassString(timeTable.getInt("year"),timeTable.getString("division"));

                            cell.setPhrase(new Phrase(entry));
                            cell.setColspan(1);
                            table.addCell(cell);
                        }else{
                            String entry = "";

                            entry += timeTable.getString("c_abbreviation") + " - ";
                            entry += timeTable.getString("t_abbreviation") + " - ";
                            entry += Constraint.getClassString(timeTable.getInt("year"),timeTable.getString("division"))+ " - #";
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

            //Adding Abbreviation Table
            AddResources.addCourseAbbreviationPage(document);
            AddResources.addTeacherAbbreviationPage(document);

            //Closing Document
            document.close();
        }

        //Closing Connection With Database
        db.closeConnection();
    }

    private static void addClassroomTableHeader(PdfPTable table) throws Exception{
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
