## MySQL Setup

### About
This file contains all the queries that must be executed in MySQL to setup the database for proper functioning of the server program.

1. Login into root account of MySQl to create a new Database, New MySQL Account and grant all permission to newly created account on newly created database.Execute the commands given below;
```
CREATE DATABASE timetable;

CREATE USER 'timetable_user' IDENTIFIED BY 'timetable_pass';

GRANT ALL PRIVILEGES ON timetable.* TO 'timetable_user';

FLUSH PRIVILEGES;
```

2. Login into MySQl using newly created Account and then execute the command given below to define database schema.
```
USE timetable;

CREATE TABLE class (
        class_id INT PRIMARY KEY AUTO_INCREMENT,
        year INT,
        division VARCHAR(1)
);

CREATE TABLE course (
        course_code VARCHAR(10) PRIMARY KEY,
        course_name VARCHAR(50),
        session_per_week INT,
        session_duration INT
);

CREATE TABLE room(
        room_id INT PRIMARY KEY AUTO_INCREMENT,
        room_name VARCHAR(50)
);

CREATE TABLE teacher(
        teacher_id INT PRIMARY KEY AUTO_INCREMENT,
        firstname VARCHAR(50),
        lastname VARCHAR(50)
);

CREATE TABLE time_info(
        college_start_time TIME,
        college_end_time TIME,
        break_start_time TIME,
        break_end_time TIME
);

CREATE TABLE class_course(
        class_id INT,
        course_code VARCHAR(50),
        PRIMARY KEY(class_id,course_code),
        FOREIGN KEY(class_id) REFERENCES class(class_id) ON DELETE CASCADE,
        FOREIGN KEY(course_code) REFERENCES course(course_code) ON DELETE CASCADE
);

CREATE TABLE course_rooms(
        course_code VARCHAR(50),
        room_id INT,
        PRIMARY KEY(course_code,room_id),
        FOREIGN KEY(course_code) REFERENCES course(course_code) ON DELETE CASCADE,
        FOREIGN KEY(room_id) REFERENCES room(room_id) ON DELETE CASCADE       
);

CREATE TABLE course_teacher(
        course_code VARCHAR(50),
        teacher_id INT,
        preference INT,
        PRIMARY KEY(course_code,teacher_id),
        FOREIGN KEY(course_code) REFERENCES course(course_code) ON DELETE CASCADE,
        FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id) ON DELETE CASCADE     
);

CREATE TABLE time_slots(
        time_id INT PRIMARY KEY AUTO_INCREMENT,
        start_time TIME,
        end_time TIME,
        day VARCHAR(10)
);

CREATE TABLE time_table(
    class_id INT,
    course_code VARCHAR(50),
    room_id INT,
    teacher_id INT,
    time_id INT,
    FOREIGN KEY(class_id) REFERENCES class(class_id) ON DELETE CASCADE,
    FOREIGN KEY(course_code) REFERENCES course(course_code) ON DELETE CASCADE,
    FOREIGN KEY(room_id) REFERENCES room(room_id) ON DELETE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id) ON DELETE CASCADE,
    FOREIGN KEY(time_id) REFERENCES time_slots(time_id) ON DELETE CASCADE
);
```

After these commands are executed successfully the mysql is setup and ready for the program.