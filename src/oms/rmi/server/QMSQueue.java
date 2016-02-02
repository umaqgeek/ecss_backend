package oms.rmi.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms.rmi.db.Conn;

public class QMSQueue implements Runnable{
    final private long seconds = 1 * 5 * 1000;
    //final private long seconds = 2000;
    private MessageController controller;
    
    public QMSQueue(){
        controller = new MessageController();
    }
    
    public void run() {
        try{
            while(true){
                synchronized(this){
                    Timestamp ts = new Timestamp(new java.util.Date().getTime());
                    DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    
                    System.out.println("Log at ["+df.format(ts)+"]");
                    controller.insertEHRCentralFromQueue();
                    controller.insertPMSFromQueue();
                    Thread.sleep(seconds);
                }
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally{
            System.out.println("QMS End");
        }
    }
}
