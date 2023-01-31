from DatabaseConnection import DatabaseConnection

db = DatabaseConnection()

courseClassList = db.fillSubjectWithCourseClass()

teacherCoursePreferenceList = db.getTeacherCoursePreferenceList()

i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad() and i < len(teacherCoursePreferenceList):
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        for row in results:
            if not db.isTeacherAllotedAnotherCourseLecture(preference[1], row[3]):
                if db.getTeacherLoad(preference[1]) + db.getCourseWeeklyLoad(preference[0]) < 18:
                    db.insertSubjectTeacherIdClassId(preference[0], preference[1], row[3])
    i += 1

i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad():
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        db.insertSubjectTeacherId(preference[0], preference[1])
    i = (i+1) % len(teacherCoursePreferenceList)

print("Subject Allocation Finished")