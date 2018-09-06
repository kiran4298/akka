package com.sample.akka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.sample.model.Student;

public class FileWriterActor extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), "FileWriterActor");

    private Connection connection = null;


    @Override
    public void onReceive(Object message) throws Exception {
        Student student = (Student) message;

        if (connection == null || connection.isClosed()) {
            connection = this.getConnection();
        }

        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO student ( firstName , fullName , lastName  ) VALUES(?,?,?)");
        stmt.setString(1, student.getFirstName());
        stmt.setString(2, student.getFullName());
        stmt.setString(3, student.getLastName());
        
        stmt.executeUpdate();
        getSender().tell(Boolean.TRUE, getSelf());
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection =
                DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1433;databaseName=sampleDB;sendStringParametersAsUnicode=false", "admin", "password");

        return connection;
    }
}
