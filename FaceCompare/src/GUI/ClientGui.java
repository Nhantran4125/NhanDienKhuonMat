package GUI;

import DTO.Person;
import DTO.Photo;
import DTO.Request;
import DTO.Response;
import encryption.AES;
import encryption.RSA;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Lenovo
 */
public class ClientGui extends JFrame {

    //============== PERSON
    public JPanel panel1, pnmenu, pnright1, pnright2, pnResult, pnInfo, pnAdd;
    public JLabel label1, lbPerson, lbObject, lbCheck, lbAdd;
    public JLabel lbTitlePic, lbInfo, lbPic, lbPicFromServer, lbPercent;
    public JLabel lbName, lbYOB, lbNameAdd, lbYOBAdd;
    public JTextField txtName, txtYOB, txtNameAdd, txtYOBAdd;
    public JButton btn1, btnAdd, btnLoad, btnSend, btnSend2;
    public JFrame f;
    Color color_background = new Color(31, 73, 91);
    //================ OBJECT
    public JLabel lbPicOj, lbServer, lbPicFromServerOj, lbPercentOj, lbNameOj, lbOj;
    public JButton btnAddOj, btnLoadOj, btnSendOj;
    public JPanel pnResultOj, pnInfoOj;
    public JTextField txtNameOj;
    public JTextPane txpNamOj;
    public File clientFileInput;
    //================ Client
    Socket socket;
    BufferedWriter out = null;
    BufferedReader in = null;

    SecretKey key = AES.generateKey();
    //================ Public Key
    PublicKey publicKey;
    ObjectInputStream inputStream = null; //Objekt vom Client
    ObjectOutputStream outputStream = null;
    //========================
    int type;
    Response response = null;
    String url = "src/encryption/";

    public ClientGui() {
        // tạo thể hiện của JFrame
        f = new JFrame();
        f.setTitle("IMAGE RECOGNITION");
//        f.setSize(1800,800);
        f.setSize(1700, 800);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.setLayout(null);
//        f.setVisible(true);

    }

    @Override
    public void show() {
        //left menu
        pnmenu = new JPanel();
        pnmenu.setBounds(0, 0, 200, 800);
        pnmenu.setBackground(color_background);
        pnmenu.setLayout(null);

        //person
        pnright1 = new JPanel();
        pnright1.setBounds(200, 0, 1400, 800);
        pnright1.setLayout(null);
        pnright1.setVisible(false);
        f.add(pnright1);

        //Object
        pnright2 = new JPanel();
        pnright2.setBounds(200, 0, 1400, 800);
        pnright2.setLayout(null);
        pnright2.setVisible(false);
        f.add(pnright2);

        lbPerson = new JLabel("PERSON", JLabel.CENTER);
        lbPerson.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbPerson.setBounds(0, 0, 200, 100);
        lbPerson.setForeground(Color.WHITE);
        lbPerson.setBackground(color_background);
        //lbPerson.setBackground(Color.yellow);
        lbPerson.setOpaque(true);
        pnmenu.add(lbPerson);
        lbPerson.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                lbObject.setForeground(Color.WHITE);
                lbObject.setBackground(color_background);
                lbPerson.setForeground(color_background);
                lbPerson.setBackground(Color.yellow);
                pnright1.setVisible(true);
                pnright2.setVisible(false);
            }

            @Override
            public void mousePressed(MouseEvent me) {

            }

            @Override
            public void mouseReleased(MouseEvent me) {

            }

            @Override
            public void mouseEntered(MouseEvent me) {

            }

            @Override
            public void mouseExited(MouseEvent me) {

            }

        });

        lbObject = new JLabel("OBJECT", JLabel.CENTER);
        lbObject.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbObject.setBounds(0, 100, 200, 100);
        lbObject.setForeground(Color.WHITE);
        lbObject.setBackground(color_background);
        lbObject.setOpaque(true);
        pnmenu.add(lbObject);

        lbObject.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                lbPerson.setForeground(Color.WHITE);
                lbPerson.setBackground(color_background);
                lbObject.setForeground(color_background);
                lbObject.setBackground(Color.yellow);
                pnright2.setVisible(true);
                pnright1.setVisible(false);
            }

            @Override
            public void mousePressed(MouseEvent me) {

            }

            @Override
            public void mouseReleased(MouseEvent me) {

            }

            @Override
            public void mouseEntered(MouseEvent me) {

            }

            @Override
            public void mouseExited(MouseEvent me) {

            }
        });

        //main PERSON------------------------------------------------------------------
        lbCheck = new JLabel("Check", JLabel.CENTER);
        lbCheck.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbCheck.setForeground(Color.WHITE);
        lbCheck.setBackground(color_background);
        lbCheck.setBounds(50, 10, 200, 70);
        //lbCheck.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lbCheck.setOpaque(true);
        pnright1.add(lbCheck);
        lbCheck.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                btnSend.setVisible(true);
                pnResult.setVisible(true);
                lbTitlePic.setVisible(true);
                lbCheck.setForeground(Color.WHITE);
                lbCheck.setBackground(color_background);

                lbAdd.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                lbAdd.setForeground(null);
                lbAdd.setBackground(null);

//                txtNameAdd.setVisible(false);
//                txtYOBAdd.setVisible(false);
                pnAdd.setVisible(false);

            }

            @Override
            public void mousePressed(MouseEvent me) {

            }

            @Override
            public void mouseReleased(MouseEvent me) {

            }

            @Override
            public void mouseEntered(MouseEvent me) {

            }

            @Override
            public void mouseExited(MouseEvent me) {

            }
        });

        lbPic = new JLabel("Add picture here", JLabel.CENTER);
        lbPic.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbPic.setBounds(50, 160, 400, 400);
        lbPic.setBackground(Color.WHITE);
        lbPic.setOpaque(true);
        pnright1.add(lbPic);

        btnAdd = new JButton("CAPTURE");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBounds(150, 580, 100, 50);
        pnright1.add(btnAdd);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenCamera();
            }
        });

        btnLoad = new JButton("UPLOAD");
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLoad.setBounds(270, 580, 100, 50);
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                UploadImage();
            }
        });
        pnright1.add(btnLoad);

        btnSend = new JButton("SEND");
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSend.setBounds(470, 310, 100, 50);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientFileInput == null) {
                    JOptionPane.showMessageDialog(null, "Hay chon hinh anh");
                } else {
                    // Gọi hàm Send
                    // Thêm cái số 1 là gửi về Server để server biết phải làm cái gì
                    Send(clientFileInput, 1);
                }

            }
        });
        pnright1.add(btnSend);

        // thong tin nhan tu server
        lbTitlePic = new JLabel("Receive from server", JLabel.CENTER);
        lbTitlePic.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbTitlePic.setBounds(600, 100, 300, 20);
        pnright1.add(lbTitlePic);

        //Border blackline = BorderFactory.createLineBorder(Color.black);
        pnResult = new JPanel();
        pnResult.setBounds(590, 140, 800, 500);
        pnResult.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //pnResult.setBackground(color_background);
        pnResult.setLayout(null);
        pnResult.setVisible(true);
        pnright1.add(pnResult);

        lbPicFromServer = new JLabel("Picture server sends...", JLabel.CENTER);
        lbPicFromServer.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbPicFromServer.setBounds(10, 15, 400, 400);
        lbPicFromServer.setBackground(Color.WHITE);
        lbPicFromServer.setOpaque(true);
        pnResult.add(lbPicFromServer);

        lbPercent = new JLabel("...%/ 100%", JLabel.CENTER);
        lbPercent.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbPercent.setBounds(150, 430, 120, 50);
        lbPercent.setBackground(Color.WHITE);
        lbPercent.setOpaque(true);
        pnResult.add(lbPercent);

        //panel chua thong tin anh
        pnInfo = new JPanel();
        pnInfo.setBounds(420, 15, 370, 400);
        pnInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnInfo.setLayout(null);
        pnResult.add(pnInfo);

        lbName = new JLabel("Name: ", JLabel.CENTER);
        lbName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbName.setBounds(10, 40, 100, 50);
        pnInfo.add(lbName);

        txtName = new JTextField();
        txtName.setBounds(120, 40, 200, 50);
        txtName.setEditable(false);
        pnInfo.add(txtName);

        txtYOB = new JTextField();
        txtYOB.setBounds(120, 100, 200, 50);
        txtYOB.setEditable(false);

        lbYOB = new JLabel("YOB: ", JLabel.CENTER);
        lbYOB.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbYOB.setBounds(10, 100, 100, 50);

        pnResult.add(pnInfo);
        pnInfo.add(lbName);

        pnInfo.add(lbYOB);
        pnInfo.add(txtYOB);

        //=============== nut add =============
        lbAdd = new JLabel("Add", JLabel.CENTER);
        lbAdd.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbAdd.setBounds(300, 10, 200, 70);
        lbAdd.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        lbAdd.setForeground(Color.WHITE);
//        lbAdd.setBackground(color_background);
        //lbPerson.setBackground(Color.yellow);
        lbAdd.setOpaque(true);

        pnright1.add(lbAdd);

        //noi nhap thong tin nguoi trong anh
        pnAdd = new JPanel();
        pnAdd.setBounds(590, 140, 800, 500);
        //pnAdd.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnAdd.setLayout(null);
        pnAdd.setVisible(false);

        pnright1.add(pnAdd);

        btnSend2 = new JButton("ADD");
        btnSend2.setBounds(430, 220, 100, 50);
        btnSend2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnAdd.add(btnSend2);

        lbNameAdd = new JLabel("Name: ", JLabel.CENTER);
        lbNameAdd.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbNameAdd.setBounds(10, 40, 100, 50);
        pnAdd.add(lbNameAdd);

        lbYOBAdd = new JLabel("YOB: ");
        lbYOBAdd.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbYOBAdd.setBounds(30, 120, 100, 50);
        pnAdd.add(lbYOBAdd);

        txtNameAdd = new JTextField();
        txtNameAdd.setBounds(120, 40, 400, 50);
        txtNameAdd.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        pnAdd.add(txtNameAdd);

        txtYOBAdd = new JTextField();
        txtYOBAdd.setBounds(120, 120, 400, 50);
        txtYOBAdd.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        pnAdd.add(txtYOBAdd);
        lbAdd.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                btnSend.setVisible(false);
                pnResult.setVisible(false);
                pnAdd.setVisible(true);
                lbTitlePic.setVisible(false);
                lbAdd.setForeground(Color.WHITE);
                lbAdd.setBackground(color_background);
                lbCheck.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                lbCheck.setForeground(null);
                lbCheck.setBackground(null);
                txtNameAdd.setVisible(true);
                txtYOBAdd.setVisible(true);

            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });

        //main OBJECT------------------------------------------------------------------
        lbOj = new JLabel("Checking Object Here", JLabel.CENTER);
        lbOj.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbOj.setForeground(Color.WHITE);
        lbOj.setBackground(color_background);
        lbOj.setBounds(50, 10, 400, 70);
        //lbCheck.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lbOj.setOpaque(true);
        pnright2.add(lbOj);

        lbPicOj = new JLabel("Add picture here", JLabel.CENTER);
        lbPicOj.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbPicOj.setBounds(50, 160, 400, 450);
        lbPicOj.setBackground(Color.WHITE);
        lbPicOj.setOpaque(true);
        pnright2.add(lbPicOj);
//        btnAddOj= new JButton("CAPTURE");
//        btnAddOj.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnAddOj.setBounds(150, 580, 100, 50);
//        pnright2.add(btnAddOj);
        btnLoadOj = new JButton("UPLOAD");
        btnLoadOj.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLoadOj.setBounds(200, 650, 100, 50);

        pnright2.add(btnLoadOj);

        btnSendOj = new JButton("SEND");
        btnSendOj.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSendOj.setBounds(470, 310, 100, 50);
        pnright2.add(btnSendOj);

        lbServer = new JLabel("Receive from server", JLabel.CENTER);
        lbServer.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbServer.setBounds(600, 100, 300, 20);
        pnright2.add(lbServer);

        //ket qua server tra ve
        pnResultOj = new JPanel();
        pnResultOj.setBounds(590, 140, 800, 500);
        pnResultOj.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnResultOj.setLayout(null);
        pnright2.add(pnResultOj);

//        lbPicFromServerOj= new JLabel("Server sends...", JLabel.CENTER);
//        lbPicFromServerOj.setFont(new Font("Segoe UI", Font.BOLD, 25));
//        lbPicFromServerOj.setBounds(10, 15, 400, 400);
//        lbPicFromServerOj.setBackground(Color.WHITE);
//        lbPicFromServerOj.setOpaque(true);
//        pnResultOj.add(lbPicFromServerOj);
//        
//        lbPercentOj= new JLabel("__%/100%", JLabel.CENTER);
//        lbPercentOj.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        lbPercentOj.setBounds(150, 420, 100, 50);
//        lbPercentOj.setBackground(Color.WHITE);
//        lbPercentOj.setOpaque(true);
//        pnResultOj.add(lbPercentOj);
//        pnInfoOj = new JPanel();
//        pnInfoOj.setBounds(420,15 , 350, 400);
//        pnInfoOj.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        pnInfoOj.setLayout(null);
//        pnResultOj.add(pnInfoOj);
        lbNameOj = new JLabel("List of things : ", JLabel.CENTER);
        lbNameOj.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbNameOj.setBounds(0, 5, 200, 50);
        pnResultOj.add(lbNameOj);
//        
//        txtNameOj= new JTextField();
//        txtNameOj.setBounds(10,70,200,50);
//        txtNameOj.setEditable(false);
//        pnInfoOj.add(txtNameOj);

        txpNamOj = new JTextPane();
        txpNamOj.setBounds(25, 70, 750, 400);
        txpNamOj.setEditable(false);
        pnResultOj.add(txpNamOj);

        f.add(pnmenu);
        f.setVisible(true);
    }

    public void OpenCamera() {

    }

    public void Send(File file, int type) {
        //khởi tạo đối tượng request
        Request request = new Request();
        // gán data là file sau khi mã hóa
        request.setData(this.EncryptData(clientFileInput));
        // gán key là key sau khi mã hóa bằng RSA
        request.setKey(this.EncryptKey(key));
        // đây là cái kiểu phải làm gì
        request.setType(type);
        // dùng cái thằng outputStream để gửi cái request đi
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        // dùng cái thằng inputstream đẻ đọc tự server về
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            response = (Response) inputStream.readObject();
            if (response.getData() != null && response.getPhoto() != null) {
                Person person = (Person) getObject(DescryptData(response.getData()));
                Photo photo = (Photo) getObject(DescryptData(response.getPhoto()));
                lbPicFromServer.setIcon(new ImageIcon(photo.getPath()));
                lbPercent.setText(String.valueOf(response.getMessage()) + "%/ 100%");
                txtName.setText(person.getHoten());
                txtYOB.setText(String.valueOf(person.getNamsinh()));
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage());
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void UploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(this);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
        clientFileInput = fileChooser.getSelectedFile();
        if (clientFileInput != null) {
            ImageIcon image = new ImageIcon(clientFileInput.getPath());
            lbPic.setIcon(image);
        }

    }

    public void ConnectToServer(String address, int port) {
        try {
            socket = new Socket(address, port);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    public void DisconnectToServer() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // mã hóa dữ liệu dùng khóa của mã hóa AES
    public byte[] EncryptData(File file) {
        try {
            return AES.encrypt(key, Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // giải mã dùng khóa của AES
    public byte[] DescryptData(byte[] data) {
        return AES.decrypt(key, data);
    }

    //chuyển object từ byte[]
    private static Object getObject(byte[] byteArr) {
        ByteArrayInputStream in = new ByteArrayInputStream(byteArr);
        ObjectInputStream is = null;
        Object obj = null;
        try {
            is = new ObjectInputStream(in);
            obj = is.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return obj;
    }

    //mã hóa khóa AES bằng RSA
    public byte[] EncryptKey(SecretKey key) {
        try {
            publicKey = RSA.getPublicKey(url + "PublicKey.txt");
            byte[] encodedKey = RSA.encrypt(publicKey, key.getEncoded());
            return encodedKey;
        } catch (Exception ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {

        ClientGui c = new ClientGui();
        c.ConnectToServer("localhost", 5000);
        c.show();
    }
}
