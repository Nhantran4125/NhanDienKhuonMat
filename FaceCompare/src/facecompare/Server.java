package facecompare;

import DAO.PersonDAO;
import DAO.PhotoDAO;
import DTO.Person;
import DTO.Photo;
import DTO.Request;
import DTO.Response;
import encryption.AES;
import encryption.RSA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Server implements Runnable {

    private Socket socket = null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream;
    private SecretKey key = null;
    private final int counter;

    public Server(Socket socket, int counter) {
        this.socket = socket;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client  " + counter + " : " + socket.getInetAddress() + "is connected ");
            if (socket.isConnected()) {
                if (key == null) {
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    byte[] arrayKey = DescryptKey((byte[]) inputStream.readObject());
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
                    if (type == 2) {
                        File file = request.getFile();
                        Person ps = request.getPerson();
                        addPhoto(file, ps);
                    }
                    if (type == 0) {
                        File file = request.getFile();
                        ObjDetection(file);
                    }
                }
            }
            if (!socket.isConnected()) {
                key = null;
            }
        } catch (Exception ex) {
            key = null;
            System.out.println("Client : " + socket.getInetAddress() + " is disconnected " + ex);
        }

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
                if (fileCompare.exists()) {
                    confidence = fc.compareFace(file, fileCompare); // so khớp 2 hình -> kết quả giống nhau
                    System.out.println(confidence);
                    if (confidence > max) // tìm được hình có độ giống nhau lớn hơn max  -> gán matchPhoto là p
                    {
                        max = confidence;
                        matchPhoto = photo;
                    }
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Thoi gian cho doi: " + duration);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Response response = null;
            if (max > 70) // max tren 70% thì trả về kết quả
            {
                Person personMatch = new Person();
                for (Person person : listPerson) // tìm Person theo ID
                {
                    if (matchPhoto.getId_person() == person.getId()) {
                        personMatch = person;
                    }
                }
                response = new Response(personMatch, matchPhoto, String.valueOf(max));
            } else {
                response = new Response("Không tìm thấy người này");
            }
            outputStream.writeObject(this.EncryptData(getBinary(response)));
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ObjDetection(File file) {
        ObjectDetection objDetection = new ObjectDetection(file);
        ArrayList<String> obj = new ArrayList<>();
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            obj = objDetection.ObjDetection(file);
            Response response = new Response(obj);
            outputStream.writeObject(this.EncryptData(getBinary(response)));
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

            ArrayList<Photo> listPhoto = new ArrayList<>();
            listPhoto = PhotoDAO.loadPhoto();

            //data send to Client
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            File directory = new File(file.getPath());
            File dic = new File(directory.getName());
            String path = "src/photo/" + dic.getPath();

            int count = 0;
            int qty = 0;
            for (Person x : listPerson) {
                if (x.getHoten().equalsIgnoreCase(ps.getHoten()) && x.getNamsinh() == ps.getNamsinh()) {
                    for (Photo t : listPhoto) {
                        if (x.getId() == t.getId_person()) {
                            qty++;
                        }
                    }
                    if (qty >= 5) {
                        System.out.println("Chỉ đc thêm tối đa 5 ảnh cho 1 đối tượng");
                        Response response = new Response("Chỉ đc thêm tối đa 5 ảnh cho 1 đối tượng");
                        outputStream.writeObject(this.EncryptData(getBinary(response)));
                        System.out.println(response);
                        //break;
                    } else { //them anh
                        FaceCompare fc = new FaceCompare();
                        // neu thong tin nguoi da ton tai -> them hinh
                        boolean flag = false;
                        List<Photo> checkArray = listPhoto.stream().filter(item -> item.getId_person() == x.getId()).toList();
                        for (Photo photo : checkArray) {
                            flag = false;
                            File fileCompare = new File(photo.getPath()).getAbsoluteFile();
                            double confidence = 0;
                            if (fileCompare.exists()) {
                                confidence = fc.compareFace(file, fileCompare); // so khớp 2 hình -> kết quả giống nhau
                                if (confidence > 95) {
                                    Response response = new Response("Hình này đã có trong cơ sở dữ liệu");
                                    outputStream.writeObject(this.EncryptData(getBinary(response)));
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag == false) {
                            Photo pt1 = new Photo();
                            pt1.setId_person(x.getId());
                            pt1.setPath(path);
                            PhotoDAO pt = new PhotoDAO();
                            pt.add(pt1);
                            Files.copy(Paths.get(file.getPath()), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);

                            Response response = new Response("Thêm hình thành công");
                            outputStream.writeObject(this.EncryptData(getBinary(response)));
                            System.out.println(response);
                        }

                    }

                } else {
                    count++;
                }
            }

            // neu thong tin nguoi chua ton tai -> them nguoi, them anh
            if (count == listPerson.size()) {
                //them nguoi
                PersonDAO psAdd = new PersonDAO();
                psAdd.add(ps);

                //them anh cua nguoi do
                listPerson = PersonDAO.loadPerson();
                for (Person x : listPerson) {
                    if (x.getHoten().equalsIgnoreCase(ps.getHoten()) && x.getNamsinh() == ps.getNamsinh()) {
                        // neu thong tin nguoi da ton tai -> them hinh
                        Photo pt1 = new Photo();
                        pt1.setId_person(x.getId());
                        pt1.setPath(path);

                        PhotoDAO pt = new PhotoDAO();
                        pt.add(pt1);
                        Files.copy(Paths.get(file.getPath()), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);

                        Response response = new Response("Thêm hình thành công");
                        outputStream.writeObject(this.EncryptData(getBinary(response)));
                        System.out.println(response);
                        break;
                    }
                }
            }
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
