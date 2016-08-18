/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;

import bean.VTSBean;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public interface Message extends Remote {
    
    // registration
    boolean isAlreadyRegistered(String pmino) throws RemoteException;
    
    // CIS order drug
    ResultSet getDrugCIS(String search) throws RemoteException;
    
    // procedure
    String insertPOS(String PMI, String dataPOS) throws RemoteException;
    ArrayList<ArrayList<String>> getProcedures() throws RemoteException;
    ArrayList<ArrayList<String>> getProceduresBasedOnPmiNo(String pmiNo) throws RemoteException;
    ArrayList<ArrayList<String>> getProcedureDetail(String orderNo) throws RemoteException;
    boolean updateProcedures(ArrayList<ArrayList<String>> prod) throws RemoteException;
    
    // fast sql query
    boolean setQuerySQL(String query) throws RemoteException;
    ArrayList<ArrayList<String>> getQuerySQL(String query) throws RemoteException;
    
    // fast query
    ArrayList<ArrayList<String>> getQuery(String query, int col, String data[]) throws RemoteException;
    boolean setQuery(String query, String data[]) throws RemoteException;
    
    // update pms
    boolean isUpdatePatientBiodata(String[] Biodata) throws RemoteException;
    
    // reset password
    String getPassword(String icno, String userid) throws RemoteException;
    
    // change password
    boolean changePassword(String userid, String pwd) throws RemoteException;
    
    // change room no
    boolean changeRoomNo(String userid, String roomNo) throws RemoteException;
    
    // update pms_patient_biodata
    boolean updatePmsPatientBiodata(String pmino, ArrayList<String> column, ArrayList<String> data) throws RemoteException;
    
    //referral
    ArrayList<ArrayList<String>> getDoctors(String user_id) throws RemoteException;
    ArrayList<String> getDoctor(String user_name) throws RemoteException;
    boolean addReferral(ArrayList<String> data) throws RemoteException;
    ArrayList<ArrayList<String>> getReferral(String user_id) throws RemoteException;
    boolean delReferral(String pmino, String episodetime) throws RemoteException;
    
    //queue calling system
    Vector getQueueCallingSystem() throws RemoteException;
    
    //consultation queue
    boolean isConsult(String pmino, String time, String doctor) throws RemoteException;
    String[] simplifyCheckBiodata(String pmiNo, String time, String doctor) throws RemoteException;
    
    //Pharmacy order drug
    void addAUTOGENERATE_ONO(String oNo) throws RemoteException;
    void addPIS_ORDER_MASTER(String oNo, String pmiNo, String hfc, Timestamp ec,
            Timestamp ed, Timestamp oDate, String id, String oF, String oTo,
            int spubNo, int toi, boolean oSM) throws RemoteException;
    void addPIS_ORDER_DETAIL(String oNo, String dmdc, String dtraden,
            String dLfreq, String droute, String ddosage, String dstrength,
            String dLqty, String dLadvisory, int duration1, String orderStatus,
            double qtyPerTime1, int totalQty, String sOUM, int qtydispensed1,
            String dOUM, boolean oSD) throws RemoteException;
    ResultSet getPrescriptionNote(String pmiNo) throws RemoteException;
    ResultSet getPrescriptionNote2(String oNo) throws RemoteException;
    ResultSet getAUTOGENERATE_ONO() throws RemoteException;
    
    //code MC
    String getPMINo(String search, String idtype, int type) throws RemoteException;
    
    //code report
    ArrayList<ArrayList<String>> getReport(int type, String date1, String date2) throws RemoteException;
    ArrayList<ArrayList<String>> getReportICD10(String date) throws RemoteException;
    String getICD10SortReport(String date) throws RemoteException;
    
    //code login rmi
    boolean isLogin(String username, String password) throws RemoteException;
    ArrayList<String> getLoginData(String username, String password) throws RemoteException;
    boolean insertData(ArrayList<String> data_user) throws RemoteException;
    
    //code maintain administrator
    ArrayList<ArrayList<String>> getListOfStaffs(String user_id) throws RemoteException;
    ArrayList<ArrayList<String>> getStaffs(String user_id) throws RemoteException;
    boolean addStaff(String data1[], String data2[]) throws RemoteException;
    boolean deleteStaff(String user_id) throws RemoteException;
    boolean isStaffs(String user_id) throws RemoteException;
    boolean updateStaff(String user_id, String cols1[], String data1[], String cols2[], String data2[]) throws RemoteException;
    ArrayList<String> getStaffLogin(String user_id, String password) throws RemoteException;
    
    ResultSet getATC(String atcn) throws RemoteException;
    ResultSet getMDC(String drugn) throws RemoteException;
    //get new pmi no
    String getPMI(String ic) throws RemoteException;

    ArrayList<String> getBioPDI(String pmiNo) throws RemoteException;
    
    String [] getBiodata(String pmiNo) throws RemoteException;
    
    //dispense code
    ArrayList<String> getDispenseMaster(String orderNo) throws RemoteException;
    boolean insertDispenseMaster(String [] data1, String data2, boolean data3) throws RemoteException;
    ArrayList<String> getOrderDetail(String orderNo, String drugCode) throws RemoteException;
    boolean insertDispenseDetail(String[] data1, int data2, boolean data3) throws RemoteException;
    boolean updateOrderDetail(int qtyDispensed, String orderNo, String drugCode, String statusDrug) throws RemoteException;
    boolean isOrderDetail(String orderNo) throws RemoteException;
    boolean updateOrderMaster(String orderNo, int status) throws RemoteException;
    boolean updateDispensedMaster(String orderNo, int status) throws RemoteException;
    
    void insertD(String [] disp)throws RemoteException;
    void insertOrder(String[] order)throws RemoteException;
    
    //begin of appoinment
    Vector<String> getpatientInfoIC(String IC)throws RemoteException;
    Vector<String> getpatientInfoPMI(String PMI) throws RemoteException;
    boolean updateAppointment(Vector<String> patient) throws RemoteException;
    boolean makeAppointment(Vector<String> patient)throws RemoteException;
    boolean deleteAppointment(String appID) throws RemoteException;
    String getAutoAppointID() throws RemoteException;
    Vector <String> getAppointment (String ID) throws RemoteException;
    ArrayList<String> getPatient(int index,String sql,String [] calDate) throws RemoteException;
    //end of appoinment
    
    void updateStatEpisode(String PMINumber, String TimeEpisode, String status, String doctor, String referer) 
            throws RemoteException;
    void updateStatEpisode2(String PMINumber, String TimeEpisode, String now) 
            throws RemoteException;
    
    Vector getQueueNameList(String name, String hfcCode, int tanda) throws RemoteException;
    
    String [] getAutoGen(int stat) throws RemoteException;
    
    String [] getBio(int stat, String ic, String type, int num_col) throws RemoteException;
    
    void insertRegCreateQ(String [] queue) throws RemoteException;
    
    void insertPatientBiodata(String [] biodata) throws RemoteException;
    
    ArrayList<ArrayList<String>> getOrderMasterAll(int stat, String pmi_no, String order_no, String hfc_code) throws RemoteException;
    
    String insertDTO(String PMI, String dataDTO) throws RemoteException;
    
    ArrayList<ArrayList<String>> getEHRLatestEpisode(String pmiNo, int limit) throws RemoteException;
    ArrayList<String> getEHRRecords(String pmiNo, int type) throws RemoteException;

    void sayHello(String name) throws RemoteException;
    
    String insertEHRCentral(int status, String pmi, String data, String episodeDate) throws RemoteException;
    
    List getEHRLatest7(String pmi)  throws RemoteException;
    
    String insertPMS(String _Hl7mgs) throws RemoteException;
    
    List getPMS(String IC) throws RemoteException;
    
    List getPMSByOldIC(String oldIC) throws RemoteException;
    
    List getPMSByID(String ID, String type) throws RemoteException;
    
    List getPMSByPMINo(String PMI) throws RemoteException;
    
    ArrayList<String> getPatientBiodata(String selectedPmiNo) throws RemoteException;

    ArrayList<String> getPisOrderMaster(String selectedPmiNo, String orderDate) throws RemoteException;

    ArrayList<ArrayList<String>> getDrugOrderDetail(String text) throws RemoteException;
    
    ArrayList<ArrayList<String>> getEhrCentral(String pmino) throws RemoteException;
    ArrayList<ArrayList<String>> getEhrCentral2(String pmino) throws RemoteException;
    boolean addEhrCentral_vts(String pmino, String vts_data) throws RemoteException;
    
    TableModel getDispensedDrug(String strSql, String prepStatement[]) throws RemoteException; //get list of dispensed drug -- Hariz 20141203
}


