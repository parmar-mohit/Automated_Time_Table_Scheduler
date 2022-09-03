# Automated Time Table Scheduler

### About

The aim of this project is help schools,colleges, universities in thier process of making time table for respective organisations. Time Table scheduling is an dynamic problem which we intend to solve using genetic algorithm.

### Instruction to Execute Program
First Run the MySQL Queries mentioned in [MySQL Setup Queries](./Database/MySQl%20Setup%20Queries.md) then run the following commands to execute the program successfully
```
To Compile Files(Location : Project Directory)
cd src
javac -d ./out/production/Automated_Time_Table_Scheduler AutomatedTimeTableScheduler/Application.java

To Execute Program(Location : Project Directory)
java -classpath "./out/production/Automated_Time_Table_Scheduler;./Jar Files/MySQl JDBC Connector.jar" AutomatedTimeTableScheduler.Application
```

### Directory

[src](./src/) contains Source code for Program.

[Jar Files](./Jar%20Files/) contains additional dependencies for this project.

### Database

To efficiently store data this project uses RDBMS.The ER Diagram and Relational Schema for Database are given below
* [ER Diagram](./Database/ER%20Diagram.drawio.pdff)
* [Relational Schema](./Database/Relational%20Model.drawio.pdf) 

Additionally [MySQL Setup Queries](./Database/MySQl%20Setup%20Queries.md) has SQL command and Instructions for Setting up Database

### Technologies

* MySQl

To store the vast amount of data that this project has to handle we have used MySQl Database. 

* IntelliJ IDE ( Community Version )

To Develop this Project efficiently IntelliJ Community Version IDE is Used.

* Java Swing

To develop GUI interface so that application is interactive Java Swing API is used.