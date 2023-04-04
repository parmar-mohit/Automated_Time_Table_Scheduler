import mysql.connector


class DatabaseConnection:
    def __init__(self):
        self.db = mysql.connector.connect(
            host="localhost",
            username="timetable_user",
            password="timetable_pass",
            database="timetable"
        )
        self.cursor = "Cursor"

    def __del__(self):
        # Closing Database Connection
        del self.cursor

    def executeReadQuery(self, query):
        self.cursor = self.db.cursor()
        self.cursor.execute(query)
        result = self.cursor.fetchall()
        self.cursor.close()
        return result

    def executeUpdateQuery(self, query):
        self.cursor = self.db.cursor()
        self.cursor.execute(query)
        self.db.commit()
        self.cursor.close()

    def getAllotedWeeklyLoad(self):
        query = "SELECT course_code FROM subject WHERE teacher_id IS NOT NULL;"
        result = self.executeReadQuery(query)

        load = 0
        for course in result:
            query = "SELECT * FROM course WHERE course_code = \"{}\";".format(course[0])
            courseResult = self.executeReadQuery(query)[0]
            load += courseResult[2] * courseResult[3]

        return load

    def getTotalWeeklyLoad(self):
        query = "SELECT session_duration,session_per_week FROM course JOIN subject ON " \
                "course.course_code=subject.course_code; "
        subjectResultSet = self.executeReadQuery(query)

        load = 0
        for subject in subjectResultSet:
            load += subject[0] * subject[1]

        return load

    def getTeacherLoad(self, teacherId):
        query = "SELECT session_duration,session_per_week FROM course JOIN subject on " \
                "course.course_code=subject.course_code WHERE subject.teacher_id = {}".format(
            teacherId)
        subjectResultSet = self.executeReadQuery(query)

        load = 0
        for subject in subjectResultSet:
            load += subject[0] * subject[1]

        return load

    def fillSubjectWithCourseClass(self):
        query = "DELETE FROM subject;"
        self.executeUpdateQuery(query)

        query = "SELECT course.course_code,session_duration,class_id FROM course JOIN class_course ON " \
                "course.course_code = class_course.course_code; "
        courseClassResult = self.executeReadQuery(query)

        for courseClass in courseClassResult:
            if courseClass[1] == 1:
                query = "INSERT INTO subject(course_code,class_id) VALUES(\"{}\",{});".format(courseClass[0],
                                                                                              courseClass[2])
                self.executeUpdateQuery(query)
            elif courseClass[1] == 2:
                for i in range(1, 5):
                    query = "INSERT INTO subject(batch,course_code,class_id) VALUES({},\"{}\",{});".format(i,
                                                                                                           courseClass[
                                                                                                               0],
                                                                                                           courseClass[
                                                                                                               2])
                    self.executeUpdateQuery(query)

    def getTeacherCoursePreferenceList(self):
        query = "SELECT * FROM course_teacher ORDER BY preference,teacher_id;"
        return self.executeReadQuery(query)

    def getCourseWeeklyLoad(self, courseCode):
        query = "SELECT * FROM course WHERE course_code = \"{}\";".format(courseCode)
        result = self.executeReadQuery(query)[0]
        return result[2] * result[3]

    def getEmptySubject(self, courseCode):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL;".format(courseCode)
        return self.executeReadQuery(query)

    def insertSubjectTeacherId(self, courseCode, teacherId):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL;".format(courseCode)
        result = self.executeReadQuery(query)

        query = "UPDATE subject SET teacher_id = {} WHERE subject_id = {};".format(teacherId, result[0][0])
        self.executeUpdateQuery(query)

    def insertSubjectTeacherIdClassId(self, courseCode, teacherId, classId):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL AND class_id = {};".format(
            courseCode, classId)
        result = self.executeReadQuery(query)

        query = "UPDATE subject SET teacher_id = {} WHERE subject_id = {};".format(teacherId, result[0][0])
        self.executeUpdateQuery(query)

    def isTeacherAllotedAnotherCourseLecture(self, teacherId, classId):
        query = "SELECT COUNT(*) FROM subject JOIN course ON subject.course_code = course.course_code WHERE " \
                "teacher_id = {} AND class_id = {} AND course.session_duration=1;".format(
            teacherId, classId)
        return self.executeReadQuery(query)[0][0]

    def getTimeSlotsCount(self):
        query = "SELECT COUNT(*) FROM time_slots;"
        return self.executeReadQuery(query)[0][0]

    def getPracticalSubjectsCount(self):
        query = "SELECT COUNT(*) FROM subject JOIN course ON subject.course_code = course.course_code WHERE " \
                "course.session_duration=2; "
        return self.executeReadQuery(query)[0][0]

    def getRoomsCount(self):
        query = "SELECT COUNT(*) FROM room;"
        return self.executeReadQuery(query)[0][0]

    def getTimeSlots(self):
        query = "SELECT time_id FROM time_slots;"
        timeSlots = []
        for timeId in self.executeReadQuery(query):
            timeSlots.append(timeId[0])
        return timeSlots

    def getPracticalSubjects(self):
        query = "SELECT subject_id FROM subject JOIN course ON subject.course_code = course.course_code WHERE session_duration = 2;"
        practicalSubjects = []
        for practical in self.executeReadQuery(query):
            practicalSubjects.append(practical[0])
        return practicalSubjects

    def getPracticalRoom(self):
        query = "SELECT DISTINCT(room_id) FROM course_rooms WHERE course_code IN (SELECT course_code FROM course WHERE course.session_duration = 2);"
        rooms = []
        for room in self.executeReadQuery(query):
            rooms.append(room[0])
        return rooms

    def checkValidSubjectRoom(self, subjectCode, roomId):
        query = "SELECT EXISTS(SELECT room_id FROM course_rooms WHERE course_code = ( SELECT course_code FROM subject WHERE subject_id = {}) AND room_id={});".format(
            subjectCode, roomId)
        return bool(self.executeReadQuery(query)[0][0])

    def getSubjectTotalHoursNeeded(self, subjectCode):
        query = "SELECT course.session_per_week FROM subject JOIN course ON subject.course_code = course.course_code and subject.subject_id = {};".format(
            subjectCode)
        return self.executeReadQuery(query)[0][0]

    def getOtherBatchesOfSameClass(self, subjectCode):
        query = "SELECT subject_id FROM subject WHERE class_id = (SELECT class_id FROM subject WHERE subject_id = {}) AND batch NOT IN (\"NULL\",(SELECT batch FROM subject WHERE subject_id={}))".format(
            subjectCode, subjectCode)
        subjects = []
        for row in self.executeReadQuery(query):
            subjects.append(row[0])
        return subjects

    def getTeacherOtherSubjects(self, subjectCode):
        query = "SELECT subject_id FROM subject WHERE teacher_id = (SELECT teacher_id FROM subject WHERE subject_id = {});".format(
            subjectCode)
        otherSubject = []
        for subject in self.executeReadQuery(query):
            otherSubject.append(subject[0])

        return otherSubject

    def getTeacherList(self):
        query = "SELECT teacher_id FROM teacher;"
        teacherList = []
        for teacher in self.executeReadQuery(query):
            teacherList.append(teacher[0])

        return teacherList

    def getClassList(self):
        query = "SELECT class_id FROM class;"
        classList = []
        for i in self.executeReadQuery(query):
            classList.append(i[0])

        return classList

    def getClassAndBatchFromSubject(self, subjectCode):
        query = "SELECT class_id,batch FROM subject WHERE subject_id={};".format(subjectCode)
        sol = self.executeReadQuery(query)[0]
        return sol[0], sol[1]

    def getTeacherIdFromSubject(self, subjectCode):
        query = "SELECT teacher_id FROM subject WHERE subject_id = {};".format(subjectCode)
        return self.executeReadQuery(query)[0][0]

    def deleteTimeTable(self):
        query = "DELETE FROM time_table;"
        self.executeUpdateQuery(query)

    def insertPracticalTimeTableRecord(self, timeId, subject, room):
        for timeSlot in timeId:
            query = "INSERT INTO time_table(time_id,subject_id,room_id) VALUES({},{},{});".format(timeSlot, subject,
                                                                                                  room)
            self.executeUpdateQuery(query)

    def getLectureSubjects(self):
        query = "SELECT subject_id FROM subject JOIN course ON subject.course_code = course.course_code WHERE session_duration = 1;"
        lectureSubjects = []
        for practical in self.executeReadQuery(query):
            lectureSubjects.append(practical[0])
        return lectureSubjects

    def getLectureRoom(self):
        query = "SELECT DISTINCT(room_id) FROM course_rooms WHERE course_code IN (SELECT course_code FROM course WHERE session_duration=1);"
        rooms = []
        for room in self.executeReadQuery(query):
            rooms.append(room[0])
        return rooms

    def checkIfClassHasPractical(self,timeId,subjectId):
        query = "SELECT EXISTS( SELECT * FROM time_table WHERE time_id = {} AND subject_id IN (SELECT subject_id FROM subject WHERE class_id =(SELECT class_id FROM subject WHERE subject_id = {} ) ) );".format(timeId,subjectId)
        return self.executeReadQuery(query)[0][0]

    def insertLectureTimeTableRecord(self,timeId,subjectId,roomId):
        query = "INSERT INTO time_table VALUES({},{},{});".format(subjectId,roomId,timeId)
        self.executeUpdateQuery(query)
