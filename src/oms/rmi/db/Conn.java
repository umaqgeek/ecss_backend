package oms.rmi.db;

import java.sql.*;

/**
 *
 * @author WC
 */
public class Conn {

// public String url = "jdbc:mysql://localhost:3306/";
// public String dbName = "cissms";
// public String SqluserName = "root";
// public String password = "1234";
//    public static Connection HSQLconnect() throws ClassNotFoundException, SQLException {
//
//        Class.forName("org.hsqldb.jdbcDriver");
//        Connection Hsqlconn = DriverManager.getConnection("jdbc:hsqldb:file:lib/userdata/friza;shutdown=true", "SA", "");
//        System.out.println("HSQL Database connected");
//        return Hsqlconn;
//
//    }
    public static Connection MySQLConnect() throws ClassNotFoundException, SQLException, SQLTransientConnectionException {

        Class.forName("com.mysql.jdbc.Driver");
        // ;rewriteBatchedStatements=true for batchInsert
//        Connection SqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servercis", "root", "qwerty");
        Connection SqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servercis", "root", "");
//        Connection SqlConn = DriverManager.getConnection("jdbc:mysql://10.73.32.200:3306/servercis", "root", "qwerty");
        return SqlConn;
    }
    
    public static Connection ConnectPkomp(String db) throws ClassNotFoundException, SQLException, SQLTransientConnectionException {
        
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection SqlConn = DriverManager.getConnection("jdbc:sqlserver://"
                + "kutkm07.utem.edu.my;user=clinicapp;password=Cl1nic$app;database="+db);
        
        return SqlConn;
    }

    public static void main(String[] args) {
        try {

            Conn.MySQLConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}