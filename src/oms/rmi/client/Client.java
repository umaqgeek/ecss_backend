/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import oms.rmi.server.Message;

public class Client {

    private void doTest() {
        try {
            // fire to localhost port 1099
            Registry myRegistry = LocateRegistry.getRegistry("127.0.0.1", 1099);

            // search for myMessage service
            Message impl = (Message) myRegistry.lookup("myMessage");

            // call server's method	
            String pmi = "PMS10003";
            String IC = "891031075331";
            impl.sayHello("..Friza ");
            List s = impl.getPMS(IC); //access mysql
            



           // List<String> pdilist = (List) s.get(0);

//            ArrayList listArray = new ArrayList();
//
//            listArray.add("Germany");
//            listArray.add("Holland");
//            listArray.add("Sweden");
//
//            String[] strArray = new String[3];
//            listArray.toArray(strArray);

            //String[] strArray = new String[pdilist.size()];
            //pdilist.toArray(strArray);
            //System.out.println("..hehehe..:" + strArray);


//            
//             for (int i=0;i<s.size();i++){
//                System.out.println(s.get(i));
//            }

            System.out.println("Message Sent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client main = new Client();
        main.doTest();
    }
}
