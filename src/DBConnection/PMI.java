/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DBConnection;

/**
 *
 * @author End User
 */
public class PMI {
//property

    public int quotientA = 0;
    public int remainderA = 0;
    public int quotientB = 0;
    public int remainderB = 0;
    public String sequence = null;
    public String sequence2 = null;
    public int i = 1;
    public String weight = "1,7,3,1,7,3,1,7,3,1,7,3"; //predetermined number
    public int checkdigit = 0;
    
    public void setI() {
        this.i = DBConnection.getNewPmi();
    }

//get total number of digit in the sequence number
    public void get_sequencenumber() {
        //sequence number 12 digit
        int length = String.valueOf(i).length();
        //generate last sequence in digits
        String giliran = String.valueOf(i);
        if (length == 2) {
            sequence = "0000000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1);
        } else if (length == 1) {
            sequence = "00000000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0,0,0,0,0,0," + giliran.substring(0);
        } else if (length == 3) {
            sequence = "000000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2);
        } else if (length == 4) {
            sequence = "00000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3);
        } else if (length == 5) {
            sequence = "0000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4);
        } else if (length == 6) {
            sequence = "000000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5);
        } else if (length == 7) {
            sequence = "00000" + String.valueOf(i);
            sequence2 = "0,0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6);
        } else if (length == 8) {
            sequence = "0000" + String.valueOf(i);
            sequence2 = "0,0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6, 7) + "," + giliran.substring(7);
        } else if (length == 9) {
            sequence = "000" + String.valueOf(i);
            sequence2 = "0,0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6, 7) + "," + giliran.substring(7, 8) + "," + giliran.substring(8);
        } else if (length == 10) {
            sequence = "00" + String.valueOf(i);
            sequence2 = "0,0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6, 7) + "," + giliran.substring(8, 9) + "," + giliran.substring(9);
        } else if (length == 11) {
            sequence = "0" + String.valueOf(i);
            sequence2 = "0," + giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6, 7) + "," + giliran.substring(8, 9) + "," + giliran.substring(9, 10) + "," + giliran.substring(10);
        } else if (length == 12) {
            sequence = String.valueOf(i);
            sequence2 = giliran.substring(0, 1) + "," + giliran.substring(1, 2) + "," + giliran.substring(2, 3) + "," + giliran.substring(3, 4) + "," + giliran.substring(4, 5) + "," + giliran.substring(5, 6) + "," + giliran.substring(6, 7) + "," + giliran.substring(8, 9) + "," + giliran.substring(9, 10) + "," + giliran.substring(10, 11) + "," + giliran.substring(11, 12) + "," + giliran.substring(12);
        }
    }

    public int update_sequence() {
        return this.i++;
    }

    public void genPMI() {
        //variable
        int totalA = 0;
        int totalB = 0;
        int totalC = 0;
        int totalD = 0;
        //split weight into array
        String[] susunan = weight.split(",");
        //split sequence number into array
        String[] susunansequence = sequence2.split(",");
        //total sum (each digi in sequence number + weight)
        for (int a = 0; a < 12; a++) {
            totalA = totalA + (Integer.parseInt(susunan[a]) * Integer.parseInt(susunansequence[a]));
        }
        //quotient A
        quotientA = totalA / 10;
        //remainder A
        remainderA = totalA % 10;
        if (remainderA == 0) {
            sequence = sequence + remainderA;
        } else if (remainderA != 0) {
            //total B
            totalB = totalA + 10;
            //quotient B
            quotientB = totalB / 10;
            //remainder B
            remainderB = totalB % 10;
            //total C
            totalC = totalB - remainderB;
            //Total D
            totalD = totalC - totalA;
            //concate sequence number with totalD
            sequence = sequence + Integer.toString(totalD);
            //new check digit value
            this.checkdigit = totalD;
        }
        System.out.println(sequence);
    }

//    public String mergeIC(String Nric) {
//        //split IC number into array
//        String[] splitNric = Nric.split("-");
//        //concate splitNric + check digit
//        String PMI = splitNric[0] + splitNric[1] + splitNric[2] + Integer.toString(this.checkdigit);
//        return PMI;
//    }
    
    public String mergeIC(String Nric) {
        return Nric + Integer.toString(this.checkdigit);
    }
    
    public String getPMI(String ic) {
        setI();
        get_sequencenumber();
        genPMI();
        update_sequence();
        return mergeIC(ic);
    }

//testing purpose
//    public static void main(String[] args) {
//        // TODO code application logic here
//        PMI main = new PMI();
//        for (int n = 0; n < 20; n++) {
//            System.out.println(main.getPMI("880526015332"));
//        }
//
//    }
}