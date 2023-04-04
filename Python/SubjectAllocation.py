from DatabaseConnection import DatabaseConnection

db = DatabaseConnection()

courseClassList = db.fillSubjectWithCourseClass()

teacherCoursePreferenceList = db.getTeacherCoursePreferenceList()

# Allocation only 12 Hours/Week Load
i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad() and i < len(teacherCoursePreferenceList):
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        for row in results:
            if not db.isTeacherAllotedAnotherCourseLecture(preference[1], row[3]):
                if db.getTeacherLoad(preference[1]) + db.getCourseWeeklyLoad(preference[0]) < 12:
                    db.insertSubjectTeacherIdClassId(preference[0], preference[1], row[3])
    i += 1


# Allocation Only 14 Hours/Week Load
i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad() and i < len(teacherCoursePreferenceList):
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        for row in results:
            if not db.isTeacherAllotedAnotherCourseLecture(preference[1], row[3]):
                if db.getTeacherLoad(preference[1]) + db.getCourseWeeklyLoad(preference[0]) < 14:
                    db.insertSubjectTeacherIdClassId(preference[0], preference[1], row[3])
    i += 1


# Allocation 16 Hours/Week Load
i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad() and i < len(teacherCoursePreferenceList):
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        for row in results:
            if not db.isTeacherAllotedAnotherCourseLecture(preference[1], row[3]):
                if db.getTeacherLoad(preference[1]) + db.getCourseWeeklyLoad(preference[0]) < 16:
                    db.insertSubjectTeacherIdClassId(preference[0], preference[1], row[3])
    i += 1


# Allocation 18 Hours/Week Load
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


# Allocation Remaining Subjects if Any if This loop is executing means that some teachers may get more than 18 hours load
i = 0
while db.getTotalWeeklyLoad() > db.getAllotedWeeklyLoad():
    preference = teacherCoursePreferenceList[i]
    results = db.getEmptySubject(preference[0])
    if len(results) > 0:
        db.insertSubjectTeacherId(preference[0], preference[1])
    i = (i+1) % len(teacherCoursePreferenceList)

print("Subject Allocation Finished")
