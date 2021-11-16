/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facecompare;

import DAO.MySQLConnect;
import DAO.PersonDAO;
import DAO.PhotoDAO;
import DTO.Person;
import DTO.Photo;
import DTO.Request;
import DTO.Response;
import encryption.AES;
import encryption.RSA;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class Server {

    private final int SERVER_PORT = 5000;
    private Socket socket = null;
    private ServerSocket server = null;
    private MySQLConnect conn = null;
    BufferedWriter out = null;
    BufferedReader in = null;
    DataInputStream input;
    DataOutputStream output = null;
    ObjectInputStream inputStream = null;
    public String line = "";
    ObjectOutput outputStream;

    public Server(int port) {
        try {
            server = new ServerSocket(SERVER_PORT);
            while (true) {
                try {
                    System.out.println("Server start");
                    socket = server.accept();
                    System.out.println("Connected");
                    Request request = null;
                    while (true) {
                        inputStream = new ObjectInputStream(socket.getInputStream());
                        request = (Request) inputStream.readObject();
                        if (request.getType() == 1) {
                            ComparePhoto(request.getData(), request.getKey());
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Client : " + socket.getInetAddress() + " is disconnected " + ex);
                }
            }
        } catch (IOException ex) {
            System.err.println("Không thể khởi tạo serversocket : " + ex);
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            socket.close();
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        Server server1 = new Server(5000);
    }

    private void ComparePhoto(byte[] data, byte[] key) {
        try {
            byte[] originalKey = DescryptKey(key);
            byte[] originalData = DescryptData(data, originalKey);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalData));
            File outputfile = new File("src/photo/temp.jpg");
            ImageIO.write(image, "jpg", outputfile);
            //MẢNG PERSON - chứa tất cả các person 
            ArrayList<Person> listPerson = new ArrayList<Person>();
            listPerson = PersonDAO.loadPerson();

            //MẢNG PHOTO - chứa tất cả các hình lưu từ CSDL  
            ArrayList<Photo> listPhoto = new ArrayList<Photo>();
            listPhoto = PhotoDAO.loadPhoto();
            //hình client gửi để so khớp
            //File input = new File("src/photo/test.jpg").getAbsoluteFile();

            double max = 0; // Kết quả khớp nhất
            double confidence = 0; // kết quả so sánh 2 ảnh
            Photo matchPhoto = new Photo(); // Hình ảnh giống nhất

            FaceCompare fc = new FaceCompare();
            long startTime = System.currentTimeMillis();

            //lần lượt so sánh hình client gửi (input) với từng hình trong CSDL (fileCompare)
            for (Photo photo : listPhoto) {
                File fileCompare = new File(photo.getPath()).getAbsoluteFile();
                confidence = fc.compareFace(outputfile, fileCompare); // so khớp 2 hình -> kết quả giống nhau
                if (confidence > max) // tìm được hình có độ giống nhau lớn hơn max  -> gán matchPhoto là p
                {
                    max = confidence;
                    matchPhoto = photo;
                }
            }
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("Thoi gian cho doi: " + duration);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            if (max > 70) // max tren 70% thì trả về kết quả
            {
                Person personMatch = new Person();
                for (Person person : listPerson) // tìm Person theo ID
                {
                    if (matchPhoto.getId_person() == person.getId()) {
                        personMatch = person;
                    }
                }
                SecretKey secretKey = new SecretKeySpec(originalKey, 0, originalKey.length, "AES");
                Response response = new Response();
                response.setData(EncryptData(getBinary(personMatch), secretKey));
                response.setPhoto(EncryptData(getBinary(matchPhoto), secretKey));
                response.setMessage(String.valueOf(max));
                outputStream.writeObject(response);
            } else {
                Response response = new Response();
                response.setMessage("Không tìm thấy người này");
                outputStream.writeObject(response);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ObjectDetection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String url = "src/encryption/";

    public byte[] DescryptKey(byte[] key) {
        try {
            PrivateKey privateKey = RSA.getPrivateKey(url + "PrivateKey.txt");
            return RSA.decrypt(privateKey, key);
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] DescryptData(byte[] data, byte[] key) {
        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
        return AES.decrypt(originalKey, data);
    }

    private static Object getObject(byte[] byteArr) {
        ByteArrayInputStream in = new ByteArrayInputStream(byteArr);
        ObjectInputStream is = null;
        Object obj = null;
        try {
            is = new ObjectInputStream(in);
            obj = is.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return obj;
    }

    private static byte[] getBinary(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] array = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            array = bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                bos.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return array;
    }

    public byte[] EncryptData(byte[] data, SecretKey secretKey) {
        try {
            return AES.encrypt(secretKey, data);
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
