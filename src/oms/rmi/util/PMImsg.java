package oms.rmi.util;

/*this file is to chunk the T12108 hl7 mgs
 */


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WC
 */
public class PMImsg implements Serializable {

    private String fullmsg;
    private String[] PDI;
    private String[] EMP;
    private String[] NOK;
    private String[] FMI;
    private String[] INS;
    Patient patient = new Patient();

    public PMImsg() {
    }

    public PMImsg(String _fullMSG) {

        this.fullmsg = _fullMSG.trim();

    }
    Map map = new HashMap();

    public Map getData(String str) {
        try {
            PDI = patient.getBiodataUsingNewIC(str);
            EMP = patient.getEmploymentDetailUsingNewIC(str);
            NOK = patient.getNokDetailUsingNewIC(str);
            FMI = patient.getFamilyDetailUsingNewIC(str);
            INS = patient.getInsuranceDetailUsingNewIC(str);


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }
        map.put("PDI", PDI);
        map.put("EMP", EMP);
        map.put("NOK", NOK);
        map.put("FMI", FMI);
        map.put("INS", INS);
        String[] a = (String[]) map.get("PDI");
        return map;

    }
    List list = new ArrayList();

    public List<String> getPMSData(String str) {
        try {
            list = new ArrayList();
            PDI = patient.getBiodataUsingNewIC(str);
            EMP = patient.getEmploymentDetailUsingNewIC(str);
            NOK = patient.getNokDetailUsingNewIC(str);
            FMI = patient.getFamilyDetailUsingNewIC(str);
            INS = patient.getInsuranceDetailUsingNewIC(str);

            list.add(0, Arrays.asList(PDI));  
            list.add(1, Arrays.asList(EMP));
            list.add(2, Arrays.asList(NOK));
            list.add(3, Arrays.asList(FMI));
            list.add(4, Arrays.asList(INS));


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

    }
    
    public List<String> getPMSDataByPMINo(String str) {

        try {
            PDI = patient.getBiodata(str);
            EMP = patient.getEmploymentDetail(str);
            NOK = patient.getNokDetail(str);
            FMI = patient.getFamilyDetail(str);
            INS = patient.getInsuranceDetail(str);

            list.add(0, Arrays.asList(PDI));
            list.add(1, Arrays.asList(EMP));
            list.add(2, Arrays.asList(NOK));
            list.add(3, Arrays.asList(FMI));
            list.add(4, Arrays.asList(INS));


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }
    
    public List<String> getPMSDataByID(String str, String str2) {

        try {
            PDI = patient.getBiodataUsingID(str, str2);
            EMP = patient.getEmploymentDetailUsingID(str, str2);
            NOK = patient.getNokDetailUsingID(str, str2);
            FMI = patient.getFamilyDetailUsingID(str, str2);
            INS = patient.getInsuranceDetailUsingID(str, str2);

            list.add(0, Arrays.asList(PDI));
            list.add(1, Arrays.asList(EMP));
            list.add(2, Arrays.asList(NOK));
            list.add(3, Arrays.asList(FMI));
            list.add(4, Arrays.asList(INS));


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }
    
    public List<String> getPMSDataByOldIC(String str) {

        try {
            PDI = patient.getBiodataUsingOldIC(str);
            EMP = patient.getBiodataUsingOldIC(str);
            NOK = patient.getNOKInfoUsingMyKad(str);
            FMI = patient.getFamilyDetailUsingOldIC(str);
            INS = patient.getInsuranceDetailUsingOldIC(str);

            list.add(0, Arrays.asList(PDI));
            list.add(1, Arrays.asList(EMP));
            list.add(2, Arrays.asList(NOK));
            list.add(3, Arrays.asList(FMI));
            list.add(4, Arrays.asList(INS));


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }
    
    private String[] CIS;

    public List<String> getDataCIS(String pmi_no) {
        List list = new ArrayList();
        try {
            CIS = patient.getEHR(pmi_no);
           
            list.add(0, Arrays.asList(CIS));
           


        } catch (Exception ex) {
            Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

//        String txt = "MSH|^~|CIS^001|<cr>"
//                + "PDI|PMS10003^LEE WEI CHUAN^891031075331^Chinese^Male^31/10/1989^AB^Married^|<cr>"
//                + "CCN|Knows the complaints procedure (finding)^Mild^Select One^2Minutes^Select One^ serious^Active^2012-08-03"
//                + "13:12:47.17^|<cr>"
//                + "DGS|Able to perform recreational use of conversation (finding)^Mild^08/08/2012^2012-08-03 13:12:47.17^|<cr>"
//                + "IMU|Consent status for immunizations (finding)^as^02/08/2012^2012-08-03 13:12:47.17^|<cr>"
//                + "ALG|Allergic reaction to drug^10/08/2012^aaaa^2012-08-03 13:12:47.17^|<cr>"
//                + "SH|Cigar smoker^01/08/2012^heavy^2012-08-03 13:12:47.17^|<cr>";
//
//        //break words by identify ">" symbol
//        StringTokenizer tokenLine = new StringTokenizer(txt, ">");//break words by identify ">" symbol
//
//        System.out.println("CIS : " + txt);//number of line including header
//        int CCID = 0;
//        String pid = null;
//        boolean redundant = false;
//        boolean justInsert = false;
//        for (int i = tokenLine.countTokens(); i > 0; i--) {
//
//            String line = tokenLine.nextToken(); //insert each line into string
//            System.out.println(line);
//            StringTokenizer column = new StringTokenizer(line, "|");// get on off status from header word
//            String TableType = column.nextToken();//1st column is table name
//            System.out.println(TableType);
//
//
//            if (TableType.equals("PDI")) {
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//                pid = tokenData.nextToken();
//                String name = tokenData.nextToken();
//                String ic = tokenData.nextToken();
//                String race = tokenData.nextToken();
//                String sex = tokenData.nextToken();
//                String dob = tokenData.nextToken();
//                String pblood = tokenData.nextToken();
//                String pstatus = tokenData.nextToken();
//                System.out.println(pid + " " + name + " " + ic + " " + race + " " + sex + " " + dob + pblood + pstatus);
//
//                consultation.txt_pPmiNo.setText(pid);
//                consultation.txt_pName.setText(name);
//                consultation.txt_pIcNo.setText(ic);
//                consultation.txt_pRace.setText(race);
//                consultation.txt_pSex.setText(sex);
//                consultation.lblDOB.setText(dob);
//                consultation.txt_pBloodSex.setText(pblood);
//                consultation.txt_pStatus.setText(pstatus);
//            } else if (TableType.equals("CCN")) {
//                System.out.println("CCN yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String problem = tokenData.nextToken();
//                String severe = tokenData.nextToken();
//                String site = tokenData.nextToken();
//                String duration = tokenData.nextToken();
//                String laterality = tokenData.nextToken();
//                String comment = tokenData.nextToken();
//                String probStatus = tokenData.nextToken();
//
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//
//                String sql = "SELECT * FROM CHIEF_COMPLAINTS "
//                        + "WHERE ENCOUNTER_DATE=? "
//                        + "AND PMI_NO = ?";
//                PreparedStatement psRedundant = conn.prepareStatement(sql);
//                psRedundant.setTimestamp(1, timestamp);
//                psRedundant.setString(2, pid);
//                ResultSet rsRedundant = psRedundant.executeQuery();
//
//                if (rsRedundant.next()) {
//                    redundant = true;
//                    System.out.println("REDUNDANT DATA");
//                } else {
//                    redundant = false;
//                }
//
//                if (redundant == false || justInsert == true) {
//
//                    System.out.println(pid + " " + problem + " " + severe + " " + site + " " + duration + " " + laterality + " " + comment + " " + probStatus);
//                    sql = "INSERT INTO CHIEF_COMPLAINTS (PMI_NO,PROBLEM,SEVERITY,SITE,DURATION,LATERALITY,C_COMMENT,PROBLEM_STATUS,ENCOUNTER_DATE)VALUES (?,?,?,?,?,?,?,?,?)";
//                    PreparedStatement ps = null;
//                    ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
//                    ps.setString(1, pid);
//                    ps.setString(2, problem);
//                    ps.setString(3, severe);
//                    ps.setString(4, site);
//                    ps.setString(5, duration);
//                    ps.setString(6, laterality);
//                    ps.setString(7, comment);
//                    ps.setString(8, probStatus);
//                    ps.setTimestamp(9, timestamp);
//                    int status = ps.executeUpdate();
//
//                    if (status != 0) {
//                        ResultSet rs = ps.getGeneratedKeys();
//                        while (rs.next()) {
//                            CCID = rs.getInt(1);
//                        }
//                    }
//                    System.out.println("CCID" + CCID);
//                    justInsert = true;
//                }
//
//            } else if (TableType.equals("DGS")) {
//                System.out.println("DGS yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String dsgType = tokenData.nextToken();
//                String dsgsev = tokenData.nextToken();
//                String diagDate = tokenData.nextToken();
//                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
//                java.util.Date parsedDate1 = dateFormat1.parse(diagDate);
//                java.sql.Date date1 = new java.sql.Date(parsedDate1.getTime());
//
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(pid + " " + dsgType + " " + dsgsev);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO DIAGNOSIS (PMI_NO,DIAGNOSIS_TYPE,SEVERITY,DIAG_DATE,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, dsgType);
//                    ps.setString(3, dsgsev);
//                    ps.setDate(4, date1);//05/09/2005
//                    ps.setTimestamp(5, timestamp);
//                    ps.setInt(6, CCID);
//                    ps.execute();
//                }
//
//            } else if (TableType.equals("IMU")) {
//                System.out.println("IMU yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String IMUtype = tokenData.nextToken();
//                String IMUcomment = tokenData.nextToken();
//                String imuDate = tokenData.nextToken();
//                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
//                java.util.Date parsedDate1 = dateFormat1.parse(imuDate);
//                java.sql.Date imuDate1 = new java.sql.Date(parsedDate1.getTime());
//
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(pid + " " + IMUtype + " " + IMUcomment);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO IMMUNISATION (PMI_NO,IMMU_TYPE,IMMU_COMMENT,IMMU_DATE,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, IMUtype);
//                    ps.setString(3, IMUcomment);
//                    ps.setDate(4, imuDate1);
//                    ps.setTimestamp(5, timestamp);
//                    ps.setInt(6, CCID);
//                    ps.execute();
//                }
//
//            } else if (TableType.equals("VTS")) {
//                System.out.println("VTS yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                double height = Double.parseDouble(tokenData.nextToken());
//                double weight = Double.parseDouble(tokenData.nextToken());
//                double BMI = Double.parseDouble(tokenData.nextToken());
//                String weightStatus = tokenData.nextToken();
//                String headCir = tokenData.nextToken();
//                double temp = Double.parseDouble(tokenData.nextToken());
//                double pulse = Double.parseDouble(tokenData.nextToken());
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(height + " " + weight + " " + BMI + " " + weightStatus + " " + headCir + " " + temp + " " + pulse + " " + date);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO Vital_Sign (PMI_NO,HEIGHT,WEIGHT,BMI,WEIGHT_STATUS,HEAD_CIRCUM,TEMP,PULSE,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setDouble(2, height);
//                    ps.setDouble(3, weight);
//                    ps.setDouble(4, BMI);
//                    ps.setString(5, weightStatus);
//                    ps.setString(6, headCir);
//                    ps.setDouble(7, temp);
//                    ps.setDouble(8, pulse);
//                    ps.setTimestamp(9, timestamp);
//                    ps.setInt(10, CCID);
//                    ps.execute();
//                }
//
//            } else if (TableType.equals("DRU")) {
//                System.out.println("DRUG yes");
//                String dataField = column.nextToken();
//                System.out.println("DRUG yes333");
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//                System.out.println("DRUG yes4444");
//                String drug = tokenData.nextToken();
//                String product = tokenData.nextToken();
//                String dose = tokenData.nextToken();
//                String quantity = tokenData.nextToken();
//                String form = tokenData.nextToken();
//                String duration = tokenData.nextToken();
//                String freq = (tokenData.nextToken()).toString();
//                String instuct = (tokenData.nextToken()).toString();
//                System.out.println(form + "  DRUG yes7777");
//                String date = tokenData.nextToken();
//                System.out.println("DRUG yes2");
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println("DRUG yes3333");
//                    System.out.println(pid + " " + drug + product + " " + dose + " " + quantity + " " + form + " " + duration + " " + freq + " " + instuct);
//
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO DRUG_ADDICT (PMI_NO,DRUG_NAME,PRODUCT_NAME,DOSE,QUANTITY,FORM,DURATION,FREQUENCY,INSTRUCTION,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, drug);
//                    ps.setString(3, product);
//                    ps.setString(4, dose);
//                    ps.setString(5, quantity);
//                    ps.setString(6, form);
//                    ps.setString(7, duration);
//                    ps.setString(8, freq);
//                    ps.setString(9, instuct);
//                    ps.setTimestamp(10, timestamp);
//                    ps.setInt(11, CCID);
//                    ps.execute();
//                    System.out.println("DRUG yes55");
//                }
//
//            } else if (TableType.equals("DIS")) {
//                System.out.println("DIS yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String DISname = tokenData.nextToken();
//                String DISdate = tokenData.nextToken();
//                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
//                java.util.Date parsedDate1 = dateFormat1.parse(DISdate);
//                java.sql.Date disDate = new java.sql.Date(parsedDate1.getTime());
//
//                String DIScom = tokenData.nextToken();
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(pid + " " + DISname + " " + DISdate + " " + DIScom);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO DISABILITY (PMI_NO,DIS_NAME,DIS_FROMDATE,DIS_COM,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, DISname);
//                    ps.setDate(3, disDate);
//                    ps.setString(4, DIScom);
//                    ps.setTimestamp(5, timestamp);
//                    ps.setInt(6, CCID);
//                    ps.execute();
//                }
//
//            } else if (TableType.equals("ALG")) {
//                System.out.println("ALG yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String ALGname = tokenData.nextToken();
//                String ALGdate = tokenData.nextToken();
//                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
//                java.util.Date parsedDate1 = dateFormat1.parse(ALGdate);
//                java.sql.Date algDate = new java.sql.Date(parsedDate1.getTime());
//
//                String ALGcom = tokenData.nextToken();
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(pid + " " + ALGname + " " + ALGdate + " " + ALGcom);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO ALLERGY (PMI_NO,ALL_NAME,ALL_FROMDATE,ALL_COM,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, ALGname);
//                    ps.setDate(3, algDate);
//                    ps.setString(4, ALGcom);
//                    ps.setTimestamp(5, timestamp);
//                    ps.setInt(6, CCID);
//                    ps.execute();
//                }
//
//            } else if (TableType.equals("SH")) {
//                System.out.println("SH yes");
//                String dataField = column.nextToken();
//                StringTokenizer tokenData = new StringTokenizer(dataField, "^");
//
//                String SHname = tokenData.nextToken();
//                String SHdate = tokenData.nextToken();
//                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
//                java.util.Date parsedDate1 = dateFormat1.parse(SHdate);
//                java.sql.Date shDate = new java.sql.Date(parsedDate1.getTime());
//
//                String SHcom = tokenData.nextToken();
//                String date = tokenData.nextToken();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//                java.util.Date parsedDate = dateFormat.parse(date);
//                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//
//                if (redundant == false || justInsert == true) {
//                    System.out.println(pid + " " + SHname + " " + SHdate + " " + SHcom);
//                    PreparedStatement ps = conn.prepareStatement("INSERT INTO SOCIAL_HISTORY (PMI_NO,SH_NAME,SH_FROMDATE,SH_COM,ENCOUNTER_DATE,CC_ID)VALUES (?,?,?,?,?,?)");
//                    ps.setString(1, pid);
//                    ps.setString(2, SHname);
//                    ps.setDate(3, shDate);
//                    ps.setString(4, SHcom);
//                    ps.setTimestamp(5, timestamp);
//                    ps.setInt(6, CCID);
//                    ps.execute();
//                }
//
//            }
//
//        }

       
    }

    public void PMIsaperator() {

        String[] seperatedMSGsegment = fullmsg.split("<cr>");

        System.out.println("****** Start Split ff *******");

        for (int i = 0; i < seperatedMSGsegment.length; i++) {
            System.out.println(seperatedMSGsegment[i]);

            String str = seperatedMSGsegment[i].trim();

            if (str.indexOf("PDI") == 0) {

                System.out.println("PDI message");

                PDI = str.replaceAll("PDI\\|", "").split("\\|");

                System.out.println("...... Insert message PDI..... ");
                try {

                    patient.addPatientBiodata(PDI);

                }  catch (Exception ex) {
                    Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("......End Insert message PDI..... ");

            }

            if (str.indexOf("EMP") == 0) {

                System.out.println("emp message");
                EMP = str.replaceAll("EMP\\|", "").split("\\|");

                System.out.println("...... Insert message EMP..... ");
                try {

                    patient.addPatientEmploymentInfo(EMP);

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    //Logger.getLogger(SynchConsumer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Logger.getLogger(SynchConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("......End Insert message EMP..... ");


            } else if (str.indexOf("NOK") == 0) {

                System.out.println("nok message");
                NOK = str.replaceAll("NOK\\|", "").split("\\|");

                System.out.println("...... Insert message NOK..... ");
                try {

                    patient.addPatientNextOfKinInfo(NOK);

                } catch (Exception ex) {
                    Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("......End Insert message NOK..... ");

            } else if (str.indexOf("FMI") == 0) {

                System.out.println("fmi message");
                FMI = str.replaceAll("FMI\\|", "").split("\\|");
                System.out.println("...... Insert message FMI..... ");
                try {

                    patient.addPatientFamilyInfo(FMI);

                } catch (Exception ex) {
                    Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("......End Insert message FMI..... ");

            } else if (str.indexOf("INS") == 0) {

                System.out.println("ins message");
                INS = str.replaceAll("INS\\|", "").split("\\|");

                System.out.println("...... Insert message INS..... ");
                try {

                    patient.addPatientInsuranceInfo(INS);

                } catch (Exception ex) {
                    Logger.getLogger(PMImsg.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("......End Insert message INS..... ");

            }

        }

        System.out.println("****** End Split *******");

    }

    public static void main(String[] args) {
    }

    public void CISSeparator() {

        String[] seperatedMSGsegment = fullmsg.split("<cr>");

        System.out.println("****** Start Split *******");


        for (int i = 0; i < seperatedMSGsegment.length; i++) {
            System.out.println(seperatedMSGsegment[i]);

            String str = seperatedMSGsegment[i].trim();

            if (str.indexOf("PDI") == 0) {

                System.out.println("PDI message");
                // MSH|^|CIS^T12108|KAJANG|CIS|HUTeM|03/08/2012 11:24:15|ST|03082012112415<cr>
                PDI = str.replaceAll("PDI\\|", "").split("\\|");
                //test();

//                try {
//
//                    patient.addPatientBiodata(PDI);
//
//                } catch (ClassNotFoundException ex) {
//                    Logger.getLogger(SynchConsumer.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (SQLException ex) {
//                    Logger.getLogger(SynchConsumer.class.getName()).log(Level.SEVERE, null, ex);
//                }
                //System.out.println("......End Insert message PDI..... ");

            }
        }

    }
    //Get String[] PDI

    public String[] getPDIMessage() {
        return this.PDI;
    }

    public void test() {

        for (int i = 0; i < PDI.length; i++) {
            System.out.println("#####  Test Display ######");
            System.out.println(PDI[i]);
            System.out.println("#####  End Test #######");
        }
    }
}
