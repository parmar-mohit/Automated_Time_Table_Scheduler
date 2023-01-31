import pandas as pd

from DatabaseConnection import DatabaseConnection

db = DatabaseConnection()

# Time Info
query = "DELETE FROM time_info;"
db.executeUpdateQuery(query)

query = "INSERT INTO time_info VALUES(\"08:30:00\", \"16:15:00\",\"12:30:00\",\"13:15:00\");"
db.executeUpdateQuery(query)
print("Time Info Inserted")

# Time Slots
query = "DELETE FROM time_slots;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="TimeSlotsData")
for index in df.index:
    query = "INSERT INTO time_slots VALUES({},\"{}\",\"{}\",\"{}\")".format(
        df["Time Id"][index],
        df["Start Time"][index],
        df["End Time"][index],
        df["Day"][index]
    )
    db.executeUpdateQuery(query)

print("Time Slots Inserted")

# Course Data
query = "DELETE FROM course;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="CourseData")
for index in df.index:
    query = "INSERT INTO course VALUES(\"{}\",\"{}\",{},{})".format(
        df["Course Code"][index],
        df["Course Name"][index],
        df["Session / Week"][index],
        df["Session Duration"][index]
    )
    db.executeUpdateQuery(query)

print("Course Data Inserted")

# Class Data
query = "DELETE FROM class;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="ClassData")
for index in df.index:
    query = "INSERT INTO class VALUES({},{},\"{}\");".format(
        df["Class Id"][index],
        df["Year"][index],
        df["Division"][index]
    )
    db.executeUpdateQuery(query)

print("Class Data Inserted")

# Class Course Data
query = "DELETE FROM class_course;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="CourseClassData")
for index in df.index:
    query = "INSERT INTO class_course VALUES({},\"{}\");".format(
        df["Class Id"][index],
        df["Course Code"][index]
    )
    db.executeUpdateQuery(query)

print("Class Course Data Inserted")

# Teacher Data
query = "DELETE FROM teacher;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="TeacherData")
for index in df.index:
    query = "INSERT INTO teacher VALUES({},\"{}\",\"{}\");".format(
        df["Teacher Id"][index],
        df["Firstname"][index],
        df["Lastname"][index]
    )
    db.executeUpdateQuery(query)

print("Teacher Data Inserted")

# Teacher Course Preference Data
query = "DELETE FROM course_teacher;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="CourseTeacherData")
for index in df.index:
    query = "INSERT INTO course_teacher VALUES(\"{}\",{},{});".format(
        df["Course Code"][index],
        df["Teacher Id"][index],
        df["Preference"][index]
    )
    db.executeUpdateQuery(query)

print("Teacher Course Preference Data Inserted")

# Classroom Data
query = "DELETE FROM room;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="ClassroomData")
for index in df.index:
    query = "INSERT INTO room VALUES({},\"{}\");".format(
        df["Room Id"][index],
        df["Room Name"][index]
    )
    db.executeUpdateQuery(query)

print("Classroom Data Inserted")

# Classroom Course Data
query = "DELETE FROM course_rooms;"
db.executeUpdateQuery(query)

df = pd.read_excel("Test Data.xlsx",sheet_name="CourseClassroomData")
for index in df.index:
    query = "INSERT INTO course_rooms VALUES(\"{}\",{})".format(
        df["Course Code"][index],
        df["Room Id"][index]
    )
    db.executeUpdateQuery(query)

print("Classroom Course Data Inserted")
print("-----------------------------------")
print("Test Data Inserted Successfully")
