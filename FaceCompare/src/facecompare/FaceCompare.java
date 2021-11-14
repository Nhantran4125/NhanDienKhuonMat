/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facecompare;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.net.ssl.SSLException;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class FaceCompare {

   //public static void main(String[] args) throws Exception {
    public FaceCompare()
    {
        
    }
    public double compareFace(File file1, File file2)
    {

//        File file = new File("YOUR IMAGE PATH");
//		byte[] buff = getBytesFromFile(file);
//		String url = "https://api-us.faceplusplus.com/facepp/v3/detect";
//        HashMap<String, String> map = new HashMap<>();
//        HashMap<String, byte[]> byteMap = new HashMap<>();
//        map.put("api_key", "YOUR API_KEY");
//        map.put("api_secret", "YOUR API_SECRET");
//        byteMap.put("image_file", buff);
//        try{
//            byte[] bacd = post(url, map, byteMap);
//            String str = new String(bacd);
//            System.out.println(str);
//        }catch (Exception e) {
//        	e.printStackTrace();
//		}
        // Create a new file object for the first file and get bytes from file
       
//        //File file1 = new File("E:\\University\\NAM4_HK1\\LTM\\Test\\pic\\leo1.jpg");                     
//        File file1 = new File("src/photo/leo1.jpg").getAbsoluteFile();
//        byte[] buff1 = getBytesFromFile(file1);
//
//        // Create a new file object for the second file and get bytes from file
//        File file2 = new File("src/photo/leo3.jpg").getAbsoluteFile();
//        byte[] buff2 = getBytesFromFile(file2);
        
        //File file1 = new File("E:\\University\\NAM4_HK1\\LTM\\Test\\pic\\leo1.jpg");                     
        //File file1 = new File("src/photo/leo1.jpg").getAbsoluteFile();
        byte[] buff1 = getBytesFromFile(file2);

        // Create a new file object for the second file and get bytes from file
        //File file2 = new File("src/photo/leo3.jpg").getAbsoluteFile();
        byte[] buff2 = getBytesFromFile(file1);

        // Data needed to use the Face++ Compare API
        String url = "https://api-us.faceplusplus.com/facepp/v3/compare";
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "dam4ZdTkSsZOUAiR4oQpP3DRnjEz1fcD");
        map.put("api_secret", "0MOCfpum1Lec06EMOzuJPOEa_EhM4Ttg");

        byteMap.put("image_file1", buff1);
        byteMap.put("image_file2", buff2);
        //byteMap.put("image_file3", buff3);
        

        try {
            // Connecting and retrieving the JSON results
            byte[] bacd = post(url, map, byteMap);
            String jsonStr = new String(bacd);

             
            JSONObject obj = new JSONObject(jsonStr);
            double confidence = obj.getDouble("confidence");
            //System.out.println("Confidence: "+confidence);
            return confidence;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();

    protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if (fileMap != null && fileMap.size() > 0) {
            Iterator fileIter = fileMap.entrySet().iterator();
            while (fileIter.hasNext()) {
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try {
            if (code == 200) {
                ins = conne.getInputStream();
            } else {
                ins = conne.getErrorStream();
            }
        } catch (SSLException e) {
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while ((len = ins.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }

    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }

    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
    
}
