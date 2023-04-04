from DatabaseConnection import DatabaseConnection

db = DatabaseConnection()

classList = db.getClassList()

for clss in classList:
    lectureSubject = db.executeReadQuery("SELECT subject_id FROM subject JOIN course ON subject.course_code = course.course_code WHERE class_id = {} AND course.session_duration=1;".format(clss))
    i = 0
    for time in db.getTimeSlots():
        if not db.checkIfClassHasPractical(time,lectureSubject[i][0]):
            db.insertLectureTimeTableRecord(time,lectureSubject[i][0],17)
            print(lectureSubject[i][0])
            i += 1
            print(i)
            i = i % len(lectureSubject)