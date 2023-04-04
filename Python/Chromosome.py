from DatabaseConnection import DatabaseConnection


class PracticalChromosome:
    def __init__(self):
        db = DatabaseConnection()
        timeSlots = db.getTimeSlots()
        self.practicalTimeSlots = []
        i = 0
        while i < len(timeSlots):
            if i + 1 < len(timeSlots) and timeSlots[i] + 1 == timeSlots[i + 1]:
                self.practicalTimeSlots.append((timeSlots[i], timeSlots[i + 1]))
                timeSlots.pop(i)
                timeSlots.pop(i)
            else:
                i += 1
        self.practicalSubjects = db.getPracticalSubjects()
        self.rooms = db.getPracticalRoom()
        self.chromosome = {}

    def setChromosome(self, chromosome):
        k = 0
        for timeId in self.practicalTimeSlots:
            self.chromosome[timeId] = {}
            for practical in self.practicalSubjects:
                self.chromosome[timeId][practical] = chromosome[k]
                k += 1

    def getConflictCount(self):
        # For types of Conflict refer to Documentation/Practical Conflicts.md
        db = DatabaseConnection()
        teacherList = db.getTeacherList()
        classList = db.getClassList()
        conflicts = 0

        subjectMap = {i: 0 for i in self.practicalSubjects}
        for timeId in self.practicalTimeSlots:
            classMap = {i: [0, 0, 0, 0] for i in classList}
            teacherMap = {i: 0 for i in teacherList}
            roomMap = {i: 0 for i in self.rooms}
            for practical in self.practicalSubjects:
                if self.chromosome[timeId][practical] != 0:
                    # Type 1
                    if not db.checkValidSubjectRoom(practical, self.chromosome[timeId][practical]):
                        conflicts += 1

                    # Type 2
                    roomMap[self.chromosome[timeId][practical]] += 1

                    # Type 3 and Type 4
                    subjectMap[practical] += 1

                    # Type 5
                    teacherMap[db.getTeacherIdFromSubject(practical)] += 1

                    # Type 6
                    classId, batch = db.getClassAndBatchFromSubject(practical)
                    classMap[classId][batch - 1] += 1

            # Type 2
            for key, val in roomMap.items():
                if val > 1:
                    conflicts += val - 1

            # Type 5
            for key, val in teacherMap.items():
                if val > 1:
                    conflicts += val - 1


            for classId, val in classMap.items():
                if val != [0,0,0,0]:
                    for i in val:
                        # Type 6
                        if i == 0:
                            conflicts += 1

                        # Type 7:
                        if i > 1:
                            conflicts += i - 1


        # Type 3 and Tye 4
        for practical in self.practicalSubjects:
            hoursNeeded = db.getSubjectTotalHoursNeeded(practical)
            if hoursNeeded < subjectMap[practical]:
                conflicts += subjectMap[practical] - hoursNeeded
            elif hoursNeeded > subjectMap[practical]:
                conflicts += hoursNeeded - subjectMap[practical]

        return conflicts

    def insertTimeTable(self):
        db = DatabaseConnection()

        # Deleting Existing Time Table From database
        db.deleteTimeTable()

        # Inserting Entries into Time Table
        for timeId in self.practicalTimeSlots:
            for practicalSubject in self.practicalSubjects:
                if self.chromosome[timeId][practicalSubject] != 0:
                    db.insertPracticalTimeTableRecord(timeId, practicalSubject,
                                                      self.chromosome[timeId][practicalSubject])

    def getChromosomeLength(self):
        return len(self.practicalTimeSlots) * len(self.practicalSubjects)


class LectureChromosome:
    def __init__(self):
        db = DatabaseConnection()
        self.lectureTimeSlots = db.getTimeSlots()
        self.lectureSubjects = db.getLectureSubjects()
        self.rooms = db.getLectureRoom()
        self.chromosome = {}

    def setChromosome(self, chromosome):
        k = 0
        for timeId in self.lectureTimeSlots:
            self.chromosome[timeId] = {}
            for practical in self.lectureSubjects:
                self.chromosome[timeId][practical] = chromosome[k]
                k += 1

    def getConflictCount(self):
        # For types of Conflict refer to Documentation/Lecture Conflicts.md
        db = DatabaseConnection()
        teacherList = db.getTeacherList()
        conflicts = 0

        subjectMap = {i: 0 for i in self.lectureSubjects}
        for timeId in self.lectureTimeSlots:
            teacherMap = {i: 0 for i in teacherList}
            roomMap = {i: 0 for i in self.rooms}
            for lecture in self.lectureSubjects:
                if self.chromosome[timeId][lecture] != 0:
                    # Type 1
                    if not db.checkValidSubjectRoom(lecture,self.chromosome[timeId][lecture]):
                        conflicts += 1

                    # Type 2
                    roomMap[self.chromosome[timeId][lecture]] += 1

                    # Type 3 and Type 4
                    subjectMap[lecture] += 1

                    # Type 5
                    teacherMap[db.getTeacherIdFromSubject(lecture)] += 1

                    # Type 6
                    if db.checkIfClassHasPractical(timeId,lecture):
                        conflicts += 1

            # Type 2
            for key, val in roomMap.items():
                if val > 1:
                    conflicts += val - 1

            # Type 5
            for key, val in teacherMap.items():
                if val > 1:
                    conflicts += val - 1

        # Type 3 and Tye 4
        for practical in self.lectureSubjects:
            hoursNeeded = db.getSubjectTotalHoursNeeded(practical)
            if hoursNeeded < subjectMap[practical]:
                conflicts += subjectMap[practical] - hoursNeeded
            elif hoursNeeded > subjectMap[practical]:
                conflicts += hoursNeeded - subjectMap[practical]

        return conflicts

    def insertTimeTable(self):
        db = DatabaseConnection()

        for timeId in self.lectureTimeSlots:
            for lecture in self.lectureSubjects:
                if self.chromosome[timeId][lecture] != 0:
                    db.insertLectureTimeTableRecord(timeId,lecture,self.chromosome[timeId][lecture])

    def getChromosomeLength(self):
        return len(self.lectureTimeSlots) * len(self.lectureSubjects)