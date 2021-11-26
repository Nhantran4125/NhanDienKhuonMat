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
import GUI.ClientGui;
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
import java.io.InputStream;
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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    private SecretKey key = null;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                try {
                    System.out.println("Server start");
                    socket = server.accept();
                    System.out.println("Client : " + socket.getInetAddress() + " connected ");

                    if (key == null) {
                        inputStream = new ObjectInputStream(socket.getInputStream());
                        byte[] arrayKey = DescryptKey((byte[]) inputStream.readObject());
                        System.out.println(arrayKey.length);
                        key = new SecretKeySpec(arrayKey, 0, arrayKey.length, "AES");

                    }
                    while (true) {
                        inputStream = new ObjectInputStream(socket.getInputStream());
                        byte[] cypher = (byte[]) inputStream.readObject();
                        Request request = (Request) getObject(this.DescryptData(cypher));
                        int type = request.getType();
                        if (type == 1) {
                            File file = request.getFile();
                            ComparePhoto(file);
                        }
                        
                        
                        if(type==2)
                        {
                            File file = request.getFile();
                            Person ps= request.getPerson();
                            addPhoto(file, ps);
                        }
                        
                    }

                } catch (Exception ex) {
                    key = null;
                    System.out.println("Client : " + socket.getInetAddress() + " is disconnected " + ex);
                }

            }
        } catch (IOException ex) {
            System.err.println("Không thể khởi tạo serversocket : " + ex);
        }

    }

    public static void main(String[] args) {
        Server server1 = new Server(5000);
    }

    private void ComparePhoto(File file) {
        try {
            //MẢNG PERSON - chứa tất cả các person 
            ArrayList<Person> listPerson = new ArrayList<>();
            listPerson = PersonDAO.loadPerson();

            //MẢNG PHOTO - chứa tất cả các hình lưu từ CSDL  
            ArrayList<Photo> listPhoto = new ArrayList<>();
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
                confidence = fc.compareFace(file, fileCompare); // so khớp 2 hình -> kết quả giống nhau
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

                Response response = new Response();
                response.setData(EncryptData(getBinary(personMatch)));
                response.setPhoto(EncryptData(getBinary(matchPhoto)));
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

    String url = "src/encryption/";

    private byte[] DescryptKey(byte[] key) {
        PrivateKey privateKey = null;
        try {
            privateKey = RSA.getPrivateKey(url + "PrivateKey.txt");

        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RSA.decrypt(privateKey, key);
    }

    public final byte[] DescryptData(byte[] data) {

        return AES.decrypt(key, data);
    }

//    private static Object getObject(byte[] byteArr) {
//        ByteArrayInputStream in = new ByteArrayInputStream(byteArr);
//        ObjectInputStream is = null;
//        Object obj = null;
//        try {
//            is = new ObjectInputStream(in);
//            obj = is.readObject();
//
//        } catch (IOException | ClassNotFoundException ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                in.close();
//            } catch (IOException ex) {
//                System.out.println(ex);
//            }
//        }
//        return obj;
//    }
    private static byte[] getBinary(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
       return null;
    }

    //chuyển object từ byte[]
    private static Object getObject(byte[] byteArr) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
                ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] EncryptData(byte[] data) {
        try {
            return AES.encrypt(key, data);
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void addPhoto(File file, Person ps) {
      try {

            ArrayList<Person> listPerson = new ArrayList<>();
            listPerson = PersonDAO.loadPerson();
            
            //data send to Client
            outputStream = new ObjectOutputStream(socket.getOutputStream());
           
           //File directory = new File(file.getPath()).getAbsoluteFile();
           // hiện tại là ảnh cần thêm phải có sẵn trong folder photo thì sau  khi thêm ảnh mới so sánh được
           File directory = new File(file.getPath());
           File dic= new File(directory.getName());
           String path= "src/photo/" + dic.getPath();
            //String path = directory.getAbsolutePath();
            
            int count=0;
            for(Person x : listPerson){
                if(x.getHoten().equals(ps.getHoten()) && x.getNamsinh() == ps.getNamsinh() )
                {
                    // neu thong tin nguoi da ton tai -> them hinh
                    Photo pt1=new Photo();
                    pt1.setId_person(x.getId());
                    pt1.setPath(path);
                    
                    PhotoDAO pt= new PhotoDAO();
                    pt.add(pt1);
                    
                    Response response = new Response();
                    response.setMessage("Thêm hình thành công");
                    outputStream.writeObject(response);
                    System.out.println(response);
                    
                }
                else{
                    count++;
                }
                }
            
            // neu thong tin nguoi chua ton tai -> them nguoi, them anh
            if(count==listPerson.size()){
                    //them nguoi
                    PersonDAO psAdd= new PersonDAO();
                    psAdd.add(ps);
                    
                    
                    //them anh cua nguoi do
                 
                   listPerson = PersonDAO.loadPerson();
                    for(Person x : listPerson){
                        if(x.getHoten().equals(ps.getHoten()) && x.getNamsinh() == ps.getNamsinh() )
                        {
                            // neu thong tin nguoi da ton tai -> them hinh
                            Photo pt1=new Photo();
                            pt1.setId_person(x.getId());
                            pt1.setPath(path);

                            PhotoDAO pt= new PhotoDAO();
                            pt.add(pt1);

                            Response response = new Response();
                            response.setMessage("Thêm hình thành công");
                            outputStream.writeObject(response);
                            System.out.println(response);
                            break;

                        }
                    }
                   
               
                    Response response = new Response();
                    response.setMessage("Thêm người + hình ảnh thành công");
                    outputStream.writeObject(response);
                    System.out.println(response);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            }
    
}
