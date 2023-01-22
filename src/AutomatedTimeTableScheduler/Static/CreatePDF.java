package AutomatedTimeTableScheduler.Static;

import AutomatedTimeTableScheduler.Database.DatabaseCon;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

public class CreatePDF {

    public static void createWorkLoadSheet() throws Exception{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/WorkLoad.pdf"));
        document.open();

        //Connecting to Database
        DatabaseCon db = new DatabaseCon();

        //Writing Header
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD,BaseColor.BLACK);
        Phrase titlePhrase = new Phrase("WorkLoad",titleFont);
        Paragraph title = new Paragraph(titlePhrase);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n\n"));

        PdfPTable table = new PdfPTable(7);
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
            cell.setRowspan(db.getDistinctCourseCountForYear(year)+1);
            table.addCell(cell);

            int yearLoad = 0;

            ResultSet courseResultSet = db.getDistinctCoursListForYear(year);
            while( courseResultSet.next() ){
                cell = new PdfPCell(new Phrase(courseResultSet.getString("course_code")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(courseResultSet.getString("course_name")));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(((int)(courseResultSet.getInt("session_duration")/60))+""));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(courseResultSet.getInt("session_per_week")+""));
                table.addCell(cell);

                int divisionCount = db.getDivisionCountForCourseYear(courseResultSet.getString("course_code"),year);
                cell = new PdfPCell(new Phrase(divisionCount+""));
                table.addCell(cell);

                int courseLoad = ((int)(courseResultSet.getInt("session_duration")/60)) * courseResultSet.getInt("session_per_week") * divisionCount;
                cell = new PdfPCell(new Phrase(courseLoad+""));
                table.addCell(cell);

                yearLoad += courseLoad;
            }

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

        //Close Document
        document.close();
    }

    private static void addWorkLoadTableHeader(PdfPTable table){
        Stream.of("Year", "Course Code", "Course Name","Session Duration","Session/Week","Division Count","Course Load")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(66, 135, 245));
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    public static void createMasterTimeTable() throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Time Table.pdf"));
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

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addMasterTableHeader(table);  //adding header to tables

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

                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(className));
                table.addCell(cell);

                //Adding Cells for TimeSlots
                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nTeacher\nClassroom"));
                    table.addCell(cell);
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

    private static void addMasterTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Class / Time"));
        table.addCell(header);

        ArrayList<Time> timeInfoArray = db.getTimeInfo();
        Time collegeStartTime = timeInfoArray.get(0);
        Time collegeEndTime = timeInfoArray.get(1);
        Time breakStartTime = timeInfoArray.get(2);
        Time breakEndTime = timeInfoArray.get(3);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Time currentTime = collegeStartTime;
        while( currentTime.before(collegeEndTime)){
            if( currentTime.equals(breakStartTime) ){
                currentTime = breakEndTime;
                continue;
            }

            String timeSlot = sdfTime.format(currentTime);
            timeSlot += " - ";
            timeSlot += sdfTime.format(currentTime.getTime()+3600000);

            header = new PdfPCell();
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);

            //Increment Time by 1 hour
            currentTime = new Time(currentTime.getTime()+3600000);  //3600000 ms = 1 hr
        }

        db.closeConnection();
    }
    public static void createClassTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet resultSet = db.getClassList();

        while( resultSet.next() ){
            int year = resultSet.getInt("year");
            String division = resultSet.getString("division");
            String fileName = "";
            switch( year ){
                case 1:
                    fileName = "FE";
                    break;

                case 2:
                    fileName = "SE";
                    break;

                case 3:
                    fileName = "TE";
                    break;

                case 4:
                    fileName = "BE";
                    break;
            }
            fileName += " " + division;

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Class/"+fileName+".pdf"));
            document.open();

            //Writing Class Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD,BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(fileName,titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addClassTableHeader(table);

            for( int i = 0; i < Constant.WEEK.length; i++ ){
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(Constant.WEEK[i]));
                table.addCell(cell);

                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nTeacher\nClassroom"));
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

    private static void addClassTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Day / Time"));
        table.addCell(header);

        ArrayList<Time> timeInfoArray = db.getTimeInfo();
        Time collegeStartTime = timeInfoArray.get(0);
        Time collegeEndTime = timeInfoArray.get(1);
        Time breakStartTime = timeInfoArray.get(2);
        Time breakEndTime = timeInfoArray.get(3);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Time currentTime = collegeStartTime;
        while( currentTime.before(collegeEndTime)){
            if( currentTime.equals(breakStartTime) ){
                currentTime = breakEndTime;
                continue;
            }

            String timeSlot = sdfTime.format(currentTime);
            timeSlot += " - ";
            timeSlot += sdfTime.format(currentTime.getTime()+3600000);

            header = new PdfPCell();
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);

            //Increment Time by 1 hour
            currentTime = new Time(currentTime.getTime()+3600000);  //3600000 ms = 1 hr
        }

        db.closeConnection();
    }

    public static void createClassroomTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet resultSet = db.getClassroomList();

        while( resultSet.next() ){
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Classroom/"+resultSet.getString("room_name")+".pdf"));
            document.open();

            //Writing Class Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD,BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(resultSet.getString("room_name"),titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addClassroomTableHeader(table);

            for( int i = 0; i < Constant.WEEK.length; i++ ){
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPhrase(new Paragraph(Constant.WEEK[i]));
                table.addCell(cell);

                for( int j = 0; j < db.getTimeSlotCount(); j++ ){
                    cell.setPhrase(new Phrase("Course\nTeacher\nClass"));
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

    private static void addClassroomTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Day / Time"));
        table.addCell(header);

        ArrayList<Time> timeInfoArray = db.getTimeInfo();
        Time collegeStartTime = timeInfoArray.get(0);
        Time collegeEndTime = timeInfoArray.get(1);
        Time breakStartTime = timeInfoArray.get(2);
        Time breakEndTime = timeInfoArray.get(3);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Time currentTime = collegeStartTime;
        while( currentTime.before(collegeEndTime)){
            if( currentTime.equals(breakStartTime) ){
                currentTime = breakEndTime;
                continue;
            }

            String timeSlot = sdfTime.format(currentTime);
            timeSlot += " - ";
            timeSlot += sdfTime.format(currentTime.getTime()+3600000);

            header = new PdfPCell();
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);

            //Increment Time by 1 hour
            currentTime = new Time(currentTime.getTime()+3600000);  //3600000 ms = 1 hr
        }

        db.closeConnection();
    }

    public static void createTeacherTimeTable() throws Exception {
        //Connecting to Database
        DatabaseCon db = new DatabaseCon();
        ResultSet resultSet = db.getTeacherList();

        while( resultSet.next() ){
            Document document = new Document(PageSize.A4.rotate());

            String fileName = Constraint.getFormattedText(resultSet.getString("firstname"));
            fileName += " " + Constraint.getFormattedText(resultSet.getString("lastname"));
            PdfWriter.getInstance(document,new FileOutputStream("TimeTable/Teacher/"+fileName+".pdf"));
            document.open();

            //Writing Class Name
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,25.0f,Font.BOLD,BaseColor.BLACK);
            Phrase titlePhrase = new Phrase(fileName,titleFont);
            Paragraph title = new Paragraph(titlePhrase);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n\n"));

            PdfPTable table = new PdfPTable(db.getTimeSlotCount()+1);
            addTeacherTableHeader(table);

            for( int i = 0; i < Constant.WEEK.length; i++ ){
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

    private static void addTeacherTableHeader(PdfPTable table) throws Exception{
        DatabaseCon db = new DatabaseCon();

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new BaseColor(66, 135, 245));
        header.setPhrase(new Phrase("Day / Time"));
        table.addCell(header);

        ArrayList<Time> timeInfoArray = db.getTimeInfo();
        Time collegeStartTime = timeInfoArray.get(0);
        Time collegeEndTime = timeInfoArray.get(1);
        Time breakStartTime = timeInfoArray.get(2);
        Time breakEndTime = timeInfoArray.get(3);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Time currentTime = collegeStartTime;
        while( currentTime.before(collegeEndTime)){
            if( currentTime.equals(breakStartTime) ){
                currentTime = breakEndTime;
                continue;
            }

            String timeSlot = sdfTime.format(currentTime);
            timeSlot += " - ";
            timeSlot += sdfTime.format(currentTime.getTime()+3600000);

            header = new PdfPCell();
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(new BaseColor(66, 135, 245));
            header.setPhrase(new Phrase(timeSlot));
            table.addCell(header);

            //Increment Time by 1 hour
            currentTime = new Time(currentTime.getTime()+3600000);  //3600000 ms = 1 hr
        }

        db.closeConnection();
    }
}
