/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class Test {
    
    public static void main(String[] args){
         String pmi= "REC10001";
         int num = Integer.parseInt(pmi.substring(3, pmi.length()));
         System.err.println(num);
           String formatted = String.format("%05d", num);
             System.err.println(formatted);
    }
    
}
