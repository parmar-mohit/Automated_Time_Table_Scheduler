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

    def executeReadQuery(self,query):
        self.cursor = self.db.cursor()
        self.cursor.execute(query)
        result = self.cursor.fetchall()
        self.cursor.close()
        return result

    def executeUpdateQuery(self,query):
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
        query = "SELECT * FROM class;"
        classResult = self.executeReadQuery(query)

        load = 0
        for classObject in classResult:
            query = "SELECT * FROM class_course WHERE class_id = {};".format(classObject[0])
            classCourseResult = self.executeReadQuery(query)

            for classCourse in classCourseResult:
                query = "SELECT * FROM course WHERE course_code = \"{}\";".format(classCourse[1])
                courseResult = self.executeReadQuery(query)[0]

                if courseResult[3] == 1:
                    load += courseResult[2] * courseResult[3]
                elif courseResult[3] == 2:
                    load += courseResult[2] * courseResult[3] * 4

        return load

    def getTeacherLoad(self,teacherId):
        query = "SELECT course_code FROM subject WHERE teacher_id = {};".format(teacherId)
        result = self.executeReadQuery(query)

        load = 0
        for course in result:
            query = "SELECT * FROM course WHERE course_code = \"{}\";".format(course[0])
            courseResult = self.executeReadQuery(query)[0]
            if courseResult[3] == 1:
                load += courseResult[2] * courseResult[3]
            elif courseResult[3] == 2:
                load += courseResult[2] * courseResult[3] * 4

        return load

    def fillSubjectWithCourseClass(self):
        query = "DELETE FROM subject;"
        self.executeUpdateQuery(query)

        query = "SELECT course.course_code,session_duration,class_id FROM course JOIN class_course ON course.course_code = class_course.course_code;"
        courseClassResult =  self.executeReadQuery(query)

        for courseClass in courseClassResult:
            if courseClass[1] == 1:
                query = "INSERT INTO subject(course_code,class_id) VALUES(\"{}\",{});".format(courseClass[0],courseClass[2])
                self.executeUpdateQuery(query)
            elif courseClass[1] == 2:
                for i in range(1,5):
                    query = "INSERT INTO subject(batch,course_code,class_id) VALUES({},\"{}\",{});".format(i,courseClass[0],courseClass[2])
                    self.executeUpdateQuery(query)

    def getTeacherCoursePreferenceList(self):
        query = "SELECT * FROM course_teacher ORDER BY preference,teacher_id;"
        return self.executeReadQuery(query)

    def getCourseWeeklyLoad(self,courseCode):
        query = "SELECT * FROM course WHERE course_code = \"{}\";".format(courseCode)
        result = self.executeReadQuery(query)[0]
        return result[2] * result[3]

    def getEmptySubject(self, courseCode):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL;".format(courseCode)
        return self.executeReadQuery(query)

    def insertSubjectTeacherId(self,courseCode,teacherId):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL;".format(courseCode)
        result = self.executeReadQuery(query)

        query = "UPDATE subject SET teacher_id = {} WHERE subject_id = {};".format(teacherId,result[0][0])
        self.executeUpdateQuery(query)

    def insertSubjectTeacherIdClassId(self, courseCode, teacherId, classId):
        query = "SELECT * FROM subject WHERE course_code = \"{}\" AND teacher_id IS NULL AND class_id = {};".format(courseCode,classId)
        result = self.executeReadQuery(query)

        query = "UPDATE subject SET teacher_id = {} WHERE subject_id = {};".format(teacherId,result[0][0])
        self.executeUpdateQuery(query)

    def isTeacherAllotedAnotherCourseLecture(self, teacherId, classId):
        query = "SELECT COUNT(*) FROM subject JOIN course ON subject.course_code = course.course_code WHERE teacher_id = {} AND class_id = {} AND course.session_duration=1;".format(teacherId,classId)
        return self.executeReadQuery(query)[0][0]