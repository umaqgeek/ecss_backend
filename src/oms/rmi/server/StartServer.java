/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;


//import oms.rmi.server.MessageImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StartServer {
    
    private void startServer(){
        try {
            // create on port 1099
            Registry registry = LocateRegistry.createRegistry(FileCall.getPort());
            
            if (FileCall.getStatus() == 1) {
                System.setProperty("java.rmi.server.hostname", FileCall.getIpserver());
            }
            
            // create a new service named myMessage
            registry.rebind("myMessage", new MessageImpl());
            
            // start qms quese
            Thread t = new Thread(new QMSQueue());
            t.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready..");
    }
    
    public static void main(String[] args) {
        StartServer main = new StartServer();
        main.startServer();
    }
}

