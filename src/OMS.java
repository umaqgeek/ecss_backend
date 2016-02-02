

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface OMS extends Remote {

    String greetme(String s) throws RemoteException;

    String insertEHRCentral(String pmi, String data) throws RemoteException;
    
    List getEHRLatest7(String pmi)  throws RemoteException;
    
    String insertPMS(String _Hl7mgs) throws RemoteException;
    
    List getPMS(String IC) throws RemoteException;
}