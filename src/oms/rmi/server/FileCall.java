/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class FileCall {
    private static String ipserver;
    private static int port;
    private static int status;
    
    public static String[] getConfig() {
        
        int num_config = 3;
        String config[] = new String[num_config];
        
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader("ipcall"));

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                String pecah[] = sCurrentLine.split(":");
                if (pecah[0].equals("ipserver")) {
                    config[0] = pecah[1];
                }
                if (pecah[0].equals("port")) {
                    config[1] = pecah[1];
                }
                if (pecah[0].equals("status")) {
                    config[2] = pecah[1];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return config;
    }

    public static String getIpserver() {
        ipserver = getConfig()[0];
        return ipserver;
    }

    public static int getPort() {
        port = Integer.parseInt(getConfig()[1]);
        return port;
    }

    public static int getStatus() {
        status = Integer.parseInt(getConfig()[2]);
        return status;
    }
}
