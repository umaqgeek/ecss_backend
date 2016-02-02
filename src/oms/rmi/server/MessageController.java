package oms.rmi.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oms.rmi.db.Conn;
import oms.rmi.util.PMImsg;

public class MessageController {
    
    public void insertEHRCentralFromQueue() {
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();
            conn.setAutoCommit(false);
            
            PreparedStatement pre = conn.prepareStatement("SELECT * FROM EHR_CENTRAL_QUEUE WHERE QUEUE_STATUS = -1");
            ResultSet rs = pre.executeQuery();
            PreparedStatement batchInsert = null;
            PreparedStatement batchDelete = null;
            
            int count = 0;
            while(rs.next()){
                batchInsert = conn.prepareStatement("INSERT INTO EHR_CENTRAL (PMI_NO,C_TXNDATE,C_TXNDATA,STATUS)VALUES (?,?,?,?)");
                batchInsert.setString(1, rs.getString("PMI_NO"));
                batchInsert.setTimestamp(2, rs.getTimestamp("C_TXNDATE"));
                batchInsert.setString(3, rs.getString("C_TXNDATA"));
                batchInsert.setInt(4, rs.getInt("STATUS"));
                batchInsert.addBatch();
                
                batchDelete = conn.prepareStatement("DELETE FROM EHR_CENTRAL_QUEUE WHERE CENTRAL_CODE = ? ");
                batchDelete.setInt(1, rs.getInt("CENTRAL_CODE"));
                batchDelete.addBatch();
                
                if(++count % 100 == 0){
                    batchInsert.executeBatch();
                    batchDelete.executeBatch();
                }
            }
            if(count > 0){
                batchInsert.executeBatch();
                batchDelete.executeBatch();
            }
            conn.commit();
            rs.close();

        }  catch (SQLException e){
            try {
                if(conn != null){
                    conn.rollback();
                }
                throw e;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public void insertPMSFromQueue(){
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();
            conn.setAutoCommit(false);
            
            PreparedStatement pre = conn.prepareStatement("SELECT * FROM PMS_QUEUE WHERE QUEUE_STATUS = -1");
            ResultSet rs = pre.executeQuery();
            PreparedStatement batchInsert = null;
            PreparedStatement batchDelete = null;
            
            int count = 0;
            while(rs.next()){
                insertPMS(rs.getString("full_text"));
            }
            
            conn.commit();
            rs.close();

        }  catch (SQLException e){
            try {
                if(conn != null){
                    conn.rollback();
                }
                throw e;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    //******* 3) Insert PMS by pms_id (Register) *********//
    private void insertPMS(String _Hl7mgs) {

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
    }
}
