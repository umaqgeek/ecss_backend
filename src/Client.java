

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import GreeterImpl;
//import _greeterimpl_Tie.*;
import javax.rmi.PortableRemoteObject;
import javax.naming.*;
import java.util.*;

/**
 *
 * @author Admin
 */
public class Client {

    public static void main(String[] args) {

        try {
            String s1 = "test";
            
            Hashtable hash1 = new Hashtable();
            hash1.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
            hash1.put("java.naming.provider.url", "iiop://localhost:900");
            
            Context context1 = new InitialContext(hash1);
            OMS obj = (OMS) PortableRemoteObject.narrow(context1.lookup("friza"), OMS.class);
            
            System.out.println("Start invoke");
            
            String pmi="PMS10003";
            String IC="891031075331";
            String data = "MSH|^~|CIS^001|<cr>"
                    + "PDI|PMS10003^LEE WEI CHUAN^891031075331^Chinese^Male^31/10/1989^AB^Married^|<cr>"
                    + "CCN|Knows the complaints procedure (finding)^Mild^Select One^2Minutes^Select One^ serious^Active^2012-08-03 13:12:47.17^|<cr>"
                    + "DGS|Able to perform recreational use of conversation (finding)^Mild^08/08/2012^2012-08-03 13:12:47.17^|<cr>"
                    + "IMU|Consent status or immunizations (finding)^as^02/08/2012^2012-08-03 13:12:47.17^|<cr>"
                    + "ALG|Allergic reaction to drug^10/08/2012^aaaa^2012-08-03 13:12:47.17^|<cr>"
                    + "SH|Cigar smoker^01/08/2012^heavy^2012-08-03 13:12:47.17^|<cr>";
            
            String pmsData = "MSH|^|PMS^T12108|HUTeM|OMS|HUTeM|03/08/2012 11:24:15|ST|03082012112415<cr>"
                + "ORC|T12108|PMS03082012112415|03/08/2012 11:24:15|03/08/2012 11:24:15|03/08/2012 11:24:15|Eric|Registraion|PMS|Registration|HUTeM<cr>"
                + "PDI|PMS10003|-|LEE WEI CHUAN|Mr|891031075331|-|-|-|Public|-|31/10/1989|Male|Married|Chinese|American|Buddha|AB|Positive|No|No|No|19 SOLOK INANGTAMAN BUNGA RAYAxxxx|TAIPING|GEORGE TOWN|81100|PULAU PINANG|SINGAPORE|-|19 SOLOK INANGTAMAN BUNGA RAYA|PEKAN|GEORGE TOWN|75060|PULAU PINANG|MALAYSIA|0165570889|ACTIVE|<cr>"
                + "EMP|PMS10003|ES10003|11447873|TAN LI HONG|SOFTWARE ENGINEER|03/06/2012|RM1500-RM2000|Hospital Ipoh|10/06/2012|ACTIVE|<cr>"
                + "NOK|PMS10003|NOKS10003|Father|JOHN LEE|651224087878|-|-|-|24/12/1965|SALESMAN|19, SOLOK INANG, TAMAN BUNGA RAYA, 14000, BUKIT "
                + "MERTAJAM, PULAU PINANG|KOTA TINGGI|GEORGE TOWN|81100|PULAU PENANG|MALAYSIA|0165571889|-|ACTIVE|<cr>"
                + "FMI|PMS10003|FS10003|Father|-|JOHN LEE|ACTIVE|<cr>"
                + "INS|PMS10003|ING Health Insurance|891112225|13/06/2013|Hospital Ipoh|ACTIVE|<cr>";
            
            
            
            //String s = obj.insertEHRCentral(pmi,data); // ok
            //String s = obj.greetme(s1);
            //List s = obj.getEHRLatest7(pmi); // ok
            List s = obj.getPMS(IC); //ok
            //String s = obj.insertPMS(pmsData); // ok
            
            System.out.println(s);
            
             List<String> pdi = (ArrayList) s.get(0);
            
            System.out.println("End invoke");
            
            System.out.println("..Start display..");
            
            
            
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}