package oms.rmi.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import package or classes
//import db.connection;
import oms.rmi.db.Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author Chung Wei Ming
 */
public class Patient {

    public Patient() {
    }

    /**
     * MSH|^~|CIS^001|<cr>
    PDI|PMS10003^LEE WEI CHUAN^891031075331^Chinese^Male^31/10/1989^AB^Married^|<cr>
    CCN|Knows the complaints procedure (finding)^Mild^Select One^2Minutes^Select One^ serious^Active^2012-08-03 
    13:12:47.17^|<cr>
    DGS|Able to perform recreational use of conversation (finding)^Mild^08/08/2012^2012-08-03 13:12:47.17^|<cr>
    IMU|Consent status for immunizations (finding)^as^02/08/2012^2012-08-03 13:12:47.17^|<cr>
    ALG|Allergic reaction to drug^10/08/2012^aaaa^2012-08-03 13:12:47.17^|<cr>
    SH|Cigar smoker^01/08/2012^heavy^2012-08-03 13:12:47.17^|<cr>
     */
    //save EHR
//    public void addEHR(String data, String PMI_No, String facility_code) throws ClassNotFoundException, SQLException {
//        Connection conn = Conn.MySQLConnect();
//        System.out.println("......data...: " + data);
//
//        //insert data into EHR_Central table
//        //PreparedStatement ps = conn.prepareStatement("INSERT INTO EHR_CENTRAL (PMI_NO,C_TXNDATE,C_TXNDATA)VALUES (?,?,?)");
//        PreparedStatement ps = conn.prepareStatement("INSERT INTO EHR_CENTRAL_2 (PMI_NO,FACILITY_CODE, EPISODE_DATE, ENCOUNTER_DATE,DATA)VALUES (?,?,?,?,?)");
//        //hfc_code - Kajang
//        //episode - date
//        //encounter - date
//        java.util.Date today = new java.util.Date();
//        java.sql.Date sqlToday = new java.sql.Date(today.getTime());
//
//        ps.setString(1, PMI_No);
//        ps.setString(2, facility_code);
//        ps.setDate(3, sqlToday); //20102012 11:50:35
//        ps.setDate(4, sqlToday);
//        ps.setString(5, data);
//        ps.execute();
//
//    }

    //get patient biodata from database when actor click table row from appointment list
    public String[] getEHR(String pmi_no) throws ClassNotFoundException, SQLException {
        String[] ehr = new String[5];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM EHR_CENTRAL WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pmi_no);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 5; i++) {
                System.out.print(rs.getString(i + 1));
                ehr[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return ehr;
    }
    
    //save patient family information into database
    public void addPatientFamilyInfo(String[] familyinfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO PMS_FAMILY (PMI_NO,FAMILY_SEQ_NO,FAMILY_RELATIONSHIP_CODE,PMI_NO_FAMILY,FAMILY_MEMBER_NAME,OCCUPATION_CODE) VALUES ('" + familyinfo[0] + "','" + familyinfo[1] + "','" + familyinfo[2] + "','" + familyinfo[3] + "','" + familyinfo[4] + "','" + familyinfo[5] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        String sql2 = "INSERT INTO AUTOGENERATE_FSNO (FAMILY_SEQ_NO) VALUES ('" + familyinfo[1] + "')";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps.executeUpdate();
        ps2.executeUpdate();
        conn.close();
    }

    //save patient insurance information into database
    public void addPatientInsuranceInfo(String[] insuranceinfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO PMS_MEDICAL_INSURANCE (PMI_NO,INSURANCE_COMPANY_CODE,POLICY_NO,MATURITY_DATE,HEALTH_FACILITY,POLICY_STATUS) VALUES ('" + insuranceinfo[0] + "','" + insuranceinfo[1] + "','" + insuranceinfo[2] + "','" + insuranceinfo[3] + "','" + insuranceinfo[4] + "','" + insuranceinfo[5] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //save patient next of kin information into database
    public void addPatientNextOfKinInfo(String[] nokinfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO PMS_NEXTOFKIN (PMI_NO,NEXTOFKIN_SEQ_NO,NEXTOFKIN_RELATIONSHIP_CODE,NEXTOFKIN_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,BIRTH_DATE,OCCUPATION_CODE,ADDRESS,DISTRICT_CODE,TOWN_CODE,POSTCODE,STATE_CODE,COUNTRY_CODE,MOBILE_PHONE,HOME_PHONE,E_MAIL) VALUES ('" + nokinfo[0] + "','" + nokinfo[1] + "','" + nokinfo[2] + "','" + nokinfo[3] + "','" + nokinfo[4] + "','" + nokinfo[5] + "','" + nokinfo[6] + "','" + nokinfo[7] + "','" + nokinfo[8] + "','" + nokinfo[9] + "','" + nokinfo[10] + "','" + nokinfo[11] + "','" + nokinfo[12] + "','" + nokinfo[13] + "','" + nokinfo[14] + "','" + nokinfo[15] + "','" + nokinfo[16] + "','" + nokinfo[17] + "','" + nokinfo[18] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        String sql2 = "INSERT INTO AUTOGENERATE_NOKSNO (NEXTOFKIN_SEQ_NO) VALUES ('" + nokinfo[1] + "')";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.executeUpdate();
        conn.close();
    }

    //save patient biodata into database
    public void addPatientBiodata(String[] biodata) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO PMS_PATIENT_BIODATA (PMI_NO,PMI_NO_TEMP,PATIENT_NAME,TITLE_CODE,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE,BIRTH_DATE,SEX_CODE,MARITAL_STATUS_CODE,RACE_CODE,NATIONALITY,RELIGION_CODE,BLOOD_TYPE,BLOOD_RHESUS_CODE,ALLERGY_IND,CHRONIC_DISEASE_IND,ORGAN_DONOR_IND,HOME_ADDRESS,HOME_DISTRICT_CODE,HOME_TOWN_CODE,HOME_POSTCODE,HOME_STATE_CODE,HOME_COUNTRY_CODE,HOME_PHONE,POSTAL_ADDRESS,POSTAL_DISTRICT_CODE,POSTAL_TOWN_CODE,POSTAL_POSTCODE,POSTAL_STATE_CODE,POSTAL_COUNTRY_CODE,MOBILE_PHONE) VALUES ('" + biodata[0] + "','" + biodata[1] + "','" + biodata[2] + "','" + biodata[3] + "','" + biodata[4] + "','" + biodata[5] + "','" + biodata[6] + "','" + biodata[7] + "','" + biodata[8] + "','" + biodata[9] + "','" + biodata[10] + "','" + biodata[11] + "','" + biodata[12] + "','" + biodata[13] + "','" + biodata[14] + "','" + biodata[15] + "','" + biodata[16] + "','" + biodata[17] + "','" + biodata[18] + "','" + biodata[19] + "','" + biodata[20] + "','" + biodata[21] + "','" + biodata[22] + "','" + biodata[23] + "','" + biodata[24] + "','" + biodata[25] + "','" + biodata[26] + "','" + biodata[27] + "','" + biodata[28] + "','" + biodata[29] + "','" + biodata[30] + "','" + biodata[31] + "','" + biodata[32] + "','" + biodata[33] + "','" + biodata[34] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        String sql2 = "INSERT INTO PMS_LOCAL_PATIENT_BIODATA (PMI_NO,PMI_NO_TEMP,PATIENT_NAME,TITLE_CODE,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE,BIRTH_DATE,SEX_CODE,MARITAL_STATUS_CODE,RACE_CODE,NATIONALITY,RELIGION_CODE,BLOOD_TYPE,BLOOD_RHESUS_CODE,ALLERGY_IND,CHRONIC_DISEASE_IND,ORGAN_DONOR_IND,HOME_ADDRESS,HOME_DISTRICT_CODE,HOME_TOWN_CODE,HOME_POSTCODE,HOME_STATE_CODE,HOME_COUNTRY_CODE,HOME_PHONE,POSTAL_ADDRESS,POSTAL_DISTRICT_CODE,POSTAL_TOWN_CODE,POSTAL_POSTCODE,POSTAL_STATE_CODE,POSTAL_COUNTRY_CODE,MOBILE_PHONE) VALUES ('" + biodata[0] + "','" + biodata[1] + "','" + biodata[2] + "','" + biodata[3] + "','" + biodata[4] + "','" + biodata[5] + "','" + biodata[6] + "','" + biodata[7] + "','" + biodata[8] + "','" + biodata[9] + "','" + biodata[10] + "','" + biodata[11] + "','" + biodata[12] + "','" + biodata[13] + "','" + biodata[14] + "','" + biodata[15] + "','" + biodata[16] + "','" + biodata[17] + "','" + biodata[18] + "','" + biodata[19] + "','" + biodata[20] + "','" + biodata[21] + "','" + biodata[22] + "','" + biodata[23] + "','" + biodata[24] + "','" + biodata[25] + "','" + biodata[26] + "','" + biodata[27] + "','" + biodata[28] + "','" + biodata[29] + "','" + biodata[30] + "','" + biodata[31] + "','" + biodata[32] + "','" + biodata[33] + "','" + biodata[34] + "')";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        String sql3 = "INSERT INTO AUTOGENERATE_PMI (PMI_NO) VALUES ('" + biodata[0] + "')";
        PreparedStatement ps3 = conn.prepareStatement(sql3);
        ps.executeUpdate();
        ps2.executeUpdate();
        ps3.executeUpdate();
        conn.close();
        // sql = "INSERT INTO AUTOGENERATE_PMI (PMI_NO) VALUES ('" + biodata[0] + "')";
        //ps = conn.prepareStatement(sql);
        // ps.executeUpdate();

    }

    //save patient employment information into database
    public void addPatientEmploymentInfo(String[] employmentinfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO PMS_EMPLOYMENT (PMI_NO,EMPLOYMENT_SEQ_NO,EMPLOYER_CODE,EMPLOYER_NAME,OCCUPATION_CODE,JOINED_DATE,INCOME_RANGE_CODE,HEALTH_FACILITY,CREATE_DATE,EMPLOYMENT_STATUS) VALUES ('" + employmentinfo[0] + "','" + employmentinfo[1] + "','" + employmentinfo[2] + "','" + employmentinfo[3] + "','" + employmentinfo[4] + "','" + employmentinfo[5] + "','" + employmentinfo[6] + "','" + employmentinfo[7] + "','" + employmentinfo[8] + "','" + employmentinfo[9] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        String sql2 = "INSERT INTO AUTOGENERATE_ESNO (EMPLOYMENT_SEQ_NO) VALUES ('" + employmentinfo[1] + "')";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps.executeUpdate();
        ps2.executeUpdate();
        conn.close();
    }

    //save patient employment information into database
    public void registerAndCreateQueue(String[] queue) throws ClassNotFoundException, SQLException {
       Connection conn = Conn.MySQLConnect();

        String sql = "INSERT INTO PMS_EPISODE (PMI_NO,EPISODE_DATE,NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,"
                + "RN_NO,PATIENT_CATEGORY_CODE,VISIT_TYPE_CODE,EMERGENCY_TYPE_CODE,"
                + "ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE,DISCIPLINE_CODE,SUBDISCIPLINE_CODE,"
                + "CONSULTATION_ROOM,COMMON_QUEUE,DOCTOR,PRIORITY_GROUP_CODE,POLICE_CASE,"
                + "COMMUNICABLE_DISEASE_CODE,NATURAL_DISASTER_CODE,DOC_TYPE,GUARDIAN_IND,REFERENCE_NO,"
                + "GROUP_GUARDIAN,GL_EXPIRY_DATE,EPISODE_TIME,STATUS)"
                + " VALUES ('" + queue[0] + "','" + queue[1] + "','" + queue[2] + "'"
                + ",'" + queue[3] + "','" + queue[4] + "','" + queue[5] + "','" + queue[6] + "'"
                + ",'" + queue[7] + "','" + queue[8] + "','" + queue[9] + "','" + queue[10] + "'"
                + ",'" + queue[11] + "','" + queue[12] + "','" + queue[13] + "','" + queue[14] + "'"
                + ",'" + queue[15] + "','" + queue[16] + "','" + queue[17] + "','" + queue[18] + "'"
                + ",'" + queue[19] + "','" + queue[20] + "','" + queue[21] + "','" + queue[22] + "'"
                + ",'" + queue[23] + "','" + queue[24] + "','" + queue[25] + "','" + queue[26] + "'"
                + ",'" + queue[27] + "','" + queue[28] + "')";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.executeUpdate();
        conn.close();
    }

    //get patient family information from database using PMINO
    public String[] getFamilyDetail(String family) throws ClassNotFoundException, SQLException {
        String[] familyinfo = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_FAMILY WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, family);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                familyinfo[i] = rs.getString(i + 1);
            }
        }


        conn.close();
        return familyinfo;
    }

    //get patient family information from database using New IC
    public String[] getFamilyDetailUsingNewIC(String family1) throws ClassNotFoundException, SQLException {
        String[] familyinfo1 = new String[6];
        Connection conn = Conn.MySQLConnect();

        String sql = "SELECT * FROM PMS_FAMILY WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO = ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, family1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                familyinfo1[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return familyinfo1;
    }

    //get patient family information from database using Old IC
    public String[] getFamilyDetailUsingOldIC(String family2) throws ClassNotFoundException, SQLException {
        String[] familyinfo2 = new String[6];
        Connection conn = Conn.MySQLConnect();

        String sql = "SELECT * FROM PMS_FAMILY WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO = ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, family2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                familyinfo2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return familyinfo2;
    }

    //get patient family information from database using ID
    public String[] getFamilyDetailUsingID(String family3, String family4) throws ClassNotFoundException, SQLException {
        String[] familyinfo3 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_FAMILY WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE ID_NO = ? AND ID_TYPE=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, family3);
        ps.setString(2, family4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                familyinfo3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return familyinfo3;
    }

    //get patient next of kin information from database using PMINO
    public String[] getNokDetail(String nok) throws ClassNotFoundException, SQLException {
        String[] nokinfo = new String[19];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_NEXTOFKIN WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nok);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 19; i++) {
                System.out.print(rs.getString(i + 1));
                nokinfo[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return nokinfo;
    }

    //get patient next of kin information from database using New IC
    public String[] getNokDetailUsingNewIC(String nok1) throws ClassNotFoundException, SQLException {
        String[] nokinfo1 = new String[19];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_NEXTOFKIN WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nok1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 19; i++) {
                System.out.print(rs.getString(i + 1));
                nokinfo1[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return nokinfo1;
    }

    //get patient next of kin information from database using Old IC
    public String[] getNokDetailUsingOldIC(String nok2) throws ClassNotFoundException, SQLException {
        String[] nokinfo2 = new String[19];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_NEXTOFKIN WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nok2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 19; i++) {
                System.out.print(rs.getString(i + 1));
                nokinfo2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return nokinfo2;
    }

    //get patient next of kin information from database using ID
    public String[] getNokDetailUsingID(String nok3, String nok4) throws ClassNotFoundException, SQLException {
        String[] nokinfo3 = new String[19];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_NEXTOFKIN WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE ID_NO = ? AND ID_TYPE=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nok3);
        ps.setString(2, nok4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 19; i++) {
                System.out.print(rs.getString(i + 1));
                nokinfo3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return nokinfo3;
    }

    //get patient insurance information from database using PMINO
    public String[] getInsuranceDetail(String insurance) throws ClassNotFoundException, SQLException {
        String[] insuranceinfo = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_MEDICAL_INSURANCE WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, insurance);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                insuranceinfo[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return insuranceinfo;
    }

    //get patient insurance information from database using New IC
    public String[] getInsuranceDetailUsingNewIC(String insurance1) throws ClassNotFoundException, SQLException {
        String[] insuranceinfo1 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_MEDICAL_INSURANCE WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, insurance1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                insuranceinfo1[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return insuranceinfo1;
    }

    //get patient insurance information from database using Old IC
    public String[] getInsuranceDetailUsingOldIC(String insurance2) throws ClassNotFoundException, SQLException {
        String[] insuranceinfo2 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_MEDICAL_INSURANCE WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, insurance2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                insuranceinfo2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return insuranceinfo2;
    }

    //get patient insurance information from database using ID
    public String[] getInsuranceDetailUsingID(String insurance3, String insurance4) throws ClassNotFoundException, SQLException {
        String[] insuranceinfo3 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_MEDICAL_INSURANCE WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE ID_NO=? AND ID_TYPE=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, insurance3);
        ps.setString(2, insurance4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                insuranceinfo3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return insuranceinfo3;
    }

    //get patient employment information from database using PMINO
    public String[] getEmploymentDetail(String employment) throws ClassNotFoundException, SQLException {
        String[] employmentinfo = new String[10];
       Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_EMPLOYMENT WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, employment);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 10; i++) {
                System.out.println(rs.getString(i + 1));
                employmentinfo[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return employmentinfo;
    }

    //get patient employment information from database using New IC
    public String[] getEmploymentDetailUsingNewIC(String employment1) throws ClassNotFoundException, SQLException {
        String[] employmentinfo1 = new String[10];
       Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_EMPLOYMENT WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, employment1);
        ResultSet rs = ps.executeQuery();

        //friza
        List listEmp = new ArrayList();

        while (rs.next()) {
            for (int i = 0; i < 10; i++) {
                System.out.print(rs.getString(i + 1));
                employmentinfo1[i] = rs.getString(i + 1);
                //friza
                listEmp.add(i, rs.getString(i + 1));
            }
        }

        conn.close();
        return employmentinfo1;
    }

    //get patient employment information from database using Old IC
    public String[] getEmploymentDetailUsingOldIC(String employment2) throws ClassNotFoundException, SQLException {
        String[] employmentinfo2 = new String[10];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_EMPLOYMENT WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, employment2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 10; i++) {
                System.out.print(rs.getString(i + 1));
                employmentinfo2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return employmentinfo2;
    }

    //get patient employment information from database using ID
    public String[] getEmploymentDetailUsingID(String employment3, String employment4) throws ClassNotFoundException, SQLException {
        String[] employmentinfo3 = new String[10];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_EMPLOYMENT WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE ID_NO=? AND ID_TYPE=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, employment3);
        ps.setString(2, employment4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 10; i++) {
                System.out.print(rs.getString(i + 1));
                employmentinfo3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return employmentinfo3;
    }

    //get patient biodata from database using PMINO
    public String[] getBiodata(String biodatainfo) throws ClassNotFoundException, SQLException {
        String[] biodatainformation = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodatainfo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                biodatainformation[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return biodatainformation;
    }

    //get patient biodata from database using New IC
    public String[] getBiodataUsingNewIC(String biodatainfo1) throws ClassNotFoundException, SQLException {
        String[] biodatainformation1 = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodatainfo1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                biodatainformation1[i] = rs.getString(i + 1);


            }
        }

        conn.close();
        return biodatainformation1;
    }

    //get patient biodata from database using New IC
    public List getBiodataUsingNewIC_1(String biodatainfo1) throws ClassNotFoundException, SQLException {
        //String[] biodatainformation1 = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodatainfo1);
        ResultSet rs = ps.executeQuery();

        List listBio = new ArrayList();
        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                //biodatainformation1[i] = rs.getString(i + 1);

                listBio.add(i, rs.getString(i + 1));
            }
        }

        conn.close();
        return listBio;
    }

    //get patient biodata from database using Old IC
    public String[] getBiodataUsingOldIC(String biodatainfo2) throws ClassNotFoundException, SQLException {
        String[] biodatainformation2 = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodatainfo2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                biodatainformation2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return biodatainformation2;
    }

    //get patient biodata from database using ID
    public String[] getBiodataUsingID(String biodatainfo3, String biodatainfo4) throws ClassNotFoundException, SQLException {
        String[] biodatainformation3 = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE ID_NO=? AND ID_TYPE=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodatainfo3);
        ps.setString(2, biodatainfo4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                biodatainformation3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return biodatainformation3;
    }

    //get existing patient biodata from database using PMINO
    public String[] getRegisterBiodata(String existbiodata) throws ClassNotFoundException, SQLException {
        String[] existBiodataInfo = new String[8];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, existbiodata);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 8; i++) {
                System.out.print(rs.getString(i + 1));
                existBiodataInfo[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return existBiodataInfo;
    }

    //get existing patient biodata from database using new ic no
    public String[] getRegisterBiodataUsingNewIC(String existbiodata1) throws ClassNotFoundException, SQLException {
        String[] existBiodataInfo1 = new String[8];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, existbiodata1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 8; i++) {
                System.out.print(rs.getString(i + 1));
                existBiodataInfo1[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return existBiodataInfo1;
    }

    //get existing patient biodata from database using old ic no
    public String[] getRegisterBiodataUsingOldIC(String existbiodata2) throws ClassNotFoundException, SQLException {
        String[] existBiodataInfo2 = new String[8];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, existbiodata2);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 8; i++) {
                System.out.print(rs.getString(i + 1));
                existBiodataInfo2[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return existBiodataInfo2;
    }

    //get existing patient biodata from database using id no and id type
    public String[] getRegisterBiodataUsingID(String existbiodata3, String existbiodata4) throws ClassNotFoundException, SQLException {
        String[] existBiodataInfo3 = new String[8];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA WHERE ID_NO=? AND ID_TYPE =?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, existbiodata3);
        ps.setString(2, existbiodata4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 8; i++) {
                System.out.print(rs.getString(i + 1));
                existBiodataInfo3[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return existBiodataInfo3;

    }

    //get patient biodata from database using MyKad and show on registration screen
    public String[] getRegisterBiodataUsingMyKad(String existbiodata4) throws ClassNotFoundException, SQLException {
        String[] existBiodataInfo4 = new String[8];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO,PATIENT_NAME,NEW_IC_NO,OLD_IC_NO,ID_TYPE,ID_NO,ELIGIBILITY_CATEGORY_CODE,ELIGIBILITY_TYPE_CODE FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, existbiodata4);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 8; i++) {
                System.out.print(rs.getString(i + 1));
                existBiodataInfo4[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return existBiodataInfo4;
    }

    //get patient biodata from database using MyKad and show on patient master index screen
    public String[] getBiodataInfoUsingMyKad(String biodataInfo) throws ClassNotFoundException, SQLException {
        String[] biodata = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, biodataInfo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                biodata[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return biodata;
    }

    //get patient employment information from database using MyKad
    public String[] getEmploymentInfoUsingMyKad(String employment5) throws ClassNotFoundException, SQLException {
        String[] employmentInfo5 = new String[10];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_EMPLOYMENT WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, employment5);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 10; i++) {
                System.out.print(rs.getString(i + 1));
                employmentInfo5[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return employmentInfo5;
    }

    //get patient employment information from database using MyKad
    public String[] getNOKInfoUsingMyKad(String nok5) throws ClassNotFoundException, SQLException {
        String[] nokInfo5 = new String[19];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_NEXTOFKIN WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nok5);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 19; i++) {
                System.out.print(rs.getString(i + 1));
                nokInfo5[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return nokInfo5;
    }

    //get patient family information from database using MyKad
    public String[] getFamilyInfoUsingMyKad(String family5) throws ClassNotFoundException, SQLException {
        String[] familyInfo5 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_FAMILY WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, family5);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                familyInfo5[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return familyInfo5;
    }

    //get patient insurance information from database using MyKad
    public String[] getInsuranceInfoUsingMyKad(String insurance5) throws ClassNotFoundException, SQLException {
        String[] insuranceInfo5 = new String[6];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_MEDICAL_INSURANCE WHERE PMI_NO = (SELECT PMI_NO FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, insurance5);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 6; i++) {
                System.out.print(rs.getString(i + 1));
                insuranceInfo5[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return insuranceInfo5;
    }

    //get autogenerate pminumber from database
    public String[] getAutogeneratePMI() throws ClassNotFoundException, SQLException {
        String[] AutogeneratePMI = new String[1];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT PMI_NO FROM AUTOGENERATE_PMI";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String pmi = null;


        while (rs.next()) {
            //for(int i=0;i<1;i++)
            {
                pmi = rs.getString(1);
            }

            int num = Integer.parseInt(pmi.substring(3, pmi.length()));
            num += 1;

            String formatted = String.format("%05d", num);
            AutogeneratePMI[0] = formatted;
        }

        conn.close();
        return AutogeneratePMI;
    }

    //get autogenerate family sequence number from database
    public String[] getAutogenerateFSNo() throws ClassNotFoundException, SQLException {
        String[] AutogenerateFS = new String[1];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT FAMILY_SEQ_NO FROM AUTOGENERATE_FSNO";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String pmi = null;


        while (rs.next()) {
            //for(int i=0;i<1;i++)
            {
                pmi = rs.getString(1);
            }

            int num = Integer.parseInt(pmi.substring(2, pmi.length()));
            num += 1;

            String formatted = String.format("%05d", num);
            AutogenerateFS[0] = formatted;
        }

        conn.close();
        return AutogenerateFS;
    }

    //get autogenerate family sequence number from database
    public String[] getAutogenerateNOKNo() throws ClassNotFoundException, SQLException {
        String[] AutogenerateNOK = new String[1];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT NEXTOFKIN_SEQ_NO FROM AUTOGENERATE_NOKSNO";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String pmi = null;


        while (rs.next()) {

            {
                pmi = rs.getString(1);
            }

            int num = Integer.parseInt(pmi.substring(4, pmi.length()));
            num += 1;

            String formatted = String.format("%05d", num);
            AutogenerateNOK[0] = formatted;
        }

        conn.close();
        return AutogenerateNOK;
    }

    //get autogenerate employment sequence number from database
    public String[] getAutogenerateESNo() throws ClassNotFoundException, SQLException {
        String[] AutogenerateES = new String[1];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT EMPLOYMENT_SEQ_NO FROM AUTOGENERATE_ESNO";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String pmi = null;


        while (rs.next()) {

            {
                pmi = rs.getString(1);
            }

            int num = Integer.parseInt(pmi.substring(2, pmi.length()));
            num += 1;

            String formatted = String.format("%05d", num);
            AutogenerateES[0] = formatted;
        }

        conn.close();
        return AutogenerateES;
    }

    //get autogenerate receipt no from database
    public String[] getAutogenerateRecNo() throws ClassNotFoundException, SQLException {
        String[] AutogenerateRec = new String[1];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT RECEIPT_NO FROM AUTOGENERATE_RECNO";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String pmi = null;


        while (rs.next()) {

            {
                pmi = rs.getString(1);
            }

            int num = Integer.parseInt(pmi.substring(3, pmi.length()));
            num += 1;

            String formatted = String.format("%05d", num);
            AutogenerateRec[0] = formatted;
        }

        conn.close();
        return AutogenerateRec;
    }

    //save receipt number into database
    public void addReceiptNo(String[] receipt) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "INSERT INTO AUTOGENERATE_RECNO (RECEIPT_NO) VALUES ('" + receipt[0] + "')";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //get patient biodata from database when actor click table row from appointment list
    public String[] getAppointmentBiodata(String appointmentbiodatainfo) throws ClassNotFoundException, SQLException {
        String[] appointmentbiodatainformation = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PMI_NO=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, appointmentbiodatainfo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                appointmentbiodatainformation[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return appointmentbiodatainformation;
    }

    //get patient biodata from database when actor click table row from referral list
    public String[] getReferralBiodata(String referralInfo) throws ClassNotFoundException, SQLException {
        String[] referralInformation = new String[35];
        Connection conn = Conn.MySQLConnect();
        String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE PATIENT_NAME=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, referralInfo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            for (int i = 0; i < 35; i++) {
                System.out.print(rs.getString(i + 1));
                referralInformation[i] = rs.getString(i + 1);
            }
        }

        conn.close();
        return referralInformation;
    }


    /*
    //get patient biodata from database when actor click table row from rEFERRAL list
    public String[] getReferralBiodataUsingNewIC(String referralinfo) throws ClassNotFoundException, SQLException
    {
    String[] referralinformation = new String[35];
    Connection conn = Conn.MySQLConnect();
    String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE NEW_IC_NO=?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, referralinfo);
    ResultSet rs = ps.executeQuery();
    
    while(rs.next()){
    for(int i=0;i<35;i++)
    {
    System.out.print(rs.getString(i+1));
    referralinformation[i]=rs.getString(i+1);
    }
    }
    
    conn.close();
    return referralinformation;
    }
    
    
    //get patient biodata from database when actor click table row from rEFERRAL list
    public String[] getReferralBiodataUsingOldIC(String referralinfo1) throws ClassNotFoundException, SQLException
    {
    String[] referralinformation1 = new String[35];
    Connection conn = Conn.MySQLConnect();
    String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE OLD_IC_NO=?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, referralinfo1);
    ResultSet rs = ps.executeQuery();
    
    while(rs.next()){
    for(int i=0;i<35;i++)
    {
    System.out.print(rs.getString(i+1));
    referralinformation1[i]=rs.getString(i+1);
    }
    }
    
    conn.close();
    return referralinformation1;
    }
    
    
    //get patient biodata from database when actor click table row from rEFERRAL list
    public String[] getReferralBiodataUsingID(String referralinfo2) throws ClassNotFoundException, SQLException
    {
    String[] referralinformation2 = new String[35];
    Connection conn = Conn.MySQLConnect();
    String sql = "SELECT * FROM PMS_PATIENT_BIODATA WHERE ID_NO=?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, referralinfo2);
    ResultSet rs = ps.executeQuery();
    
    while(rs.next()){
    for(int i=0;i<35;i++)
    {
    System.out.print(rs.getString(i+1));
    referralinformation2[i]=rs.getString(i+1);
    }
    }
    
    conn.close();
    return referralinformation2;
    }
     */
    //update patient insurance information and save into database
    public void updatePatientInsuranceInfo(String[] insuranceInfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "UPDATE PMS_MEDICAL_INSURANCE SET PMI_NO = '" + insuranceInfo[0] + "',INSURANCE_COMPANY_CODE = '" + insuranceInfo[1] + "',POLICY_NO = '" + insuranceInfo[2] + "',MATURITY_DATE = '" + insuranceInfo[3] + "',HEALTH_FACILITY = '" + insuranceInfo[4] + "',POLICY_STATUS = '" + insuranceInfo[5] + "' WHERE PMI_NO = '" + insuranceInfo[0] + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //update patient family information and save into database
    public void updatePatientFamilyInfo(String[] familyInfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "UPDATE PMS_FAMILY SET PMI_NO = '" + familyInfo[0] + "',FAMILY_SEQ_NO = '" + familyInfo[1] + "',FAMILY_RELATIONSHIP_CODE = '" + familyInfo[2] + "',PMI_NO_FAMILY = '" + familyInfo[3] + "',FAMILY_MEMBER_NAME = '" + familyInfo[4] + "',OCCUPATION_CODE = '" + familyInfo[5] + "' WHERE PMI_NO = '" + familyInfo[0] + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //update patient next of kin information and save into database
    public void updatePatientNextOfKinInfo(String[] nokInfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "UPDATE PMS_NEXTOFKIN SET PMI_NO = '" + nokInfo[0] + "',NEXTOFKIN_SEQ_NO = '" + nokInfo[1] + "',NEXTOFKIN_RELATIONSHIP_CODE = '" + nokInfo[2] + "',NEXTOFKIN_NAME = '" + nokInfo[3] + "',NEW_IC_NO = '" + nokInfo[4] + "',OLD_IC_NO = '" + nokInfo[5] + "',ID_TYPE = '" + nokInfo[6] + "',ID_NO = '" + nokInfo[7] + "',BIRTH_DATE = '" + nokInfo[8] + "',OCCUPATION_CODE = '" + nokInfo[9] + "',ADDRESS = '" + nokInfo[10] + "',DISTRICT_CODE = '" + nokInfo[11] + "',TOWN_CODE = '" + nokInfo[12] + "',POSTCODE = '" + nokInfo[13] + "',STATE_CODE = '" + nokInfo[14] + "',COUNTRY_CODE = '" + nokInfo[15] + "',MOBILE_PHONE = '" + nokInfo[16] + "',HOME_PHONE = '" + nokInfo[17] + "',E_MAIL = '" + nokInfo[18] + "' WHERE PMI_NO = '" + nokInfo[0] + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //update patient employment information and save into database
    public void updatePatientEmploymentInfo(String[] employmentInfo) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "UPDATE PMS_EMPLOYMENT SET PMI_NO = '" + employmentInfo[0] + "',EMPLOYMENT_SEQ_NO = '" + employmentInfo[1] + "',EMPLOYER_CODE = '" + employmentInfo[2] + "',EMPLOYER_NAME = '" + employmentInfo[3] + "',OCCUPATION_CODE = '" + employmentInfo[4] + "',JOINED_DATE = '" + employmentInfo[5] + "',INCOME_RANGE_CODE = '" + employmentInfo[6] + "',HEALTH_FACILITY = '" + employmentInfo[7] + "',CREATE_DATE = '" + employmentInfo[8] + "',EMPLOYMENT_STATUS = '" + employmentInfo[9] + "' WHERE PMI_NO = '" + employmentInfo[0] + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    //update patient biodata and save into database
    public void updatePatientBiodata(String[] Biodata) throws ClassNotFoundException, SQLException {
        Connection conn = Conn.MySQLConnect();
        String sql = "UPDATE PMS_PATIENT_BIODATA SET PMI_NO = '" + Biodata[0] + "',PMI_NO_TEMP = '" + Biodata[1] + "',PATIENT_NAME = '" + Biodata[2] + "',TITLE_CODE = '" + Biodata[3] + "',NEW_IC_NO = '" + Biodata[4] + "',OLD_IC_NO = '" + Biodata[5] + "',ID_TYPE = '" + Biodata[6] + "',ID_NO = '" + Biodata[7] + "',ELIGIBILITY_CATEGORY_CODE = '" + Biodata[8] + "',ELIGIBILITY_TYPE_CODE = '" + Biodata[9] + "',BIRTH_DATE = '" + Biodata[10] + "',SEX_CODE = '" + Biodata[11] + "',MARITAL_STATUS_CODE = '" + Biodata[12] + "',RACE_CODE = '" + Biodata[13] + "',NATIONALITY = '" + Biodata[14] + "',RELIGION_CODE = '" + Biodata[15] + "',BLOOD_TYPE = '" + Biodata[16] + "',BLOOD_RHESUS_CODE = '" + Biodata[17] + "',ALLERGY_IND = '" + Biodata[18] + "',CHRONIC_DISEASE_IND = '" + Biodata[19] + "',ORGAN_DONOR_IND = '" + Biodata[20] + "',HOME_ADDRESS = '" + Biodata[21] + "',HOME_DISTRICT_CODE = '" + Biodata[22] + "',HOME_TOWN_CODE = '" + Biodata[23] + "',HOME_POSTCODE = '" + Biodata[24] + "',HOME_STATE_CODE = '" + Biodata[25] + "',HOME_COUNTRY_CODE = '" + Biodata[26] + "',HOME_PHONE = '" + Biodata[27] + "',POSTAL_ADDRESS = '" + Biodata[28] + "',POSTAL_DISTRICT_CODE = '" + Biodata[29] + "',POSTAL_TOWN_CODE = '" + Biodata[30] + "',POSTAL_POSTCODE = '" + Biodata[31] + "',POSTAL_STATE_CODE = '" + Biodata[32] + "',POSTAL_COUNTRY_CODE = '" + Biodata[33] + "',MOBILE_PHONE = '" + Biodata[34] + "' WHERE PMI_NO = '" + Biodata[0] + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }
    //get town information from database
    /*public String[] getTownInfo() throws SQLException, ClassNotFoundException{
    
    String[] towninfo = new String[16];
    Connection conn = Conn.MySQLConnect();
    String sql = "SELECT * FROM COMBOBOX_PMIFORM";
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    
    for(int i=0;rs.next();i++){
    
    System.out.print(rs.getString(1));
    towninfo[i]=rs.getString(1);
    }
    
    //while(rs.next())
    //{
    // towninfo[i].addItem(rs.getString(1));
    //}
    
    conn.close();
    return towninfo;
    }
    
    //get state information from database
    public String[] getStateInfo() throws SQLException, ClassNotFoundException{
    
    String[] stateinfo = new String[16];
    Connection conn = Conn.MySQLConnect();
    String sql = "SELECT * FROM COMBOBOX_PMIFORM";
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    
    for(int i=0;rs.next();i++){
    
    System.out.print(rs.getString(2));
    stateinfo[i]=rs.getString(2);
    }
    
    //while(rs.next())
    //{
    // towninfo[i].addItem(rs.getString(1));
    //}
    
    conn.close();
    return stateinfo;
    }
     */
}
