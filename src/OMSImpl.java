
import oms.rmi.util.PMImsg;
import oms.rmi.db.Conn;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.rmi.PortableRemoteObject;
import javax.naming.*;
import java.rmi.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
//import java.rmi.server.*;
//import greeter.*;

public class OMSImpl extends PortableRemoteObject implements OMS {

    String s;

    public static void main(String args[]) {
        try {
            Context initialNamingContext = new InitialContext();
            //   System.setSecurityManager(new RMISecurityManager());
            OMSImpl obj = new OMSImpl("friza");
            initialNamingContext.rebind("friza", obj);
            System.out.println("remote server ready!");
            System.out.println("Server is waiting for call.");
        } catch (Exception e1) {
            System.out.println("error" + e1);
        }
    }

    public OMSImpl(String a) throws RemoteException {
        s = " ........ RMI-IIOP(RMI-CORBA) ..WELCOMES ";
    }

    public String greetme(String a) throws RemoteException {
        return s + a;
    }

    public String insertEHRCentral(String PMI, String data) throws RemoteException {


        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();

            Date date = new Date(new java.util.Date().getTime());
            //insert data into EHR_Central table
            PreparedStatement ps = conn.prepareStatement("INSERT INTO EHR_CENTRAL (PMI_NO,C_TXNDATE,C_TXNDATA)VALUES (?,?,?)");
            ps.setString(1, PMI);
            ps.setDate(2, date);
            ps.setString(3, data);
            ps.execute();
            System.out.println("..........Success Insert EHR Central!.........");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OMSImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OMSImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return PMI;
    }

    //Get 7 latest EHR
    public List getEHRLatest7(String pmi) throws RemoteException {
        Connection conn = null;
        List list = new ArrayList();
        try {
            System.out.println(".....getEHRLatest7....");
            conn = Conn.MySQLConnect();
            System.out.println(".....getEHRLatest7..1..");
            String sql = "select * from ehr_central where pmi_no=? order by c_txndate desc limit 7";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pmi);
            ResultSet rs = ps.executeQuery();
            System.out.println(".....getEHRLatest7..2..");
            String[] ehr = null;
            while (rs.next()) {
                ehr = new String[4];
                for (int i = 0; i < 4; i++) {
                    System.out.println("...record....:" + rs.getString(i + 1));

                    ehr[i] = rs.getString(i + 1);

                }
                //System.out.println("...Next Record....");
                list.add(ehr);

            }
            System.out.println("...Return lsit size...." + list.size());

//             for (int i = 0; i < list.size(); i++) {
//                 System.out.println(".extract..list....:" + list.get(i));
//                  System.out.println("***************");
//             }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

        return list;
    }

    public String insertPMS(String _Hl7mgs) {

        if (_Hl7mgs.startsWith("MSH")) {
            System.out.println("...PMS - Inserting......");


            // seperate the hl7 string 
            // according to segment
            String[] seperatedMSG = _Hl7mgs.split("<cr>");

            //chunk header
            String[] chunkheader = seperatedMSG[0].split("\\|");

            //get the transaction code
            String[] tranxcode = chunkheader[2].split("\\^");

            PMImsg pmi = new PMImsg(_Hl7mgs);

            if (tranxcode[1].equalsIgnoreCase("T12108")) {
                System.out.println(tranxcode[1] + "....start");

                pmi.PMIsaperator(); //here will insert PMS
                pmi.test();  //print out

            } else if (tranxcode[1].equalsIgnoreCase("T12109")) {
                //NOt sure whether need this. 
            /*
                MSH|^|CIS^T12108|KAJANG|CIS|HUTeM|03/08/2012 11:24:15|ST|03082012112415<c>
                #MSH|^~&|CIS^T0100| <cr>
                PDI|PMS10003|-|LEE WEI CHUAN|Mr|891031075331|-|-|-|Public|-
                #PDI|0123456789|Mohd Zamanhuri Abdullah|a0332322|660206-01-5261|….. <cr>
                HCS|AB|positif|….    <cr>
                EPI|20092002^HKJ^Outpatient^^^ ….<cr>
                PWS|x.101|Fever|episode-1^onsetDate-1^resolutionDate-1~episode-2^onsetDate-2^resolutionDate-2|Chronic 
                ind|drug-1^drugName-1~drug-2^drugName-2|proc-1^procName-1~ proc-2^procName-2<cr>
                DGS|20102012^11:50:35|x.101^fever|laterality code|comments ….<cr>
                CCN|20092002^11:50:35|x.102^fever~x.103^flu|severity code| ….<cr>
                VTS|20092002^11:50:35|x.102^fever~x.103^flu|severity code| ….<cr>
                EPI|20092002^HKJ^Outpatient^^^ ….<cr>
                PWS|x.101|Fever|episode-1^onsetDate-1^resolutionDate-1~episode-2^onsetDate-2^resolutionDate-2|Chronic 
                ind|drug-1^drugName-1~drug-2^drugName-2|proc-1^procName-1~ proc-2^procName-2<cr>
                DGS|20092002^11:50:35|x.101^fever|laterality code|comments ….<cr>
                CCN|20092002^11:50:35|x.102^fever~x.103^flu|severity code| ….<cr>
                VTS|20092002^11:50:35|x.102^fever~x.103^flu|severity code| ….<cr>
                
                 */
                //insert CIS to db server
                pmi.CISSeparator();
                //pmi.test();  //print out

            }

            for (int i = 0; i < chunkheader.length - 1; i++) {
                System.out.println(chunkheader[i]);
            }
        }
        return "Success Inserted PMS";
    }

    public List getPMS(String IC) {
        System.out.println("String received IC : " + IC);
        PMImsg pmi = new PMImsg();
        return pmi.getPMSData(IC);

    }
}
