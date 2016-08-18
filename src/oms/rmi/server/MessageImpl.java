/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;

import DBConnection.PMI;
import Helper.S;
import Process.MainRetrieval;
import bean.VTSBean;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import oms.rmi.db.Conn;
import oms.rmi.util.PMImsg;

public class MessageImpl extends UnicastRemoteObject implements Message {

    public MessageImpl() throws RemoteException {
        super(Registry.REGISTRY_PORT);
        System.out.println("... Connected ...");
    }

    @Override
    public void sayHello(String name) throws RemoteException {
        System.out.println("hello " + name);
    }
    
    //******* 1)Insert into EHR Central - Discharge *********//
    public String insertEHRCentral(int status, String PMI, String data, String episodeDate) throws RemoteException {
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();
            
            long time = System.currentTimeMillis();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
            
            Date date = new Date(timestamp.getTime());
            
            System.out.println("date: "+date);
            System.out.println("timestamp: "+timestamp); //2014-03-06 12:50:30.798
            
            //insert data into EHR_Central table
            PreparedStatement ps = conn.prepareStatement("INSERT INTO EHR_CENTRAL_QUEUE (PMI_NO,C_TXNDATE,C_TXNDATA,STATUS,QUEUE_STATUS)VALUES (?,?,?,?,?)");
            ps.setString(1, PMI);
            ps.setString(2, episodeDate);
            ps.setString(3, data);
            ps.setInt(4, status);
            ps.setInt(5, -1);
            ps.execute();
            System.out.println("..........Success Insert EHR Central Queue!.........");

        }  catch (Exception ex) {
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

    //******* 2) Get 7 latest EHR CIS *********//
    public List getEHRLatest7(String pmi) throws RemoteException {
        Connection conn = null;
        List list = new ArrayList();
        try {
            //System.out.println(".....getEHRLatest7....");
            conn = Conn.MySQLConnect();
            //System.out.println(".....getEHRLatest7..1..");
            String sql = "select * from ehr_central where pmi_no=? order by c_txndate desc limit 7";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pmi);
            ResultSet rs = ps.executeQuery();
            //System.out.println(".....getEHRLatest7..2..");
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

    //******* 3) Insert PMS by pms_id (Register) *********//
    public String insertPMS(String _Hl7mgs) {
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();

            Date date = new Date(new java.util.Date().getTime());
            //insert data into EHR_Central table
            PreparedStatement ps = conn.prepareStatement("INSERT INTO PMS_QUEUE (FULL_TEXT,QUEUE_STATUS)VALUES (?,?)");
            ps.setString(1, _Hl7mgs);
            ps.setInt(2, -1);
            ps.execute();
            System.out.println("..........Success Insert PMS Central Queue!.........");

        }  catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        
        return "";
    }

    //******* 4) Get PMS by id - Lookup Online at PMS pg *********//
    public List getPMS(String IC) {
        System.out.println("String received IC : " + IC);
        PMImsg pmi = new PMImsg();
        return pmi.getPMSData(IC);

    }
    
    public List getPMSByPMINo(String PMI) {
        System.out.println("String received PMI : " + PMI);
        PMImsg pmi = new PMImsg();
        return pmi.getPMSDataByPMINo(PMI);

    }
    
    public List getPMSByID(String ID, String type) {
        System.out.println("String received ID : " + ID);
        PMImsg pmi = new PMImsg();
        return pmi.getPMSDataByID(ID, type);
    }
    
    public List getPMSByOldIC(String oldIC) {
        System.out.println("String received oldIC : " + oldIC);
        PMImsg pmi = new PMImsg();
        return pmi.getPMSDataByOldIC(oldIC);
    }
    
    public ArrayList<ArrayList<String>> getEHRLatestEpisode(String pmiNo, int limit) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM ehr_central "
                    + "WHERE PMI_NO = ? "
                    + "AND STATUS = 1 "
                    + "ORDER BY C_TXNDATE DESC "
                    + "LIMIT ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmiNo);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                try {
                    for (int i = 0; ; i++) {
                        d.add(rs.getString(i + 1));
                    }
                } catch (Exception ee) {
                }
                data.add(d);
            }
        } catch (Exception e) {
            System.out.println("Error getEHRLatestEpisode: " + e.getMessage());
        }
        return data;
    }

    public ArrayList<String> getEHRRecords(String pmiNo, int type) throws RemoteException {
        System.out.println("Get EHR Records");
        ArrayList<String> arStr = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs;
        String sql = "";
        String sql_history = "";
        String cpyFile = "";
        String cpyFile_history = "";
        String status = "1";
        String coldata[] = new String[100]; //100, maximum number of column
        try {
            conn = Conn.MySQLConnect();
            sql = "SELECT C_TxnData, STATUS "
                    + "FROM ehr_central "
                    + "WHERE PMI_NO = ? "
                    + "ORDER BY CENTRAL_CODE DESC "
                    + "LIMIT 1";
            sql_history = "SELECT C_TxnData "
                    + "FROM ehr_central "
                    + "WHERE PMI_NO = ? "
                    + "AND STATUS = 1 "
                    + "ORDER BY CENTRAL_CODE DESC "
                    + "LIMIT 7";
            coldata[0] = "C_TxnData";
            coldata[1] = "STATUS";

            /**
             * keluar pada form *
             */
            ps = conn.prepareStatement(sql);
            ps.setString(1, pmiNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                cpyFile = rs.getString(coldata[0]);
                status = rs.getString(coldata[1]);
            }

            /**
             * keluar pada 7 history *
             */
            ps = conn.prepareStatement(sql_history);
            ps.setString(1, pmiNo);
            rs = ps.executeQuery();
            while (rs.next()) {
                cpyFile_history += rs.getString(coldata[0]);
            }
        } catch (Exception e) {
            arStr.add("-");
            arStr.add(e.getMessage());
            System.out.println("Error RMI: " + e.getMessage());
        }
        arStr.add(cpyFile);
        arStr.add(status);
        arStr.add(cpyFile_history);
        return arStr;
    }
    
    public String firm_number(int num) {
        String numb = "";
        if(num < 10) {
            numb = "000000"+num;
        } else if(num < 100) {
            numb = "00000"+num;
        } else if(num < 1000) {
            numb = "0000"+num;
        } else if(num < 10000) {
            numb = "000"+num;
        } else if(num < 100000) {
            numb = "00"+num;
        } else if(num < 1000000) {
            numb = "0"+num;
        } else if(num < 10000000) {
            numb = ""+num;
        } else {
            numb = "0000001";
        }
        return numb;
    }
    
    private final static String status_new = "New";
    private final static String status_partial = "Partial";
    private final static String status_complete_partial = "Complete Partial";
    private final static String status_full = "Full Complete";
    
    public String insertDTOMaster(String PMI, String dataDTO) {
        Connection conn = null;
        String order_no = "0000000";
        
//        Calendar c = Calendar.getInstance();
//        int tahun = c.get(c.YEAR);
        java.util.Date tahunini = new java.util.Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yy");
        int tahun = Integer.parseInt(sdf1.format(tahunini));
//        String year = String.valueOf(tahun).split("")[3]+""
//                +String.valueOf(tahun).split("")[4];
        String year = sdf2.format(tahunini);
        
        MainRetrieval mr = new MainRetrieval();
        mr.startProcess(dataDTO);
        String dto[][] = mr.getData("DTO");
        int row_dto = mr.getRowNums();
        
        try {
            conn = Conn.MySQLConnect();
            
            String ayat = "SELECT MAX(ORDER_NO) AS MAX_ORDER_NO "
                    + "FROM PIS_ORDER_MASTER ";
//            String ayat = "SELECT MAX(ORDER_NO) AS MAX_ORDER_NO "
//                    + "FROM AUTOGENERATE_ONO ";
            PreparedStatement pss = conn.prepareStatement(ayat);
            ResultSet rs = pss.executeQuery();
            if(rs.next()) {
                String max_order_no = rs.getString("MAX_ORDER_NO") != null && !rs.getString("MAX_ORDER_NO").equals("")
                        && rs.getString("MAX_ORDER_NO").length() > 0 ? 
                        rs.getString("MAX_ORDER_NO") : 
                        year+"0000000";
                    order_no = max_order_no.substring(2,  
                        max_order_no.length());
                if(!max_order_no.substring(0,2).equals(year)) {
                    order_no = "0000000";
                }
            }
            try {
                if (order_no.equals("")) {
                    order_no = "0000000";
                }
            } catch(Exception ee) {
                order_no = "0000000";
            }
            
            int o_no = Integer.parseInt(order_no)+1 < 10000000 ? 
                    Integer.parseInt(order_no)+1 : 1;
            order_no = firm_number(o_no);
            order_no = year + order_no;
            
            Date date = new Date(new java.util.Date().getTime());
            //insert data into EHR_Central table
            ayat = "INSERT INTO PIS_ORDER_MASTER ("
                    + "ORDER_NO,PMI_NO, HEALTH_FACILITY_CODE,"
                    + "EPISODE_CODE, ENCOUNTER_DATE, "
                    + "ORDER_DATE, "
                    + "ORDER_BY, ORDER_FROM, "
                    + "ORDER_TO, HFC_FROM, "
                    + "HFC_TO, SPUB_NO, "
                    + "KEYIN_BY, TOTAL_ORDER, "
                    + "STATUS) VALUES (?";
            int soal = 15 - 1;
            while (soal-- != 0) {
                ayat += ",?";
            }
            ayat += ");";
            System.out.println("ayat: " + ayat);
            PreparedStatement ps = conn.prepareStatement(ayat);
            ps.setString(1, order_no);
            ps.setString(2, PMI);
            ps.setString(3, row_dto > 0 ? dto[0][29] : "");//HFC
            ps.setString(4, row_dto > 0 ? dto[0][0] : "0000-00-00");
            ps.setString(5, row_dto > 0 ? dto[0][0] : "0000-00-00");
            ps.setString(6, row_dto > 0 ? dto[0][0] : "0000-00-00");
            ps.setString(7, row_dto > 0 ? dto [0][31]: "DR001");//order by
            ps.setString(8, row_dto > 0 ? dto[0][33] :"");//order from
            ps.setString(9, "");//order to
            ps.setString(10, row_dto > 0 ? dto[0][35] :"");//hfc from
            ps.setString(11, "");//hfc to
            //ps.setDouble(12, 0.00);
            ps.setInt(12, 0);//spub_no
            ps.setString(13, "");//keyin by
            //ps.setDouble(14, 0.00);
            ps.setInt(14, 0);//total order
            ps.setInt(15, 0);//status
            ps.execute();
            System.out.println("..........Success Insert PIS Order Master!.........");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return order_no;
    }

    public String insertDTO(String PMI, String dataDTO) throws RemoteException {
        
        System.out.println("DTO: "+dataDTO);
        
        MainRetrieval mr = new MainRetrieval();
        mr.startProcess(dataDTO);
        String dto[][] = mr.getData("DTO");
        int rows = mr.getRowNums();
        
        for(int ii = 0; ii < rows; ii++) {
            for(int jj = 0; jj < dto[ii].length; jj++) {
                System.out.print(dto[ii][jj]+"|");
            }
        }
        System.out.println("");
        
        String order_no = insertDTOMaster(PMI, dataDTO);
        
        for(int i = 0; i < rows; i++) {
            Connection conn = null;
            try {
                conn = Conn.MySQLConnect();

                Date date = new Date(new java.util.Date().getTime());
                //insert data into EHR_Central table
                String ayat = "INSERT INTO PIS_ORDER_DETAIL ("
                        + "ORDER_NO, DRUG_ITEM_CODE,"
                        + "DRUG_ITEM_DESC,"
                        + "DRUG_FREQUENCY, DRUG_ROUTE,"
                        + "DRUG_FORM, DRUG_STRENGTH,"
                        + "DRUG_DOSAGE, ORDER_OUM,"
                        + "DURATION, ORDER_STATUS,"
                        + "QTY_ORDERED, QTY_SUPPLIED,"
                        + "SUPPLIED_OUM, QTY_DISPENSED,"
                        + "DISPENSE_OUM, STATUS) VALUES (?";
                int soal = 17 - 1;
                while (soal-- != 0) {
                    ayat += ",?";
                }
                ayat += ");";
                System.out.println("ayat: " + ayat);
                double a = 0.00;
                int f1;
                int d1;
                double qt1;
                //int qt1;
                String dType = null;
                
                PreparedStatement ps = conn.prepareStatement(ayat);
                ps.setString(1, order_no);
                ps.setString(2, dto[i][6]);//drug item code
                ps.setString(3, dto[i][5]);//drug item desc
                ps.setString(4, dto[i][14]);//frequency
                ps.setString(5, dto[i][14]);//drug route X
                ps.setString(6, dto[i][8]); //drug form
                ps.setString(7, dto[i][18]); //strenght
                ps.setString(8, dto[i][21]); //d dosage --Hariz not sure different vs strength
                ps.setString(9, dto[i][27]); //order oum 
                ps.setString(10, dto[i][22]); //duration
                ps.setString(11, "New");//order status        
                
                try {
                    a = Double.parseDouble(dto[i][23]);
                } catch(Exception ee) {}         
                
                ps.setDouble(12, a);//qty order               
                
                try {
                    a = Double.parseDouble(dto[i][23]);
                } catch(Exception ee) {}         
                
                ps.setDouble(13, a);//QTY_SUPPLIED                
                ps.setString(14, "-");//SUPPLIED_OUM                
                
                try {
                    a = Double.parseDouble(dto[i][21]);
                } catch(Exception ee) {}         
                
                ps.setDouble(15, a);  //QTY_DISPENSED = 0              
                ps.setString(16, "-");  //DISPENSE_OUM              
                ps.setInt(17, 0); //status               
                
                ps.execute();
                System.out.println("..........["+(i+1)+"] Success Insert PIS Order Detail!.........");

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }
        return PMI;
    }

    public ArrayList<ArrayList<String>> getOrderMasterAll(int stat, String pmi_no, String order_no, String hfc_code) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        try {

            Date date = new Date(new java.util.Date().getTime());
            //insert data into EHR_Central table
            String tamb = "";
            if (stat != 1) {
                switch (stat) {
                    case 2: {
                        tamb = "AND pom.pmi_no = ? ";
                    }
                    break;
                    case 3: {
                        tamb = "AND pom.pmi_no = ? AND pom.order_no = ? ";
                    }
                    break;
                }
            }
            String ayat = "SELECT * FROM pis_order_master pom, "
                    //+ "pis_order_detail pod, "
                    + "PMS_PATIENT_BIODATA ppb "
                    //+ "WHERE pom.ORDER_NO = pod.order_no "
                    + "WHERE DATE(ORDER_DATE) = DATE(CURDATE()) "
                    + "AND ppb.PMI_NO = pom.pmi_no "
                    + "AND pom.STATUS <> 1 "
                    + "AND pom.health_facility_code = ? "
                    + tamb
                    + "ORDER BY ORDER_DATE DESC "
                    + "LIMIT 30";
            
            S.oln(stat + "|" + pmi_no + "|" + order_no);
            S.oln(ayat);
            
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(ayat);
            ps.setString(1, hfc_code);
            if (stat != 1) {
                switch (stat) {
                    case 2: {
                        ps.setString(2, pmi_no);
                    }
                    break;
                    case 3: {
                        ps.setString(2, pmi_no);
                        ps.setString(3, order_no);
                    }
                    break;
                }
            }
            ResultSet rs = ps.executeQuery();
            for (int i = 0; i < 30 && rs.next(); i++) {
                ArrayList<String> d = new ArrayList<String>();
                for (int j = 0; j < 50; j++) {
                    d.add(rs.getString(j + 1));
                }
                data.add(d);
            }
            System.out.println("Get Order Master");
        } catch (Exception ex) {
            System.out.println("FAILED Get Order Master");
            ex.printStackTrace();
        }
        return data;
    }

    public void insertPatientBiodata(String[] biodata) throws RemoteException {
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();
            Date date = new Date(new java.util.Date().getTime());
            
            String sql = "INSERT INTO PMS_PATIENT_BIODATA (PMI_NO,PMI_NO_TEMP,"
                    + "PATIENT_NAME,TITLE_CODE,NEW_IC_NO,OLD_IC_NO,ID_TYPE,"
                    + "ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE,"
                    + "BIRTH_DATE,SEX_CODE,MARITAL_STATUS_CODE,RACE_CODE,"
                    + "NATIONALITY,RELIGION_CODE,BLOOD_TYPE,BLOOD_RHESUS_CODE,"
                    + "ALLERGY_IND,CHRONIC_DISEASE_IND,ORGAN_DONOR_IND,"
                    + "HOME_ADDRESS,HOME_DISTRICT_CODE,HOME_TOWN_CODE,"
                    + "HOME_POSTCODE,HOME_STATE_CODE,HOME_COUNTRY_CODE,"
                    + "HOME_PHONE,POSTAL_ADDRESS,POSTAL_DISTRICT_CODE,"
                    + "POSTAL_TOWN_CODE,POSTAL_POSTCODE,POSTAL_STATE_CODE,"
                    + "POSTAL_COUNTRY_CODE,MOBILE_PHONE) "
                    + "VALUES (?";
            for (int i = 0; i <= 33; i++) {
                sql += ",?";
            }
            sql += ")";
            PreparedStatement ps = conn.prepareStatement(sql);
            for(int i = 0; i <= 34; i++) {
                ps.setString(i+1, biodata[i]);
            }
            ps.execute();
            
            String sql2 = "INSERT INTO AUTOGENERATE_PMI (PMI_NO) VALUES (?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, biodata[0]);
            ps2.execute();
            S.oln("Insert PMI Success...");
        } catch(Exception e) {
            S.oln("Error: " + e.getMessage());
            e.printStackTrace();
            S.oln("Insert PMI FAILED!!...");
        }
    }

    public void insertRegCreateQ(String[] queue) throws RemoteException {
        Connection conn = null;
        try {
            conn = Conn.MySQLConnect();
            Date date = new Date(new java.util.Date().getTime());

            String sql = "INSERT INTO PMS_EPISODE (PMI_NO, EPISODE_DATE, NAME, "
                    + "NEW_IC_NO, OLD_IC_NO, ID_TYPE, ID_NO, "
                    + "RN_NO, PATIENT_CATEGORY_CODE, VISIT_TYPE_CODE, "
                    + "EMERGENCY_TYPE_CODE, "
                    + "ELIGIBILITY_CATEGORY_CODE, ELIGIBILITY_TYPE_CODE, "
                    + "DISCIPLINE_CODE, SUBDISCIPLINE_CODE, "
                    + "CONSULTATION_ROOM, COMMON_QUEUE, DOCTOR, "
                    + "PRIORITY_GROUP_CODE,POLICE_CASE,"
                    + "COMMUNICABLE_DISEASE_CODE,"
                    + "NATURAL_DISASTER_CODE,DOC_TYPE,GUARDIAN_IND,"
                    + "REFERENCE_NO,"
                    + "GROUP_GUARDIAN,GL_EXPIRY_DATE,EPISODE_TIME,STATUS,HEALTH_FACILITY_CODE)"
                    + " VALUES (?";
            for (int i = 0; i <= 28; i++) {
                sql += ",?";
            }
            sql += ")";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i <= 29; i++) {
                ps.setString(i + 1, queue[i]);
            }
            // execute query insert pms_episode
            ps.execute();
            
            String ppq_hfc_cd = queue[29];
            String ppq_queue_name = queue[16];
            String ppq_episode_date = queue[1];
            String ppq_user_id = "";
            String ppq_pmi_no = queue[0];
            
            int last_queue_no = 1;
            
            String sql_plqn = "SELECT last_queue_no "
                    + "FROM pms_last_queue_no "
                    + "WHERE hfc_cd = ? "
                    + "AND queue_name = ? "
                    + "AND DATE(episode_date) = DATE(?) "
                    + "ORDER BY last_queue_no DESC";
            PreparedStatement ps_plqn = conn.prepareStatement(sql_plqn);
            ps_plqn.setString(1, ppq_hfc_cd);
            ps_plqn.setString(2, ppq_queue_name);
            ps_plqn.setString(3, ppq_episode_date);
            ResultSet rs_plqn = ps_plqn.executeQuery();
            boolean isExist = rs_plqn.next();
            if (isExist) {
                try {
                    last_queue_no = Integer.parseInt(rs_plqn.getString("last_queue_no")) + 1;
                } catch (Exception e) {
                    last_queue_no = 1;
                }
            } else {
                last_queue_no = 1;
            }
            String sql_plqn2 = "";
            PreparedStatement ps_plqn2;
            if (isExist) {
                sql_plqn2 = "UPDATE pms_last_queue_no "
                        + "SET last_queue_no = ? "
                        + "WHERE hfc_cd = ? "
                        + "AND queue_name = ? "
                        + "AND DATE(episode_date) = DATE(?)";
            } else {
                sql_plqn2 = "INSERT INTO pms_last_queue_no(hfc_cd, queue_name, episode_date, last_queue_no) "
                        + "VALUES(?, ?, ?, ?) ";
            }
            ps_plqn2 = conn.prepareStatement(sql_plqn2);
            if (isExist) {
                ps_plqn2.setString(1, last_queue_no+"");
                ps_plqn2.setString(2, ppq_hfc_cd);
                ps_plqn2.setString(3, ppq_queue_name);
                ps_plqn2.setString(4, ppq_episode_date);
            } else {
                ps_plqn2.setString(1, ppq_hfc_cd);
                ps_plqn2.setString(2, ppq_queue_name);
                ps_plqn2.setString(3, ppq_episode_date);
                ps_plqn2.setString(4, last_queue_no+"");
            }
            // execute update or insert last queue no
            ps_plqn2.execute();
            
            // get queue type from queue name
            String sql_pqn = "SELECT queue_type "
                    + "FROM pms_queue_name "
                    + "WHERE queue_name = ? ";
            PreparedStatement ps_pqn = conn.prepareStatement(sql_pqn);
            ps_pqn.setString(1, ppq_queue_name);
            ResultSet rs_pqn = ps_pqn.executeQuery();
            String queue_type = "";
            if (rs_pqn.next()) {
                queue_type = rs_pqn.getString("queue_type");
            }
            
            String sql_ppq = "INSERT INTO pms_patient_queue(hfc_cd, queue_name, episode_date, pmi_no, queue_no, queue_type) "
                    + "VALUES(?, ?, ?, ?, ?, ?) ";
            PreparedStatement ps_ppq = conn.prepareStatement(sql_ppq);
            ps_ppq.setString(1, ppq_hfc_cd);
            ps_ppq.setString(2, ppq_queue_name);
            ps_ppq.setString(3, ppq_episode_date);
            ps_ppq.setString(4, ppq_pmi_no);
            ps_ppq.setString(5, last_queue_no+"");
            ps_ppq.setString(6, queue_type);
            // insert patient queue
            ps_ppq.execute();
            
            S.oln("Insert Queue Success...");
        } catch (Exception e) {
            S.oln("Error: " + e.getMessage());
            e.printStackTrace();
            S.oln("Insert Queue FAILED!!...");
        }
    }
    
    public String getPMINo(String search, String idtype, int type) {
        String pmino = "";
        try {
            
            String col_type = "PMI_NO=?";
            switch(type) {
                case 1:
                    col_type = "PMI_NO=?";
                    break;
                case 2:
                    col_type = "NEW_IC_NO=?";
                    break;
                case 3:
                    col_type = "OLD_IC_NO=?";
                    break;
                case 4:
                    col_type = "ID_NO=? AND ID_TYPE=?";
                    break;
            }

            String sql = "SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE "+col_type;
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, search);
            if (type == 4) {
                ps.setString(2, idtype);
            }
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pmino = rs.getString("PMI_NO");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return pmino;
    }

    public String[] getBio(int stat, String ic, String type, int num_col) throws RemoteException {
        Connection conn = null;
        int num_sii = 19;
        String bio[] = new String[num_col+num_sii];
        String sql = "";
        try {
            conn = Conn.MySQLConnect();
            Date date = new Date(new java.util.Date().getTime());
            
            String where = "NEW_IC_NO=?";
            switch (stat) {
                case 1: {
                    where = "NEW_IC_NO=?";
                }
                break;
                case 2: {
                    where = "PMI_NO=?";
                }
                break;
                case 3: {
                    where = "ID_NO=? AND ID_TYPE=?";
                }
                break;
                case 4: {
                    where = "OLD_IC_NO=?";
                }
                break;
                default: {
                    where = "NEW_IC_NO=?";
                }
                break;
            }
            if (stat == 2) {
                sql = "SELECT * FROM PMS_PATIENT_BIODATA "
                        + "WHERE " + where + "";
            } else {
                sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,"
                        + "ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,"
                        + "ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA "
                        + "WHERE " + where + "";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ic);
            if (stat == 3) {
                ps.setString(2, type);
            }
            ResultSet rs = ps.executeQuery();
            
            boolean check = false;

            if (rs.next()) {
                check = true;
                for (int i = 0; i < num_col; i++) {
                    System.out.println("gg:"+rs.getString(i + 1));
                    bio[i] = rs.getString(i + 1);
                }
            }
            
            // check smp
            where = "National_ID_No = ? ";
            if (stat == 1 || stat == 4 || (stat == 3 && type.contains("Foreigner"))) {
                where = "National_ID_No = ? ";
            } else if ((stat == 3 && type.contains("Matric")) || (stat == 3 && type.contains("Staff"))) {
                where = "Person_ID_No = ? ";
                if (type.contains("Matric")) {
                    where += " AND Person_Type = '1' ";
                } else if (type.contains("Staff")) {
                    where += " AND Person_Type = '0' ";
                }
            }
            sql = "SELECT * FROM Special_Integration_Information "
                    + "WHERE " + where;
            System.out.println("sql:"+sql);
            PreparedStatement ps1 = conn.prepareStatement(sql);
            ps1.setString(1, ic);
            ResultSet rs1 = ps1.executeQuery();
            ResultSetMetaData rsmd1 = rs1.getMetaData();
            int rs1ColsNum = rsmd1.getColumnCount();
            System.out.println("rs1ColsNum:"+rs1ColsNum);

            if (rs1.next()) {
                bio[num_col] = "1";
                System.out.println("num_sii:"+num_sii);
                for (int i = 1; i < num_sii; i++) {
                    try {
                        System.out.println("curr pos:"+(num_col+i));
                        bio[num_col+i] = rs1.getString(i);
                    } catch (Exception e) {
                        System.out.println("Error SMP:"+e.getMessage());
                    }
                }
            } else {
                bio[num_col] = "0";
            }
            
            if (check) {
                bio[num_col] += "1";
            } else {
                bio[num_col] += "0";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < bio.length; i++) {
            System.out.println(i+":"+bio[i]);
        }
        
        return bio;
    }

    public String[] getAutoGen(int stat) throws RemoteException {
        String[] g = new String[1];
        try {
            //Connection conn = DbConnection.doConnection();
            String sql = "SELECT PMI_NO FROM AUTOGENERATE_PMI";
            switch (stat) {
                case 1: {
                    sql = "SELECT PMI_NO FROM AUTOGENERATE_PMI";
                }
                break;
                case 2: {
                    sql = "SELECT FAMILY_SEQ_NO FROM AUTOGENERATE_FSNO";
                }
                break;
                case 3: {
                    sql = "SELECT NEXTOFKIN_SEQ_NO FROM AUTOGENERATE_NOKSNO";
                }
                break;
                case 4: {
                    sql = "SELECT EMPLOYMENT_SEQ_NO FROM AUTOGENERATE_ESNO";
                }
                break;
                case 5: {
                    sql = "SELECT RECEIPT_NO FROM AUTOGENERATE_RECNO";
                }
                break;
                default: {
                    sql = "SELECT PMI_NO FROM AUTOGENERATE_PMI";
                }
            }
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            String pmi = null;
            while (rs.next()) {
                pmi = rs.getString(1);
                int num = 0;
                switch (stat) {
                    case 1: {
                        num = Integer.parseInt(pmi.substring(3, pmi.length()));
                    }
                    break;
                    case 2: {
                        num = Integer.parseInt(pmi.substring(2, pmi.length()));
                    }
                    break;
                    case 3: {
                        num = Integer.parseInt(pmi.substring(4, pmi.length()));
                    }
                    break;
                    case 4: {
                        num = Integer.parseInt(pmi.substring(2, pmi.length()));
                    }
                    break;
                    case 5: {
                        num = Integer.parseInt(pmi.substring(3, pmi.length()));
                    }
                    break;
                    default: {
                        num = Integer.parseInt(pmi.substring(3, pmi.length()));
                    }
                    break;
                }
                num += 1;
                String formatted = String.format("%05d", num);
                g[0] = formatted;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return g;
    }
    
    private static boolean isDateToday(String date) {
        boolean stat = false;
        try {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = dateFormat.format(today.getTime());
            String str1x[] = date.split(" ");
            String str1[] = str1x[0].split("-");
            String day1 = str1[2];
            String month1 = str1[1];
            String year1 = str1[0];
            String str2x[] = now.split(" ");
            String str2[] = str2x[0].split("-");
            String day2 = str2[2];
            String month2 = str2[1];
            String year2 = str2[0];
            if (day1.equals(day2) && month1.equals(month2) && year1.equals(year2)) {
                stat = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            stat = false;
        }
        return stat;
    }

    public Vector getQueueNameList(String name, String hfcCode, int tanda) throws RemoteException {
        Vector<Vector<String>> QueueVector = new Vector<Vector<String>>();
        try {
            String sql = "SELECT "
                    
                    + "PE.PMI_NO," //1
                    + "PE.NAME," //2
                    + "PE.EPISODE_TIME," //3
                    + "PE.CONSULTATION_ROOM," //4
                    + "PE.DOCTOR," //5
                    + "PE.STATUS," //6
                    + "PE.EPISODE_DATE, " //7
                    + "PPQ.QUEUE_NAME, " //8
                    + "PPQ.QUEUE_NO " //9
                    
                    + "FROM PMS_EPISODE PE, PMS_PATIENT_QUEUE PPQ "
                    + "WHERE PE.HEALTH_FACILITY_CODE = PPQ.HFC_CD "
                    + "AND PE.EPISODE_DATE = PPQ.EPISODE_DATE "
                    + "AND PE.PMI_NO = PPQ.PMI_NO "
                    + "AND PE.NAME LIKE upper(?) "
                    + "AND PE.HEALTH_FACILITY_CODE = ? "
                    //+ "AND STATUS NOT LIKE 'Consult' "
                    + "AND PE.STATUS NOT LIKE 'Discharge' "
                    + "AND PE.STATUS NOT LIKE 'Missing' "
                    + "ORDER BY PE.EPISODE_TIME ASC";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            name = "%" + name + "%";
            ps.setString(1, name);
            ps.setString(2, hfcCode);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                if(isDateToday(rs.getString(7))) {
                    Vector<String> queue = new Vector<String>();
                    queue.add(rs.getString(1));//pmino
                    queue.add(rs.getString(2));//name
                    queue.add(rs.getString(3));//time
                    queue.add(rs.getString(8));//queue name
                    queue.add(rs.getString(9));//queue no
                    queue.add(rs.getString(5));//doctor
                    queue.add(rs.getString(6));//status
                    
                    if (tanda == 2) {
                        queue.add("DELETE");
                    }

                    QueueVector.add(queue);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return QueueVector;
    }
    
    public Vector getQueueCallingSystem() throws RemoteException {
        Vector<Vector<String>> QueueVector = new Vector<Vector<String>>();
        try {
            String sql = "SELECT PMI_NO,NAME,EPISODE_TIME,CONSULTATION_ROOM,DOCTOR,"
                    + "STATUS,EPISODE_DATE FROM PMS_EPISODE "
                    + "ORDER BY STATUS DESC, EPISODE_TIME ASC";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            int i = 1;
            while (rs.next()) {
                if (isDateToday(rs.getString(7))) {
                    if (!rs.getString(6).equals("Consult")) {
                        Vector<String> queue = new Vector<String>();
                        queue.add(i + ".");//queue no
                        queue.add(rs.getString(2));//name
                        queue.add(rs.getString(3));//time
                        queue.add(rs.getString(5));//doctor

                        QueueVector.add(queue);
                    }
                    i++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return QueueVector;
    }

    public void updateStatEpisode(String PMINumber, String TimeEpisode, 
            String status, String doctor, String referer) 
            throws RemoteException {
        try {
            String plussql = "";
            if (status.equals("Consult") || status.equals("Hold") || status.equals("Second Opinion")) {
                plussql = ", DOCTOR=?";
            }
            PreparedStatement statement2 = 
                    Conn.MySQLConnect().prepareStatement(""
                    + "UPDATE PMS_EPISODE "
                    + "SET STATUS=?"
                    + plussql + " "
                    + "WHERE PMI_NO=? "
                    + "AND EPISODE_TIME=?");
            statement2.setString(1, status);
            if (status.equals("Consult") || status.equals("Hold") || status.equals("Second Opinion")) {
                if (status.equals("Second Opinion")) {
                    doctor = referer;
                }
                statement2.setString(2, doctor);
                statement2.setString(3, PMINumber);
                statement2.setString(4, TimeEpisode);
            } else {
                statement2.setString(2, PMINumber);
                statement2.setString(3, TimeEpisode);
            }
            statement2.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void updateStatEpisode2(String PMINumber, String TimeEpisode, String now) 
            throws RemoteException {
        try {
            PreparedStatement statement2 = Conn.MySQLConnect().prepareStatement("UPDATE PMS_EPISODE "
                    + "SET STATUS='Hold' "
                    + "WHERE PMI_NO=? "
                    + "AND EPISODE_TIME=? "
                    + "AND EPISODE_DATE=?");
            statement2.setString(1, PMINumber);
            statement2.setString(2, TimeEpisode);
            statement2.setString(3, now);
            statement2.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
//begin Appoinment
    public Vector<String> getAppointment(String ID) throws RemoteException {
        Vector<String> details = new Vector<String>();
        try {
            String sql = "SELECT AL.*, PB.NEW_IC_NO FROM PMS_APPOINTMENT_LIST AL,PMS_PATIENT_BIODATA PB  where APPOINTMENT_ID = ? and AL.PMI_NO = PB.PMI_NO";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                for (int i = 0; i < 11; i++) {
                    details.add(rs.getString(i + 1));
                }

            }
            //hconn.close();
            return details;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
        return details;
        }
    }

    public String getAutoAppointID() throws RemoteException {
        String AutogenerateID = new String();
        try {
            String sql = "SELECT APPOINTMENT_ID FROM PMS_APPOINTMENT_LIST";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            String id = null;
            while (rs.next()) {
                {
                    id = rs.getString(1);
                }

                int num = Integer.parseInt(id.substring(1, id.length()));
                num += 1;

                String formatted = String.format("%05d", num);
                AutogenerateID = formatted;
            }

            //hconn.close();
            return AutogenerateID;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            return AutogenerateID;
        }
    }

    public boolean deleteAppointment(String appID) throws RemoteException {
        boolean status = false;
        try {
                String sql = "Delete From PMS_APPOINTMENT_LIST where APPOINTMENT_ID = ?";
                PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                ps.setString(1, appID);
                int s =ps.executeUpdate();
                if(s>0){
                    status = true;                
                }else{
                    status=false;       
                }
            
                //hconn.close();
            
                return status;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            return status;
        }
    }

    public boolean makeAppointment(Vector<String> patient) throws RemoteException {
        boolean makeStat=false;
        try {
            
                String sql = "INSERT INTO PMS_APPOINTMENT_LIST(APPOINTMENT_ID,"
                        + "APPOINTMENT_DATE, APPOINTMENT_TIME, PMI_NO, "
                        + "PATIENT_NAME, DOCTOR_NAME, LOCATION_NAME, "
                        + "DISCIPLINE, SUBDISCIPLINE, APPOINTMENT_TYPE) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                ps.setString(1, patient.elementAt(0));
                ps.setString(2, patient.elementAt(1));
                ps.setString(3,patient.elementAt(2));
                ps.setString(4, patient.elementAt(3));
                ps.setString(5, patient.elementAt(4));
                ps.setString(6, patient.elementAt(5));
                ps.setString(7, patient.elementAt(6));
                ps.setString(8,patient.elementAt(7));
                ps.setString(9, patient.elementAt(8));
                ps.setString(10,patient.elementAt(9));
                int m = ps.executeUpdate();

                if(m>0){
                   makeStat = true;                
                }else{
                   makeStat=false;       
                }
              S.oln("Success insert appoinment online");
             return makeStat;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
        return makeStat;
        }
    }

    public boolean updateAppointment(Vector<String> patient) throws RemoteException {
        boolean updatestat=false;
        try {
            
                 String sql = "Update PMS_APPOINTMENT_LIST set APPOINTMENT_DATE = ?,APPOINTMENT_TIME = ?,PMI_NO=?,PATIENT_NAME=?,"
                         + "DOCTOR_NAME=?,LOCATION_NAME=?,DISCIPLINE=?,SUBDISCIPLINE=?,APPOINTMENT_TYPE=? where APPOINTMENT_ID = ?";
                 PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                 ps.setString(10, patient.elementAt(0));
                 ps.setString(1, patient.elementAt(1));
                 ps.setString(2,patient.elementAt(2));
                 ps.setString(3, patient.elementAt(3));
                 ps.setString(4, patient.elementAt(4));
                 ps.setString(5, patient.elementAt(5));
                 ps.setString(6, patient.elementAt(6));
                 ps.setString(7,patient.elementAt(7));
                 ps.setString(8, patient.elementAt(8));
                 ps.setString(9,patient.elementAt(9));
                 int m = ps.executeUpdate();

                 if(m>0){
                    updatestat = true;                
                 }else{
                    updatestat=false;       
                 }
             
                 return updatestat;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            return updatestat;
        }
    }

    public Vector<String> getpatientInfoPMI(String PMI) throws RemoteException {
        Vector<String> patinfo = new Vector<String>();
        try {
            
              String sql= "SELECT PMI_NO, PATIENT_NAME, NEW_IC_NO "
                      + "FROM PMS_PATIENT_BIODATA "
                      + "WHERE PMI_NO=Upper(?)";
              PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
              ps.setString(1, PMI);
              ResultSet rs = ps.executeQuery();
              while(rs.next()){
                  patinfo.add(rs.getString(1));
                  patinfo.add(rs.getString(2));
                  patinfo.add(rs.getString(3));
              }
             
            return patinfo;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            return patinfo;
        }
    }

    public Vector<String> getpatientInfoIC(String IC) throws RemoteException {
        Vector<String> patinfobyic = new Vector<String>();
        try {
            
             String sql= "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=? or OLD_IC_NO = ?";
             PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
             ps.setString(1,IC);
             ps.setString(2,IC);
             ResultSet rs = ps.executeQuery();
             while(rs.next()){
                 patinfobyic.add(rs.getString(1));
                 patinfobyic.add(rs.getString(2));
                 patinfobyic.add(rs.getString(3));
             }
            
           return patinfobyic;
        } catch (Exception ex) {
            Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            return patinfobyic;
        }
    }
//end of Appoinment

    public void insertOrder(String[] order) throws RemoteException {
        try{
            String sql = "INSERT INTO AUTOGENERATE_ONO (ORDER_NO) VALUES (?)";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, order[0]);
            ps.execute();
            
            String sql2 = "INSERT INTO PIS_ORDER_MASTER "
                    + "(order_no, pmi_no,health_facility_code,episode_code,encounter_date,"
                    + "order_date, order_by,order_from, order_to, "
                    + "hfc_from, hfc_to, spub_no, keyin_by, "
                    + "total_order, status) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql2);
            for (int i = 0; i <= 14; i++) {
                ps2.setString(i + 1, order[i]);
            }
            ps2.execute();
            
            String sql3 = "INSERT INTO PIS_ORDER_DETAIL "
                    + "(order_no,drug_item_code,drug_item_desc,"
                    + "drug_frequency,drug_route,drug_form,drug_strength,"
                    + "drug_dosage,order_oum,duration,order_status ,qty_ordered,"
                    + "qty_supplied,supplied_oum,qty_dispensed,dispense_oum,status) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement ps3 = Conn.MySQLConnect().prepareStatement(sql3);
            for (int i = 0; i <= 16; i++) {
                ps3.setString(i + 1, order[i]);
            }
            ps3.execute();


            
            S.oln("Insert Order Success...");
        }catch(Exception e){
            
        }
       
    }

    public void insertD(String[] dispense) throws RemoteException {
        try{
            String sql ="INSERT INTO PIS_DISPENSE_MASTER"
                           + "(order_no, order_date, location_code, arrival_date, dispensed_date, dispensed_by ,"
                           + "filled_by, screened_by, assigned_by, status)"
                           + "VALUES ('"+ dispense[0] + "','"+ dispense[1] +"','" + dispense[2]+"',"
                                   + "'"+ dispense[3] +"','"+ dispense[4] +"','"+ dispense[5] +"',"
                                   + "'"+ dispense[6] +"','"+ dispense[7] +"','"+ dispense[8] + "',"
                                   + "'"+ dispense[9]+"','" + dispense[10]+"')";
			
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            for (int i = 0; i <= 10; i++) {
                ps.setString(i + 1, dispense[i]);
            }
            ps.execute();
            
            String sql2 = "INSERT INTO PIS_DISPENSE_DETAIL"
                           + "(order_no,drug_item_code,dispensed_qty,dispensed_uom ,status)"
                           + "VALUES ('"+ dispense[11] + "','"+ dispense[12] +"','" + dispense[13]+"',"
                                   + "'"+ dispense[14] +"','"+ dispense[15] +"')";
            PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql2);
            for (int i = 0; i <= 5; i++) {
                ps2.setString(i + 1, dispense[i]);
            }
            ps2.execute();
            
        }catch(Exception e)
        {
            
        }
    }

    public ArrayList<String> getPatientBiodata(String selectedPmiNo) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PMI_NO = ?";

            //prepare the sql query and execute it
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, selectedPmiNo);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                //read data get from database to all fields
                data.add(result.getString("Patient_Name"));
                data.add(result.getString("PMI_No"));
                data.add(result.getString("New_IC_No"));
                data.add(result.getString("Sex_Code"));
                data.add(result.getString("Birth_Date"));
                data.add(result.getString("Race_Code"));
                data.add(result.getString("Blood_Type"));
                data.add(result.getString("Allergy_Ind"));
            }
            //clean the results and data
            ps.close();
            result.close();

        } catch (Exception ex) {
            System.out.println("S PMS_P_BIODATA " + ex.getMessage());
        }
        return data;
    }

    public ArrayList<String> getPisOrderMaster(String selectedPmiNo, String orderDate) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM PIS_ORDER_MASTER WHERE PMI_NO = ? "
                    + "AND ORDER_DATE = ?";
            //prepare the sql query and execute it
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, selectedPmiNo);
            ps.setString(2, orderDate);
            //ps.setString(2,selectedoNo);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                //read data get from database to all fields

                data.add(result.getString("ORDER_BY"));
                data.add(result.getString("ORDER_NO"));
                data.add(result.getString("ORDER_DATE"));
                data.add(result.getString("HEALTH_FACILITY_CODE"));
//                data.add(result.getString("HFC_FROM"));

            }
            //clean the results and data
            ps.close();
            result.close();

        } catch (Exception ex) {
            System.out.println("S po_master" + ex.getMessage());
        }
        return data;
    }

    public ArrayList<ArrayList<String>> getDrugOrderDetail(String orderNo) {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            try {
                String sql = "SELECT * FROM PIS_ORDER_DETAIL WHERE ORDER_NO = ? "
                        + "AND STATUS = 0 ";

                //prepare the sql query and execute it
                PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                ps.setString(1, orderNo);
                ResultSet result = ps.executeQuery();

                while (result.next()) {
                    //read data get from database to all fields

                    ArrayList<String> d = new ArrayList<String>();
                    String oNo = result.getString("ORDER_NO");
                    String productCode = result.getString("DRUG_ITEM_CODE");
                    String productName = result.getString("DRUG_ITEM_DESC");
                    String frequency = result.getString("DRUG_FREQUENCY");
                    String route = result.getString("DRUG_ROUTE");
                    String dosageForm = result.getString("DRUG_FORM");
                    String strength = result.getString("DRUG_STRENGTH");
                    String dosage = result.getString("DRUG_DOSAGE");
                    String instruction = result.getString("ORDER_OUM");
                    String duration = result.getString("Duration");
                    //Double duration1 = Double.parseDouble(duration);
                    int duration1 = Integer.parseInt(duration);

                    int qtyPerTime1 = result.getInt("QTY_ORDERED");
                    String qtyPerTime = Integer.toString(qtyPerTime1);
                    int totalQty = result.getInt("QTY_SUPPLIED");
                    //totalQty1 = Integer.toString(totalQty);
                    String qtydispensed = result.getString("QTY_DISPENSED");
                    //int qtydispensed1= Integer.parseInt(qtydispensed);
                    String oSD = result.getString("STATUS"); //true false
                    String orderStatus = result.getString("ORDER_STATUS"); //new partial full
                    //                    //identify frequency
                    //                    if ((frequency.equals("In the morning")) || (frequency.equals("At night")) || (frequency.equals("Daily"))) {
                    //                        frequency1 = 1;
                    //                    } else if (frequency.equals("Twice a day")) {
                    //                        frequency1 = 2;
                    //                    } else if ((frequency.equals("3 times a day")) || (frequency.equals("8 hourly"))) {
                    //                        frequency1 = 3;
                    //                    } else if ((frequency.equals("4 times a day")) || (frequency.equals("6 hourly"))) {
                    //                        frequency1 = 4;
                    //                    } else {
                    //                        frequency1 = 6;
                    //                    }
                    //
                    //                    //calculate total quantity
                    //                    totalQty = frequency1 * qtyPerTime1 * duration1;
                    String totalQty1 = Double.toString(totalQty);
                    int stockQty1 = 0;
                    String stockQty = "Available";
                    
                    String generic_name = "";

                    try {
//                        String sql1 = "SELECT Drug_Product_Name, Def_Route_Code "
//                                + "FROM PIS_MDC "
//                                + "WHERE UD_MDC_Code = ?";
                        String sql1 = "SELECT * "
                                + "FROM PIS_MDC2 "
                                + "WHERE UD_MDC_CODE = ? ";

                        //prepare the sql query and execute it
                        PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
                        ps1.setString(1, productCode);
                        ResultSet result1 = ps1.executeQuery();

                        while (result1.next()) {
                            //read data get from database to all fields
//                            productName = result1.getString("Drug_Product_Name");
//                            route = result1.getString("Def_Route_Code");
//                            stockQty1 = result1.getInt("STOCK_QTY");
                            productName = result1.getString("D_TRADE_NAME");
                            route = result1.getString("D_ROUTE_CODE");
                            stockQty1 = result1.getInt("D_STOCK_QTY");
                            generic_name = result1.getString("D_GNR_NAME");

                            if (stockQty1 != 0) {
                                stockQty = "Available";
                            } else {
                                stockQty = "Pending";
                                //stockQty = "Not Available";
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("dbc 1 " + ex.getMessage());
                    }

                    d.add(productCode); //0
                    d.add(productName); //1
                    d.add(frequency); //2
                    d.add(route); //3
                    d.add(dosageForm); //4
                    d.add(strength); //5
                    d.add(dosage); //6
                    d.add(instruction); //7
                    d.add(duration); //8
                    d.add(qtyPerTime); //9
                    d.add(totalQty1); //10
                    d.add(qtydispensed); //11
                    d.add(orderStatus); //12
                    d.add(stockQty1 + ""); //13
                    d.add(stockQty + ""); //14
                    
                    d.add(generic_name); //15

                    data.add(d);
                }
                //clean the results and data
                ps.close();
                result.close();

            } catch (Exception ex) {
                System.out.println("S po_master" + ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<ArrayList<String>> getEhrCentral(String pmino) throws RemoteException {
        System.out.println("Get EHR Central ..");
        String str = "";
        if(!pmino.equals("")) {
            str = "AND PMI_NO LIKE ? ";
        }
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM ehr_central "
                    + "WHERE STATUS = 1 "
                    + str
                    + "ORDER BY C_TXNDATE DESC ";

            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            if (!pmino.equals("")) {
                ps.setString(1, pmino);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                d.add(""+rs.getInt(1));
                d.add(rs.getString(2));
                d.add(rs.getString(3));
                d.add(rs.getString(4));
                d.add(""+rs.getInt(5));
                data.add(d);
            }
            System.out.println("Get EHR Central Success!");
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList<String> d = new ArrayList<String>();
            d.add("ERROR");
            d.add(e.getMessage());
            data.add(d);
        }
        return data;
    }
    
    public ArrayList<ArrayList<String>> getEhrCentral2(String pmino) throws RemoteException {
        System.out.println("Get EHR Central ..");
        String str = "";
        if(!pmino.equals("")) {
            str = "AND PMI_NO LIKE ? ";
        }
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM ehr_central "
                    + "WHERE STATUS = 1 "
                    + str
                    + "ORDER BY C_TXNDATE ASC ";

            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            if (!pmino.equals("")) {
                ps.setString(1, pmino);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                d.add(""+rs.getInt(1));
                d.add(rs.getString(2));
                d.add(rs.getString(3));
                d.add(rs.getString(4));
                d.add(""+rs.getInt(5));
                data.add(d);
            }
            System.out.println("Get EHR Central Success!");
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList<String> d = new ArrayList<String>();
            d.add("ERROR");
            d.add(e.getMessage());
            data.add(d);
        }
        return data;
    }

    public ArrayList<String> getDispenseMaster(String orderNo) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM PIS_DISPENSE_MASTER WHERE ORDER_NO = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= 10; i++) {
                    data.add(rs.getString(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public boolean insertDispenseMaster(String[] data1, 
            String data2, boolean data3) throws RemoteException {
        boolean stat = false;
        try {
            String in1 = "INSERT INTO PIS_DISPENSE_MASTER"
                    + "(order_no, order_date, location_code, "
                    + "arrival_date, dispensed_date, dispensed_by, "
                    + "filled_by, screened_by, assigned_by, status)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ins1 = Conn.MySQLConnect().prepareStatement(in1);
            ins1.setString(1, data1[0]);
            ins1.setString(2, data1[1]);
            ins1.setString(3, data1[2]);
            ins1.setString(4, data1[3]);
            ins1.setString(5, data2);
            ins1.setString(6, data1[4]);
            ins1.setString(7, data1[5]);
            ins1.setString(8, data1[6]);
            ins1.setString(9, data1[7]);
            ins1.setBoolean(10, data3);
            stat = ins1.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public ArrayList<String> getOrderDetail(String orderNo, 
            String drugCode) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM PIS_ORDER_DETAIL WHERE ORDER_NO = ? "
                    + "AND DRUG_ITEM_CODE = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, orderNo);
            ps.setString(2, drugCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= 17; i++) {
                    data.add(rs.getString(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public boolean insertDispenseDetail(String[] data1, int data2, 
            boolean data3) throws RemoteException {
        boolean stat = false;
        try {
            String in2 = "INSERT INTO PIS_DISPENSE_DETAIL"
                    + "(order_no,drug_item_code,dispensed_qty,dispensed_uom ,status)"
                    + "VALUES (?,?,?,?,?)";
            PreparedStatement ins2 = Conn.MySQLConnect().prepareStatement(in2);
            ins2.setString(1, data1[0]);//ono
            ins2.setString(2, data1[1]);
            ins2.setInt(3, data2);
            ins2.setString(4, data1[2]);
            ins2.setBoolean(5, data3);//true
            stat = ins2.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean updateOrderDetail(int qtyDispensed, String orderNo, 
            String drugCode, String statusDrug) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "SELECT QTY_ORDERED, QTY_DISPENSED "
                    + "FROM PIS_ORDER_DETAIL "
                    + "WHERE ORDER_NO = ? "
                    + "AND DRUG_ITEM_CODE = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, orderNo);
            ps.setString(2, drugCode);
            ResultSet rs = ps.executeQuery();
            
            int old_qty_dispensed = 0;
            int old_qty_ordered = 0;
            int drugQty = 0;
            
            if (rs.next()) {
                old_qty_ordered = rs.getInt("QTY_ORDERED");
                old_qty_dispensed = rs.getInt("QTY_DISPENSED");
            }
            //old_qty_dispensed += qtyDispensed; //disabled by - hadi
            if (qtyDispensed >= old_qty_dispensed) { //pharmacist dispensed same or more druq quantity than set by the doctor - hadi
                // * * * * * * * * * * * * * * * * *
                try{
                    String sql2 = "SELECT * "
                            + "FROM PIS_MDC2 "
                            + "WHERE UD_MDC_CODE = ?";
                    PreparedStatement ps3 = Conn.MySQLConnect().prepareStatement(sql2);
                    ps3.setString(1, drugCode);
                    ResultSet rs2 = ps3.executeQuery();
                    while (rs2.next()){
                        drugQty = rs2.getInt("D_STOCK_QTY");
                        drugQty -=(qtyDispensed);
                    }
                    try{
                        String sql3 = "UPDATE PIS_MDC2 "
                                + "SET D_STOCK_QTY = ? "
                                + "WHERE UD_MDC_CODE = ? ";
                        PreparedStatement ps4 = Conn.MySQLConnect().prepareStatement(sql3);
                        ps4.setInt(1, drugQty);
                        ps4.setString(2, drugCode);
                        stat = ps4.execute();
                        S.oln("success update PIS_MDC :"+drugQty);
                        
                    }catch(Exception vv){
                        vv.printStackTrace();
                        
                    }
                }catch(Exception qq){
                    qq.printStackTrace();
                    
                }
                // * * * * * * * * * * * * * * * * *
               String oS ="Full";
               try{
                   sql = "UPDATE PIS_ORDER_DETAIL "
                           + "SET QTY_DISPENSED = ? "
                           + ", STATUS = ? , ORDER_STATUS = ? "
                           + "WHERE ORDER_NO = ? AND DRUG_ITEM_CODE = ? ";
                   PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql);
                   ps2.setInt(1, qtyDispensed); // changed from old_qty_dispensed - hadi
                   ps2.setInt(2, 1);
//                   ps2.setBoolean(2, true);
                   ps2.setString(3, oS);
                   ps2.setString(4, orderNo);
                   ps2.setString(5, drugCode);
                   stat = ps2.execute();
                   
               }catch(Exception mm){
                   mm.printStackTrace();                   
               }
            } else { //pharmacist dispensed less druq quantity than set by the doctor - hadi
                // * * * * * * * * * * * * * * * * *
                try {
                    String sql2 = "SELECT * "
                            + "FROM PIS_MDC2 "
                            + "WHERE UD_MDC_CODE = ?";
                    PreparedStatement ps3 = Conn.MySQLConnect().prepareStatement(sql2);
                    ps3.setString(1, drugCode);
                    ResultSet rs2 = ps3.executeQuery();
                    while (rs2.next()) {
                        drugQty = rs2.getInt("D_STOCK_QTY");
                        drugQty -= (qtyDispensed);
                    }
                    try {
                        String sql3 = "UPDATE PIS_MDC2 "
                                + "SET D_STOCK_QTY = ? "
                                + "WHERE UD_MDC_CODE = ? ";
                        PreparedStatement ps4 = Conn.MySQLConnect().prepareStatement(sql3);
                        ps4.setInt(1, drugQty);
                        ps4.setString(2, drugCode);
                        stat = ps4.execute();
                        S.oln("success update PIS_MDC :" + drugQty);

                    } catch (Exception vv) {
                        vv.printStackTrace();

                    }
                } catch (Exception qq) {
                    qq.printStackTrace();

                }
                // * * * * * * * * * * * * * * * * *
                try{
                    String oS = statusDrug;
                    sql = "UPDATE PIS_ORDER_DETAIL "
                            + "SET QTY_DISPENSED = ?, ORDER_STATUS = ?, STATUS = ? "
                            + "WHERE ORDER_NO = ? AND DRUG_ITEM_CODE = ? ";
                    PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql);
                    ps2.setInt(1, qtyDispensed); // changed from old_qty_dispensed - hadi
                    ps2.setString(2, oS); // changed from 3 to 2 - hadi
                    if (statusDrug.equals(status_full) || statusDrug.equals(status_complete_partial)) {
                        ps2.setInt(3, 1);
                    } else {
                        ps2.setInt(3, 0);
                    }
                    ps2.setString(4, orderNo);
                    ps2.setString(5, drugCode);
                    stat = ps2.execute();
                  
                }catch(Exception qq){
                    qq.printStackTrace();                    
                }
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean isOrderDetail(String orderNo) throws RemoteException {
        boolean stat = true;
        try {
            String sql = "SELECT STATUS "
                    + "FROM PIS_ORDER_DETAIL "
                    + "WHERE ORDER_NO = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("STATUS") == 0) {
                    stat = false;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean updateOrderMaster(String orderNo, 
            int status) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "UPDATE PIS_ORDER_MASTER "
                    + "SET STATUS = ? "
                    + "WHERE ORDER_NO = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, orderNo);
            stat = ps.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean updateDispensedMaster(String orderNo, 
            int status) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "UPDATE PIS_DISPENSE_MASTER "
                    + "SET STATUS = ? "
                    + "WHERE ORDER_NO = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, orderNo);
            stat = ps.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stat;
    }

    public String[] getBiodata(String pmiNo) throws RemoteException {
        String[] appointmentbiodatainformation = new String[35];
        try {
            

            String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PMI_NO=?";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmiNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                for (int i = 0; i < 35; i++) {
                    System.out.print(rs.getString(i + 1));
                    appointmentbiodatainformation[i] = rs.getString(i + 1);
                }
            }

            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return appointmentbiodatainformation;
    }

    public ArrayList<String> getBioPDI(String pmiNo) throws RemoteException {
        ArrayList<String> pdidata = new ArrayList<String>();
        try {


            String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PMI_NO=?";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmiNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                for (int i = 0; i < 35; i++) {
                    pdidata.add(rs.getString(i + 1));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pdidata;
    }

    public String getPMI(String ic) throws RemoteException {
        PMI pmitemp = new PMI();
        return pmitemp.getPMI(ic);
    }

    public ResultSet getMDC(String drugn) throws RemoteException {
       ResultSet rs = null;
       
       try{
           String sql = "SELECT * FROM PIS_MDC2 WHERE D_TRADE_NAME LIKE ? ";
           PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
           ps.setString(1, drugn.toUpperCase());
           rs = ps.executeQuery();
           
       }catch(Exception x){
           x.printStackTrace();
       }
       
       return rs;
    }

    public ResultSet getATC(String atcn) throws RemoteException {
        ResultSet rs = null;
       
       try{
           String sql = "SELECT * FROM PIS_ATC WHERE UD_ATC_CODE LIKE ? ";
           PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
           ps.setString(1, atcn.toUpperCase());
           rs = ps.executeQuery();
           System.out.println("tetsttttt");
           
       }catch(Exception x){
           x.printStackTrace();
       }
       
       return rs;
    }
   //get list of dispensed drug -- Hariz 20141203
    public TableModel getDispensedDrug(String strSql, String prepStatement[]) throws RemoteException
    {
        ResultSet rs = null;
        
        TableModel tm = null;
        try
        {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(strSql);
            if(prepStatement.length == 0 || prepStatement == null)
            {
                rs = ps.executeQuery();
                tm = DbUtils.resultSetToTableModel(rs);
                
                //ps.close();
            }           
            
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return tm;
        
        
    }
    //get list of dispensed drug -- Hariz 20141203 END
    
    public ArrayList<ArrayList<String>> getListOfStaffs(String user_id) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM ADM_USER AU, ADM_USER_ACCESS AUA "
                    + "WHERE AU.USER_ID = AUA.USER_ID "
                    + "AND AU.USER_ID <> ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                d.add(rs.getString("USER_ID"));
                d.add(rs.getString("USER_NAME"));
                d.add(rs.getString("ROLE_CODE"));
                d.add(rs.getString("HEALTH_FACILITY_CODE"));
                d.add(rs.getString("DISCIPLINE_CODE"));
                d.add(rs.getString("SUBDISCIPLINE_CODE"));
                data.add(d);
            }
            System.out.println("Get List Of Staffs Success ...");
        } catch (Exception ex) {
            data = new ArrayList<ArrayList<String>>();
            ex.printStackTrace();
        }
        return data;
    }

    public ArrayList<ArrayList<String>> getStaffs(String user_id) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM ADM_USER AU, ADM_USER_ACCESS AUA "
                    + "WHERE AU.USER_ID = AUA.USER_ID ";
            if (!user_id.equals("")) {
                sql += "AND AU.USER_ID = ? ";
            }
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            if (!user_id.equals("")) {
                ps.setString(1, user_id);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                for (int i = 0; i < 22; i++) {
                    d.add(rs.getString(i + 1));
                }
                data.add(d);
            }
            System.out.println("Get Staffs Success ...");
        } catch (Exception ex) {
            data = new ArrayList<ArrayList<String>>();
            ex.printStackTrace();
        }
        return data;
    }

    public boolean addStaff(String data1[], String data2[]) throws RemoteException {
        boolean stat = false;
        int num_cols1 = data1.length;
        int num_cols2 = data2.length;
        try {
            String sql1 = "INSERT INTO ADM_USER VALUES(?";
            for (int i = 1; i < num_cols1; i++) {
                sql1 += ",?";
            }
            sql1 += ")";
            PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
            for (int i = 0; i < num_cols1; i++) {
                ps1.setString(i + 1, data1[i]);
            }
            ps1.execute();

            String sql2 = "INSERT INTO ADM_USER_ACCESS VALUES(?";
            for (int i = 1; i < num_cols2; i++) {
                sql2 += ",?";
            }
            sql2 += ")";
            PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql2);
            for (int i = 0; i < num_cols2; i++) {
                ps2.setString(i + 1, data2[i]);
            }
            ps2.execute();
            stat = true;
            System.out.println("Add Staff Success ...");
        } catch (Exception ex) {
            stat = false;
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean deleteStaff(String user_id) throws RemoteException {
        boolean stat = false;
        try {
            String sql1 = "DELETE FROM ADM_USER "
                    + "WHERE USER_ID = ? ";
            PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
            ps1.setString(1, user_id);
            ps1.execute();
            String sql2 = "DELETE FROM ADM_USER_ACCESS "
                    + "WHERE USER_ID = ? ";
            PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql2);
            ps2.setString(1, user_id);
            ps2.execute();
            stat = true;
            System.out.println("Delete Staff Success ...");
        } catch (Exception e) {
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    public boolean isStaffs(String user_id) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "SELECT * "
                    + "FROM ADM_USER AU, ADM_USER_ACCESS AUA "
                    + "WHERE AU.USER_ID = AUA.USER_ID "
                    + "AND AU.USER_ID = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stat = true;
            } else {
                stat = false;
            }
            System.out.println("Is Staff Success ...");
        } catch (Exception ex) {
            stat = false;
            ex.printStackTrace();
        }
        return stat;
    }

    public boolean updateStaff(String user_id, String cols1[], String data1[], String cols2[], String data2[]) throws RemoteException {
        boolean stat = false;
        int num_cols1 = data1.length;
        int num_cols2 = data2.length;
        try {
            String sql1 = "UPDATE ADM_USER SET ";
            for (int i = 0; i < cols1.length - 1; i++) {
                sql1 += cols1[i] + " = ?, ";
            }
            sql1 += cols1[cols1.length - 1] + " = ? " + "WHERE USER_ID = ? ";
            PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
            for (int i = 0; i < data1.length; i++) {
                ps1.setString(i+1, data1[i]);
            }
            ps1.setString(data1.length+1, user_id);
            ps1.execute();

            String sql2 = "UPDATE ADM_USER_ACCESS SET ";
            for (int i = 0; i < cols2.length - 1; i++) {
                sql2 += cols2[i] + " = ?, ";
            }
            sql2 += cols2[cols2.length - 1] + " = ? " + "WHERE USER_ID = ? ";
            PreparedStatement ps2 = Conn.MySQLConnect().prepareStatement(sql2);
            for (int i = 0; i < data2.length; i++) {
                ps2.setString(i+1, data2[i]);
            }
            ps2.setString(data2.length+1, user_id);
            ps2.execute();

            stat = true;
            System.out.println("Update Staff Success ...");
        } catch (Exception ex) {
            stat = false;
            ex.printStackTrace();
        }
        return stat;
    }

    public ArrayList<String> getStaffLogin(String user_id, String password) throws RemoteException {
        System.out.println("Login ...");
        System.out.println("USER ID: " + user_id);
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * "
                    + "FROM ADM_USER AU, ADM_USER_ACCESS AUA "
                    + "WHERE AU.USER_ID = AUA.USER_ID "
                    + "AND AU.USER_ID = ? "
                    + "AND AU.PASSWORD = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_id);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < 23; i++) {
                    data.add(rs.getString(i + 1));
                }
            }
            System.out.println("Login Success ...");
        } catch (Exception ex) {
            data = new ArrayList<String>();
            ex.printStackTrace();
            System.out.println("Login Failed!!");
        }
        return data;
    }

    public boolean addEhrCentral_vts(String pmino, String vts_data) throws RemoteException {
        boolean stat = false;
        try {
            Date date = new Date(new java.util.Date().getTime());
            //insert data into EHR_Central table
            String sql = "INSERT INTO EHR_CENTRAL "
                    + "(PMI_NO,C_TXNDATE,C_TXNDATA,STATUS) "
                    + "VALUES (?,?,?,?)";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmino);
            ps.setDate(2, date);
            ps.setString(3, vts_data);
            ps.setInt(4, 1);
            ps.execute();
            System.out.println("PMI No          : " + pmino);
            System.out.println("Insert VTS Data : " + vts_data);
            stat = true;
        } catch (Exception ex) {
            stat = false;
            ex.printStackTrace();
        }
        return stat;
    }

    public ArrayList<String> getPatient(int index,String sql,String[] calDate) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql); 
            ps.setString(1, calDate [index]);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                data.add(rs.getString(1));
                data.add(rs.getString("APPOINTMENT_TIME"));
                data.add(rs.getString(5));
            }
            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return data;
    }

    public boolean isLogin(String username, String password) throws RemoteException {
        System.out.println("Is Login?");
        boolean stat = false;
        try {
            String sql = "SELECT * "
                    + "FROM signup_tbl st, pms_patient_biodata ppb "
                    + "WHERE st.ic_no = ppb.new_ic_no "
                    + "AND st.username = ? "
                    + "AND st.password = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stat = true;
                System.out.println("Login Has Data..");
            } else {
                System.out.println("Login No Data!");
            }
            System.out.println("Login True");
        } catch (Exception ex) {
            stat = false;
            System.out.println("Login False");
            ex.printStackTrace();
        }
        return stat;
    }

    public ArrayList<String> getLoginData(String username, String password) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * "
                    + "FROM signup_tbl st, pms_patient_biodata ppb "
                    + "WHERE st.ic_no = ppb.new_ic_no "
                    + "AND st.username = ? "
                    + "AND st.password = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < 40; i++) {
                    data.add(rs.getString(i + 1));
                }
            }
            System.out.println("Get Login Data Success");
        } catch (Exception ex) {
            data.removeAll(data);
            data = new ArrayList<String>();
            System.out.println("Get Login Data Failed!!");
            ex.printStackTrace();
        }
        return data;
    }

    public boolean insertData(ArrayList<String> data_user) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "INSERT INTO "
                    + "signup_tbl(ic_no, username, email, password, name) "
                    + "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            for (int i = 0; i < data_user.size(); i++) {
                ps.setString(i+1, data_user.get(i));
            }
            ps.execute();
            stat = true;
            System.out.println("Insert Sign Up Success..");
        } catch (Exception ex) {
            stat = false;
            System.out.println("Insert Sign Up Failed!!");
            ex.printStackTrace();
        }
        return stat;
    }

    public ArrayList<ArrayList<String>> getReport(int type, String date1, String date2) throws RemoteException {
        String temp = "";
        ArrayList<ArrayList<String>> dat = new ArrayList<ArrayList<String>>();
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT C_TXNDATA "
                    + "FROM ehr_central "
                    + "WHERE C_TXNDATE >= ? "
                    + "AND C_TXNDATE <= ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, date1);
            ps.setString(2, date2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                temp = rs.getString("C_TXNDATA");
                MainRetrieval mr = new MainRetrieval();
                mr.startProcess(temp);
                String msg[][] = mr.getData(getSymptom(type));
                int row = mr.getRowNums();
                for (int i = 0; i < row; i++) {
                    data.add(msg[i][getReadCode(type)] + "|" + msg[i][getReadCode(type) + 1]);
                    System.out.println("msg: "+msg[i][getReadCode(type)] + "|" + msg[i][getReadCode(type) + 1]);
                }
            }
            String te = "";
            ArrayList<String> lst = new ArrayList<String>();
            ArrayList<String> lst2 = new ArrayList<String>();
            for (int i = 0; i < data.size(); i++) {
                te += data.get(i) + ", ";
                lst.add(data.get(i));
                lst2.add(data.get(i));
            }
            System.out.println("te: "+te);
            System.out.println("Duplicates List " + lst);
            Object[] st = lst.toArray();
            for (Object s : st) {
                if (lst.indexOf(s) != lst.lastIndexOf(s)) {
                    lst.remove(lst.lastIndexOf(s));
                }
            }
            System.out.println("Distinct List " + lst);
            ArrayList<String> llt = new ArrayList<String>();
            for (int i = 0; i < lst.size(); i++) {
                int count = 0;
                for (int j = 0; j < lst2.size(); j++) {
                    if (lst.get(i).equals(lst2.get(j))) {
                        count += 1;
                    }
                }
                llt.add("" + count);
            }
            System.out.println("Size          " + llt);
            dat.add(lst); //key
            dat.add(llt); //value
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dat;
    }
    public static String getSymptom(int type) {
        /**
         * msg[0] = mr[0].getData("CCN"); row[0] = mr[0].getRowNums(); msg[1] =
         * mr[1].getData("HPI"); row[1] = mr[1].getRowNums(); msg[2] =
         * mr[2].getData("PMH"); row[2] = mr[2].getRowNums(); msg[3] =
         * mr[3].getData("FMH"); row[3] = mr[3].getRowNums(); msg[4] =
         * mr[4].getData("SOH"); row[4] = mr[4].getRowNums(); msg[5] =
         * mr[5].getData("ALG"); row[5] = mr[5].getRowNums(); msg[6] =
         * mr[6].getData("IMU"); row[6] = mr[6].getRowNums(); msg[7] =
         * mr[7].getData("DAB"); row[7] = mr[7].getRowNums(); msg[8] =
         * mr[8].getData("VTS"); row[8] = mr[8].getRowNums(); msg[9] =
         * mr[9].getData("DGS"); row[9] = mr[9].getRowNums(); msg[10] =
         * mr[10].getData("DTO");
         */
        String str = "CCN";
        switch (type) {
            case 1:
                str = "CCN";
                break;
            case 2:
                str = "PMH";
                break;
            case 3:
                str = "FMH";
                break;
            case 4:
                str = "SOH";
                break;
            case 5:
                str = "ALG";
                break;
            case 6:
                str = "IMU";
                break;
            case 7:
                str = "DAB";
                break;
            case 8:
                str = "DGS";
                break;
            case 9:
                str = "DTO";
                break;
        }
        return str;
    }
    public static int getReadCode(int type) {
        /**
         * msg[0] = mr[0].getData("CCN"); msg[2] = mr[2].getData("PMH"); msg[3]
         * = mr[3].getData("FMH"); msg[4] = mr[4].getData("SOH"); msg[5] =
         * mr[5].getData("ALG"); msg[6] = mr[6].getData("IMU"); msg[7] =
         * mr[7].getData("DAB"); msg[9] = mr[9].getData("DGS"); msg[10] =
         * mr[10].getData("DTO");
         */
        int str = 1;
        switch (type) {
            case 1:
                str = 1;
                break;
            case 2:
                str = 1;
                break;
            case 3:
                str = 4;
                break;
            case 4:
                str = 1;
                break;
            case 5:
                str = 1;
                break;
            case 6:
                str = 1;
                break;
            case 7:
                str = 1;
                break;
            case 8:
                str = 5;
                break;
            case 9:
                str = 4;
                break;
        }
        return str;
    }

    public void addAUTOGENERATE_ONO(String oNo) throws RemoteException {
        try {
            String sql = "INSERT INTO AUTOGENERATE_ONO"
                    + "(ORDER_NO)"
                    + "VALUES (?)";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, oNo);

            //close & update
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            System.out.println("insert generateono" + e);

        }
    }

    public void addPIS_ORDER_MASTER(String oNo, String pmiNo, String hfc, Timestamp ec, Timestamp ed, Timestamp oDate, String id, String oF, String oTo, int spubNo, int toi, boolean oSM) throws RemoteException {
        //insert value into table PMS_Patient_Medication
        try {
            //order status for boolean
            //EDIT SQL 13 MAY 2013
            String sql = "INSERT INTO PIS_ORDER_MASTER "
                    + "(order_no, pmi_no,health_facility_code,episode_code,encounter_date,"
                    + "order_date, order_by,order_from, order_to, "
                    + "hfc_from, hfc_to, spub_no, keyin_by, "
                    + "total_order, status) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String sql="Insert into PMS_Patient_Medication (Medication_Form_Code,PMI_No,Dispense_Status,Staff_ID,Dispense_Date) Values (?,?,?,?,?)";

            //prepare sql query and execute it
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            //pls enable var after decl

            ps.setString(1, oNo);
            S.oln(oNo);
            ps.setString(2, pmiNo);
            S.oln(pmiNo);
            ps.setString(3, hfc);
            S.oln(hfc);
            // java.sql.Timestamp ec;
            ec = new java.sql.Timestamp(new java.util.Date().getTime());
            ps.setTimestamp(4, ec);
            S.oln("" + ec);
            //java.sql.Timestamp ed;
            ed = new java.sql.Timestamp(new java.util.Date().getTime());
            ps.setTimestamp(5, ed);
            S.oln("" + ed);

            //get time now
            oDate = new java.sql.Timestamp(new java.util.Date().getTime());
            ps.setTimestamp(6, oDate);
            S.oln("" + oDate);
            ps.setString(7, id);//Session.getUser_id()
            S.oln(id);
            //ps.setString(7,oBy);//id staff
            ps.setString(8, oF);
            S.oln(oF);
            ps.setString(9, oTo);
            S.oln(oTo);
            ps.setString(10, hfc);//hfcF
            S.oln(hfc);
            ps.setString(11, oF + oTo);
            S.oln(oF + oTo);
            //ps.setString(12,oNo);//D : spubNo
            ps.setInt(12, spubNo);//D : spubNo
            S.oln(spubNo);
            ps.setString(13, id);//D: kiBy
            S.oln(id);
            //ps.setString(14,toi);
            ps.setInt(14, toi);
            S.oln(toi);
            ps.setBoolean(15, oSM);//true false
            S.oln("" + oSM);

            ps.executeUpdate();
            ps.close();

            System.out.println("sucess insert PIS_OM ");

        } catch (Exception e) {
            System.out.println("Error insert PIS_OM " + e);
        }
    }

    public void addPIS_ORDER_DETAIL(String oNo, String dmdc, String dtraden, String dLfreq, String droute, String ddosage, String dstrength, String dLqty, String dLadvisory, int duration1, String orderStatus, double qtyPerTime1, int totalQty, String sOUM, int qtydispensed1, String dOUM, boolean oSD) throws RemoteException {
        //insert value into table PMS_Drug_Dispense
        try {

            String sql = "INSERT INTO PIS_ORDER_DETAIL "
                    + "(order_no,drug_item_code,drug_item_desc,"
                    + "drug_frequency,drug_route,drug_form,drug_strength,"
                    + "drug_dosage,order_oum,duration,order_status ,qty_ordered,"
                    + "qty_supplied,supplied_oum,qty_dispensed,dispense_oum,status) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            //prepare sql query and execute it txt_dosageOList
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, oNo);
            ps.setString(2, dmdc);
            ps.setString(3, dtraden);
            ps.setString(4, dLfreq);
            ps.setString(5, droute);
            ps.setString(6, ddosage);
            ps.setString(7, dstrength);
            ps.setString(8, dLqty);
            ps.setString(9, dLadvisory);
            ps.setInt(10, duration1);
            ps.setString(11, orderStatus);//default; new : full partial outstanding
            ps.setDouble(12, qtyPerTime1);
            ps.setInt(13, totalQty);
            ps.setString(14, sOUM);
            ps.setInt(15, qtydispensed1);
            ps.setString(16, dOUM);
            ps.setBoolean(17, oSD);//default false

            System.out.println("sucess insert PIS_OD");

            ps.executeUpdate();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error insert PIS_OD 2" + ex);
        }
    }

    public ResultSet getPrescriptionNote(String pmiNo) throws RemoteException {
        ResultSet results = null;
        try {
            String sql = "select * "
                    + "from pms_patient_biodata,pis_order_master "
                    + "where PIS_ORDER_MASTER.pmi_no = PMS_PATIENT_BIODATA.pmi_no "
                    + "AND pms_patient_biodata.pmi_no = ? ";
    //            String sql="SELECT * "
    //                    + "FROM PMS_PATIENT_BIODATA "
    //                    + "WHERE PMI_NO = ?";
    //            String sql="SELECT *" +
    //                        "FROM PIS_ORDER_MASTER,PMS_PATIENT_BIODATA" +
    //                        "WHERE PIS_ORDER_MASTER.pmi_no = PMS_PATIENT_BIODATA.pmi_no "
    //                        + "AND PMI_NO = ?";
    //            String sql="SELECT *" +
    //                        "WHERE PIS_ORDER_MASTER.pmi_no = PMS_PATIENT_BIODATA.pmi_no ";
    //                        "WHERE PIS_ORDER_MASTER.pmi_no = PMS_PATIENT_BIODATA.pmi_no ";

            //prepare sql query and execute it
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmiNo);

            results = ps.executeQuery();

            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    public ResultSet getPrescriptionNote2(String oNo) throws RemoteException {
        ResultSet results1 = null;
        try {
            String sql1 = "select * "
                    + "from pis_order_detail,pis_order_master "
                    + "where pis_order_master.order_no = pis_order_detail.order_no "
                    + "AND pis_order_detail.order_no = ? ";
//                 
            //prepare sql query and execute it
            PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
            ps1.setString(1, oNo);
            results1 = ps1.executeQuery();

            ps1.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return results1;
    }

    public ResultSet getAUTOGENERATE_ONO() throws RemoteException {
        ResultSet rs = null;
        try {
            //String sql = "SELECT ORDER_NO FROM PIS_ORDER_MASTER";
            String sql = "SELECT ORDER_NO FROM AUTOGENERATE_ONO";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);

            rs = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public boolean isConsult(String pmino, String time, String doctor) throws RemoteException {
        boolean stat = false;
        try {
            String sql1 = "SELECT * "
                    + "FROM PMS_EPISODE "
                    + "WHERE PMI_NO = ? "
                    + "AND (STATUS NOT LIKE 'Consult' OR (STATUS LIKE 'Consult' AND DOCTOR = ?)) "
                    + "AND DATE(EPISODE_DATE) = DATE(?) "
                    + "AND (DOCTOR = ? OR DOCTOR = '-') ";
            if (!time.equals("")) {
                sql1 += "AND EPISODE_TIME = ? ";
            }
            sql1 += "ORDER BY EPISODE_TIME ASC";
            PreparedStatement ps1 = Conn.MySQLConnect().prepareStatement(sql1);
            ps1.setString(1, pmino);
            ps1.setString(2, doctor);
            java.util.Date date = new java.util.Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ps1.setString(3, sdf.format(date));
            ps1.setString(4, doctor);
            
            if (!time.equals("")) {
                ps1.setString(5, time);
            }
            
            ResultSet rs1 = ps1.executeQuery();
            
            stat = rs1.next();
            
        } catch (Exception e) {
            
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    public String[] simplifyCheckBiodata(String pmiNo, String time, String doctor) throws RemoteException {
        String[] appointmentbiodatainformation = new String[35];
        try {
            boolean isCons = isConsult(pmiNo, time, doctor);
            if (isCons) {
                appointmentbiodatainformation = getBiodata(pmiNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointmentbiodatainformation;
    }
    
    private ArrayList<String> getMainRetrieval(ArrayList<String> data_in, 
            String temp, String type, int pos) {
        try {
            MainRetrieval mr = new MainRetrieval();
            mr.startProcess(temp);
            String msg[][] = mr.getData(type);
            int row = mr.getRowNums();
            for (int i = 0; i < row; i++) {
                data_in.add(msg[i][pos]);
                System.out.println("icd10: " + msg[i][pos]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data_in;
    }

    public ArrayList<ArrayList<String>> getReportICD10(String date) throws RemoteException {
        ArrayList<ArrayList<String>> dat = new ArrayList<ArrayList<String>>();
        try {
            ArrayList<String> data = new ArrayList<String>();
            String month = "";
            String day = "";
            String date1[] = date.split(" ")[0].split("-");
            String month1 = date1[1];
            String day1 = date1[2];
            if (!month1.equals("00")) {
                month = "AND MONTH(C_TXNDATE) = MONTH(?) ";
                if (!day1.equals("00")) {
                    day = "AND DAY(C_TXNDATE) = DAY(?) ";
                }
            }
            String sql = "SELECT C_TXNDATA "
                    + "FROM EHR_CENTRAL "
                    + "WHERE YEAR(C_TXNDATE) = YEAR(?) "
                    + month + day;
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, date);
            if (!month1.equals("00")) {
                ps.setString(2, date);
                if (!day1.equals("00")) {
                    ps.setString(3, date);
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String temp = rs.getString("C_TXNDATA");
                //CCN, PMH, FH, ALG, IMU, DAB, DGS
                data = getMainRetrieval(data, temp, "CCN", 20);
//                data = getMainRetrieval(data, temp, "PMH", 16);
//                data = getMainRetrieval(data, temp, "FMH", 2);
//                data = getMainRetrieval(data, temp, "ALG", 14);
//                data = getMainRetrieval(data, temp, "IMU", 14);
//                data = getMainRetrieval(data, temp, "DAB", 5);
                data = getMainRetrieval(data, temp, "DGS", 27);
            }
            String te = "";
            ArrayList<String> lst = new ArrayList<String>();
            ArrayList<String> lst2 = new ArrayList<String>();
            for (int i = 0; i < data.size(); i++) {
                te += data.get(i) + ", ";
                lst.add(data.get(i));
                lst2.add(data.get(i));
            }
            Object[] st = lst.toArray();
            for (Object s : st) {
                if (lst.indexOf(s) != lst.lastIndexOf(s)) {
                    lst.remove(lst.lastIndexOf(s));
                }
            }
            lst.removeAll(Collections.singleton(null));
            lst.removeAll(Collections.singleton(""));
            lst.removeAll(Collections.singleton("-"));
            Collections.sort(lst);
            System.out.println("Distinct List " + lst);
            ArrayList<String> llt = new ArrayList<String>();
            for (int i = 0; i < lst.size(); i++) {
                int count = 0;
                for (int j = 0; j < lst2.size(); j++) {
                    if (lst.get(i).equals(lst2.get(j))) {
                        count += 1;
                    }
                }
                llt.add("" + count);
            }
            System.out.println("Size          " + llt);
            dat.add(lst); //key
            dat.add(llt); //value
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dat;
    }
    
    /**
     * Distinct ArrayList<String>
     */
    public static ArrayList<String> getDistinctList(ArrayList<String> data) {
        String te = "";
        ArrayList<String> lst = new ArrayList<String>();
        ArrayList<String> lst2 = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            te += data.get(i) + ", ";
            lst.add(data.get(i));
            lst2.add(data.get(i));
        }
        Object[] st = lst.toArray();
        for (Object s : st) {
            if (lst.indexOf(s) != lst.lastIndexOf(s)) {
                lst.remove(lst.lastIndexOf(s));
            }
        }
        lst.removeAll(Collections.singleton(null));
        lst.removeAll(Collections.singleton(""));
        lst.removeAll(Collections.singleton("-"));
        Collections.sort(lst);
        System.out.println("Distinct List " + lst);
        return lst;
    }

    public String getICD10SortReport(String date) throws RemoteException {
        
        ArrayList<ArrayList<String>> arr_out = new ArrayList<ArrayList<String>>();
        String str_out = "";
        
        ArrayList<ArrayList<String>> data = getReportICD10(date);
        
        System.out.println("size:" + data.size());

        int line_paragraph = data.get(0).size();

        System.out.println("line size:" + line_paragraph);

        String icd10_code[] = new String[line_paragraph];
        String icd10_num[] = new String[line_paragraph];

        for (int i = 0; i < line_paragraph; i++) {
            icd10_code[i] = data.get(0).get(i);
            icd10_num[i] = data.get(1).get(i);
            System.out.println(icd10_code[i] + " : " + icd10_num[i]);
        }

        ArrayList<String> chapter = new ArrayList<String>();
        ArrayList<String> block = new ArrayList<String>();
        for (int i = 0; i < line_paragraph; i++) {
            chapter.add(icd10_code[i].substring(0, 2));
            block.add(icd10_code[i].substring(2, 5) + "|" + icd10_code[i].substring(0, 2));
        }

        System.out.println("chapter: " + chapter);
        System.out.println("block: " + block);

        ArrayList<String> new_chapter = getDistinctList(chapter);
        ArrayList<String> new_block = getDistinctList(block);

        System.out.println("new_chapter: " + new_chapter);
        System.out.println("new_block: " + new_block);

        int num_chapter[] = new int[new_chapter.size()];
        for (int i = 0; i < new_chapter.size(); i++) {
            num_chapter[i] = 0;
        }

        int num_block[] = new int[new_block.size()];
        for (int i = 0; i < new_block.size(); i++) {
            num_block[i] = 0;
        }

        for (int i = 0; i < line_paragraph; i++) {
            String code = data.get(0).get(i);
            for (int j = 0; j < new_chapter.size(); j++) {
                if (code.substring(0, 2).equals(new_chapter.get(j))) {
                    num_chapter[j] += 1;
                }
            }
            for (int j = 0; j < new_block.size(); j++) {
                if (code.substring(2, 5).equals(new_block.get(j))) {
                    num_block[j] += 1;
                }
            }
        }
        
        String sql = "";
        PreparedStatement ps;
        ResultSet rs;
        String chap_desc = "";
        String block_desc = "";
        String code_desc = "";
        String year = getYear(date);
        String month = getMonth(date);
        String day = getDay(date);
        
        long time = System.currentTimeMillis();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
        String timeid = String.valueOf(timestamp);

        for (int i = 0; i < new_chapter.size(); i++) {
            System.out.println("CHAPTER " + new_chapter.get(i));
            for (int j = 0; j < new_block.size(); j++) {
                String bloc = new_block.get(j).split("\\|")[0];
                String chap = new_block.get(j).split("\\|")[1];
                if (chap.equals(new_chapter.get(i))) {
                    System.out.println("BLOCK " + bloc);
                    for (int k = 0; k < line_paragraph; k++) {
                        String ch = icd10_code[k].substring(0, 2);
                        String bl = icd10_code[k].substring(2, 5);
                        if (ch.equals(chap) && bl.equals(bloc)) {
                            System.out.println(icd10_code[k] + ":" + icd10_num[k]);
                            try {
                                String code = icd10_code[k];
                                String code_num = icd10_num[k];
                                
                                //chapters
                                sql = "SELECT name FROM icd10_chapters WHERE Id = ? ";
                                ps = Conn.MySQLConnect().prepareStatement(sql);
                                ps.setString(1, chap);
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    chap_desc = rs.getString("name");
                                }
                                ps.close();
                                rs.close();
                                
                                //blocks
                                sql = "SELECT name FROM icd10_blocks WHERE Id = ? ";
                                ps = Conn.MySQLConnect().prepareStatement(sql);
                                ps.setString(1, bloc);
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    block_desc = rs.getString("name");
                                }
                                ps.close();
                                rs.close();
                                
                                //codes
                                sql = "SELECT icd10_desc FROM icd10_codes WHERE icd10_code = ? ";
                                ps = Conn.MySQLConnect().prepareStatement(sql);
                                ps.setString(1, icd10_code[k]);
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    code_desc = rs.getString("icd10_desc");
                                }
                                ps.close();
                                rs.close();
                                
                                //insert report
                                sql = "INSERT INTO icd10_report(report_id, report_year, "
                                        + "report_month, report_day, report_chapter, "
                                        + "report_chapterdesc, report_block, "
                                        + "report_blockdesc, report_code, "
                                        + "report_codedesc, report_num) "
                                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                ps = Conn.MySQLConnect().prepareStatement(sql);
                                ps.setString(1, timeid);
                                ps.setString(2, year);
                                ps.setString(3, month);
                                ps.setString(4, day);
                                ps.setString(5, chap);
                                ps.setString(6, chap_desc);
                                ps.setString(7, bloc);
                                ps.setString(8, block_desc);
                                ps.setString(9, code);
                                ps.setString(10, code_desc);
                                ps.setString(11, code_num);
                                ps.execute();
                                ps.close();
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        
        try {
            //get report
            sql = "SELECT * FROM icd10_report WHERE report_id = ? ";
            ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, timeid);
            rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                for (int l = 0; l < 11; l++) {
                    d.add(rs.getString(l + 1));
                }
                System.out.println("d:" + d);
                arr_out.add(d);
            }
            rs.close();
            ps.close();
            
            //delete report
            sql = "DELETE FROM icd10_report WHERE report_id = ? ";
            ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, timeid);
            ps.execute();
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ArrayList<String> chapter1 = new ArrayList<String>();
        ArrayList<String> block1 = new ArrayList<String>();

        for (int i = 0; i < arr_out.size(); i++) {
            for (int j = 0; j < arr_out.get(i).size(); j++) {
                chapter1.add(arr_out.get(i).get(4));
                block1.add(arr_out.get(i).get(6) + "|" + arr_out.get(i).get(4));
            }
        }

        ArrayList<String> newchapter = getDistinctList(chapter1);
        ArrayList<String> newblock = getDistinctList(block1);

        // list all chapter
        for (int i = 0; i < newchapter.size(); i++) {
            String chap = newchapter.get(i);
            str_out += chap + "|" + getChapter(chap, arr_out) + "|";
            
            // list all block
            for (int j = 0; j < newblock.size(); j++) {
                String ch = newblock.get(j).split("\\|")[1];
                String bl = newblock.get(j).split("\\|")[0];
                
                // if chapter equal to block's chapter
                if (ch.equals(chap)) {
                
                    str_out += bl + "^" + getBlock(bl, arr_out) + "^";
                    for (int k = 0; k < arr_out.size(); k++) {
                        if (arr_out.get(k).get(4).equals(ch)
                                && arr_out.get(k).get(6).equals(bl)) {
                            
                            str_out += arr_out.get(k).get(8)+";"
                                    +arr_out.get(k).get(9)+";"
                                    +arr_out.get(k).get(10)+";";
                        }
                    }
                    str_out += "^";
                }
            }
            
            str_out += "|";
        }
        
        System.out.println("\n\n\n"+str_out);
        
        return str_out;
    }
    
    private static String getChapter(String chap, ArrayList<ArrayList<String>> data) {
        String c = "-";
        try {
            for (int i = 0; i < data.size(); i++) {
                if (chap.equals(data.get(i).get(4))) {
                    c = data.get(i).get(5);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
    
    private static String getBlock(String bloc, ArrayList<ArrayList<String>> data) {
        String b = "-";
        try {
            for (int i = 0; i < data.size(); i++) {
                if (bloc.equals(data.get(i).get(6))) {
                    b = data.get(i).get(7);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
    
    private String getYear(String date) {
        String y = "0000";
        try {
            String a[] = date.split(" ");
            String b[] = a[0].split("-");
            y = b[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return y;
    }
    
    private String getMonth(String date) {
        String m = "00";
        try {
            String a[] = date.split(" ");
            String b[] = a[0].split("-");
            m = b[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    
    private String getDay(String date) {
        String d = "00";
        try {
            String a[] = date.split(" ");
            String b[] = a[0].split("-");
            d = b[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    @Override
    public ArrayList<ArrayList<String>> getDoctors(String user_id) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM adm_user au "
                    + "INNER JOIN adm_user_access aua "
                    + "ON au.user_id = aua.user_id "
                    + "WHERE aua.role_code = 'DOCTOR' "
                    + "AND au.user_id <> ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_id);
            ResultSet rs1 = ps.executeQuery();
            while (rs1.next()) {
                ArrayList<String> d = new ArrayList<String>();
                for (int i = 0; i < 22; i++) {
                    d.add(rs1.getString(i+1));
                }
                data.add(d);
            }
            System.out.println("Get Doctor's List ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean updatePmsPatientBiodata(String pmino, ArrayList<String> column, ArrayList<String> data) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "UPDATE pms_patient_biodata SET ";
            for (int i = 0; i < column.size(); i++) {
                sql += column.get(i) + " = ?";
                if (i == column.size() - 1) {
                    sql += " ";
                } else {
                    sql += ", ";
                }
            }
            sql += "WHERE pmi_no = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            for (int i = 0; i < column.size(); i++) {
                ps.setString(i + 1, data.get(i));
            }
            ps.setString(column.size()+1, pmino);
            ps.execute();
            stat = true;
        } catch (Exception e) {
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    @Override
    public ArrayList<String> getDoctor(String user_name) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String sql = "SELECT * "
                    + "FROM adm_user au "
                    + "INNER JOIN adm_user_access aua "
                    + "ON au.user_id = aua.user_id "
                    + "WHERE au.user_name = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < 22; i++) {
                    data.add(rs.getString(i+1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean addReferral(ArrayList<String> data) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "INSERT INTO cis_referal_tab VALUES(";
            if (data.size() > 0) {
                sql += "?";
            }
            for (int i = 1; i < data.size(); i++) {
                sql += ", ?";
            }
            sql += ") ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            for (int i = 0; i < data.size(); i++) {
                ps.setString(i+1, data.get(i));
            }
            ps.execute();
            stat = true;
        } catch (Exception e) {
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    @Override
    public ArrayList<ArrayList<String>> getReferral(String user_id) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * "
                    + "FROM cis_referal_tab crt "
                    + "INNER JOIN adm_user au "
                    + "ON crt.doctor_from = au.user_id "
                    + "WHERE crt.doctor_to = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, user_id);
            ResultSet rs1 = ps.executeQuery();
            while (rs1.next()) {
                ArrayList<String> d = new ArrayList<String>();
                for (int i = 0; i < 29; i++) {
                    d.add(rs1.getString(i+1));
                }
                data.add(d);
            }
            System.out.println("Get Doctor's Referral ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean delReferral(String pmino, String episodetime) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "DELETE FROM cis_referal_tab WHERE pmi_no = ? AND episode_time = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmino);
            ps.setString(2, episodetime);
            ps.execute();
            stat = true;
        } catch (Exception e) {
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    @Override
    public boolean changePassword(String userid, String pwd) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "UPDATE adm_user SET password = ? WHERE user_id = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pwd);
            ps.setString(2, userid);
            ps.execute();
            stat = true;
        } catch (Exception e) {
            e.printStackTrace();
            stat = false;
        }
        return stat;
    }
    
    @Override
    public boolean changeRoomNo(String userid, String roomNo) throws RemoteException {
        boolean stat = false;
        try {
            String sql = "UPDATE adm_user SET room_no = ? WHERE user_id = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, roomNo);
            ps.setString(2, userid);
            ps.execute();
            stat = true;
        } catch (Exception e) {
            e.printStackTrace();
            stat = false;
        }
        return stat;
    }

    @Override
    public boolean isUpdatePatientBiodata(String[] Biodata) throws RemoteException {
        boolean stat = false;
        try {
            //Connection conn = DbConnection.doConnection();
            String sql = "UPDATE PMS_PATIENT_BIODATA SET PMI_NO = '" + Biodata[0] + "',PMI_NO_TEMP = '" + Biodata[1] + "',PATIENT_NAME = '" + Biodata[2] + "',TITLE_CODE = '" + Biodata[3] + "',NEW_IC_NO = '" + Biodata[4] + "',OLD_IC_NO = '" + Biodata[5] + "',ID_TYPE = '" + Biodata[6] + "',ID_NO = '" + Biodata[7] + "',ELIGIBILITY_CATEGORY_CODE = '" + Biodata[8] + "',ELIGIBILITY_TYPE_CODE = '" + Biodata[9] + "',BIRTH_DATE = '" + Biodata[10] + "',SEX_CODE = '" + Biodata[11] + "',MARITAL_STATUS_CODE = '" + Biodata[12] + "',RACE_CODE = '" + Biodata[13] + "',NATIONALITY = '" + Biodata[14] + "',RELIGION_CODE = '" + Biodata[15] + "',BLOOD_TYPE = '" + Biodata[16] + "',BLOOD_RHESUS_CODE = '" + Biodata[17] + "',ALLERGY_IND = '" + Biodata[18] + "',CHRONIC_DISEASE_IND = '" + Biodata[19] + "',ORGAN_DONOR_IND = '" + Biodata[20] + "',HOME_ADDRESS = '" + Biodata[21] + "',HOME_DISTRICT_CODE = '" + Biodata[22] + "',HOME_TOWN_CODE = '" + Biodata[23] + "',HOME_POSTCODE = '" + Biodata[24] + "',HOME_STATE_CODE = '" + Biodata[25] + "',HOME_COUNTRY_CODE = '" + Biodata[26] + "',HOME_PHONE = '" + Biodata[27] + "',POSTAL_ADDRESS = '" + Biodata[28] + "',POSTAL_DISTRICT_CODE = '" + Biodata[29] + "',POSTAL_TOWN_CODE = '" + Biodata[30] + "',POSTAL_POSTCODE = '" + Biodata[31] + "',POSTAL_STATE_CODE = '" + Biodata[32] + "',POSTAL_COUNTRY_CODE = '" + Biodata[33] + "',MOBILE_PHONE = '" + Biodata[34] + "' WHERE PMI_NO = '" + Biodata[0] + "'";
            S.oln("isUpdate:" + sql);
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.executeUpdate();
            stat = true;
        } catch (Exception e) {
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }

    @Override
    public String getPassword(String icno, String userid) throws RemoteException {
        String pwd = "-";
        try {
            String sql = "SELECT * "
                    + "FROM adm_user "
                    + "WHERE new_icno = ? "
                    + "AND user_id = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, icno);
            ps.setString(2, userid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pwd = rs.getString("password");
            }
        } catch (Exception e) {
            pwd = "-";
            e.printStackTrace();
        }
        return pwd;
    }

    @Override
    public ArrayList<ArrayList<String>> getQuery(String query, int col, String dat[]) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(query);
            for (int i = 0; i < dat.length; i++) {
                ps.setString(i+1, dat[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                for (int i = 0; i < col; i++) {
                    d.add(rs.getString(i+1));
                }
                data.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean setQuery(String query, String data[]) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(query);
            for (int i = 0; i < data.length; i++) {
                ps.setString(i+1, data[i]);
            }
            ps.execute();
            status = true;
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public String insertPOS(String PMI, String dataPOS) throws RemoteException {
        String str = "";
        try {
            
            MainRetrieval mr = new MainRetrieval();
            mr.startProcess(dataPOS);
            String pos[][] = mr.getData("POS");
            int rows = mr.getRowNums();
            
            String order_no = insertPOSMaster(PMI, pos, rows);
            
            if (!order_no.equals("0000000")) {
                str = insertPOSDetail(order_no, pos, rows);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    
    public String insertPOSDetail(String order_no, String pos[][], int row_pos) {
        String str = "";
        int count = 0;
        
        for (int i = 0; i < row_pos; i++) {
            try {

                String sql = "INSERT INTO CIS_POS_DETAIL(ORDER_NO, PROCEDURE_CD, PROCEDURE_NAME, COMMENTS, COMMENTS_DOCTOR, CIS_POS_STATUS) VALUES(?, ?, ?, ?, ?, ?) ";
                PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                ps.setString(1, order_no);
                ps.setString(2, pos[i][4]);
                ps.setString(3, pos[i][5]);
                ps.setString(4, "");
                ps.setString(5, "");
                ps.setString(6, "1");
                ps.execute();
                count += 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (count == 0) {
            str = count+"|No Order Delivered!";
        } else if (count > 0 && count < row_pos) {
            str = count+"|Some Order Missing!";
        } else {
            str = count+"|Success.";
        }
        return str;
    }
    
    public String insertPOSMaster(String PMI, String pos[][], int row_pos) {
        Connection conn = null;
        String order_no = "0000000";
        
        Calendar c = Calendar.getInstance();
        int tahun = c.get(c.YEAR);
        String year = String.valueOf(tahun).split("")[3]+""
                +String.valueOf(tahun).split("")[4];
        
        try {
            conn = Conn.MySQLConnect();
            String ayat = "";
            
            try {
                ayat = "SELECT MAX(ORDER_NO) AS MAX_ORDER_NO "
                        + "FROM CIS_POS_MASTER ";
//            String ayat = "SELECT MAX(ORDER_NO) AS MAX_ORDER_NO "
//                    + "FROM AUTOGENERATE_ONO ";
                PreparedStatement pss = conn.prepareStatement(ayat);
                ResultSet rs = pss.executeQuery();
                if (rs.next()) {
                    String max_order_no = !rs.getString("MAX_ORDER_NO").equals("")
                            && rs.getString("MAX_ORDER_NO").length() > 0
                            ? rs.getString("MAX_ORDER_NO")
                            : "POS" + year + "0000000";
                    order_no = max_order_no.substring(5,
                            max_order_no.length());
                    if (!max_order_no.substring(3, 5).equals(year)) {
                        order_no = "0000000";
                    }
                }
            } catch (Exception e) {
            }
            
            try {
                if (order_no.equals("")) {
                    order_no = "0000000";
                }
            } catch(Exception ee) {
                order_no = "0000000";
            }
            
            int o_no = Integer.parseInt(order_no)+1 < 10000000 ? 
                    Integer.parseInt(order_no)+1 : 1;
            order_no = firm_number(o_no);
            order_no = "POS" + year + order_no;
            
//            Date date = new Date(new java.util.Date().getTime());
            java.util.Date date = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //insert data into EHR_Central table
            ayat = "INSERT INTO CIS_POS_MASTER ("
                    + "ORDER_NO,"
                    + "PMI_NO,"
                    + "ORDER_DATE,"
                    + "LOCATION_CODE,"
                    + "ARRIVAL_DATE,"
                    + "DOCTOR_ID) VALUES (?";
            int soal = 6 - 1;
            while (soal-- != 0) {
                ayat += ",?";
            }
            ayat += ");";
            System.out.println("ayat: " + ayat);
            PreparedStatement ps = conn.prepareStatement(ayat);
            ps.setString(1, order_no);
            ps.setString(2, PMI);
            ps.setString(3, sdf.format(date));
            ps.setString(4, row_pos > 0 ? pos[0][16] : "");
            ps.setString(5, row_pos > 0 ? pos[0][0] : "0000-00-00 00:00:00");
            ps.setString(6, "");
            ps.execute();
            System.out.println("..........Success Insert POS Order Master!.........");

        } catch (Exception ex) {
            order_no = "0000000";
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return order_no;
    }

    @Override
    public ArrayList<ArrayList<String>> getProcedures() throws RemoteException {
        ArrayList<ArrayList<String>> procedures = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * FROM cis_pos_master WHERE DATE(order_date) = DATE(NOW()) ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> data = new ArrayList<String>();
                try {
                    for (int i = 1; ; i++) {
                        data.add(rs.getString(i));
                    }
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
                procedures.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return procedures;
    }
    
    @Override
    public ArrayList<ArrayList<String>> getProceduresBasedOnPmiNo(String pmiNo) throws RemoteException {
        ArrayList<ArrayList<String>> procedures = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * FROM cis_pos_master WHERE DATE(order_date) = DATE(NOW()) AND pmi_no = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmiNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> data = new ArrayList<String>();
                try {
                    for (int i = 1; ; i++) {
                        data.add(rs.getString(i));
                    }
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
                procedures.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return procedures;
    }

    @Override
    public ArrayList<ArrayList<String>> getProcedureDetail(String orderNo) throws RemoteException {
        ArrayList<ArrayList<String>> procedures = new ArrayList<ArrayList<String>>();
        try {
            String sql = "SELECT * FROM cis_pos_detail cpd, cis_pos_master cpm "
                    + "WHERE cpd.order_no = cpm.order_no "
                    + "AND cpd.order_no = ? ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ArrayList<String> data = new ArrayList<String>();
                try {
                    for (int i = 1; ; i++) {
                        data.add(rs.getString(i));
                    }
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
                procedures.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return procedures;
    }

    @Override
    public ResultSet getDrugCIS(String search) throws RemoteException {
        try {
            String sql = "SELECT * "
                    + "FROM PIS_MDC2 "
                    + "WHERE UPPER(D_TRADE_NAME) LIKE UPPER(?) "
                    + "OR UPPER(D_GNR_NAME) LIKE UPPER(?) ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateProcedures(ArrayList<ArrayList<String>> prod) throws RemoteException {
        boolean status = false;
        try {
            for (int i = 0; i < prod.size(); i++) {
                String sql = "UPDATE cis_pos_detail "
                        + "SET comments = ?, comments_doctor = ?, cis_pos_status = ? "
                        + "WHERE order_no = ? "
                        + "AND procedure_cd = ? ";
                PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
                ps.setString(1, prod.get(i).get(0));
                ps.setString(2, prod.get(i).get(1));
                ps.setString(3, prod.get(i).get(2));
                ps.setString(4, prod.get(i).get(3));
                ps.setString(5, prod.get(i).get(4));
                ps.execute();
            }
            status = true;
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    @Override
    public boolean setQuerySQL(String query) throws RemoteException {
        boolean stat = false;
        try {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(query);
            ps.execute();
            stat = true;
        } catch (Exception e) {
            e.printStackTrace();
            stat = false;
        }
        return stat;
    }

    @Override
    public ArrayList<ArrayList<String>> getQuerySQL(String query) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                try {
                    for (int i = 0; ; i++) {
                        d.add(rs.getString(i+1));
                    }
                } catch (Exception e) {
                }
                data.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean isAlreadyRegistered(String pmino) throws RemoteException {
        boolean status = false;
        try {
            java.util.Date date = new java.util.Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "SELECT * "
                    + "FROM PMS_EPISODE "
                    + "WHERE PMI_NO = ? "
                    + "AND (STATUS LIKE '%Consult%' "
                    + "OR STATUS LIKE '%Waiting%' "
                    + "OR STATUS LIKE '%Hold%' "
                    + "OR STATUS LIKE '%Second Opinion%') "
                    + "AND DATE(EPISODE_DATE) = DATE(?) ";
            PreparedStatement ps = Conn.MySQLConnect().prepareStatement(sql);
            ps.setString(1, pmino);
            ps.setString(2, sdf.format(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
