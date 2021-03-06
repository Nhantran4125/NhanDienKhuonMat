/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.awt.Dimension;
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.CAP_DSHOW;

/**
 *
 * @author DELL
 */
public class Camera extends JFrame {
    // Camera screen

    private JLabel cameraScreen = null;

    // Button for image capture
    private JButton btnCapture = null;

    // Start camera
    private VideoCapture capture = null;


    // Store image as 2D matrix
    private Mat image = null;

    private boolean clicked = false;
    //private static final String xml = "D:/test/test/src/OpenCV/lbpcascade_frontalface.xml";
    private static final String xml = "src/facecompare/lbpcascade_frontalface.xml";
    public static String capturePath; //đường dẫn của file ảnh vừa chụp từ webcam

    public Camera() {

        // Designing UI
        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 640, 480);
        add(cameraScreen);

        btnCapture = new JButton("capture");
        btnCapture.setBounds(300, 480, 80, 40);
        add(btnCapture);

        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                clicked = true;
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                //do something
                capture.release();
            }
        });

        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // Creating a camera
    public void startCamera() {
        //capture = new VideoCapture(0);
        capture = new VideoCapture(0, CAP_DSHOW);
        //capture = new VideoCapture(1, CAP_DSHOW);

        image = new Mat();
        byte[] imageData;

        if (capture.isOpened()) {
            //----------------------------
            ImageIcon icon;
            while (true) {
                // read image to matrix
                if (capture.read(image)) {
                    CascadeClassifier classifier = new CascadeClassifier(xml);
                    // convert matrix to byte
                    final MatOfByte buf = new MatOfByte();
                    Imgcodecs.imencode(".jpg", image, buf);

                    imageData = buf.toArray();
                    MatOfRect faceDetections = new MatOfRect();
                    classifier.detectMultiScale(image, faceDetections);
                    // Add to JLabel
                    icon = new ImageIcon(imageData);
                    cameraScreen.setIcon(icon);
                    // Detecting the face in the snap
                }

                //Khi chọn nút CAPTURE => clicked = true
                if (clicked) {
                    saveImage(); //tạo file ảnh

                    ImageIcon image = new ImageIcon(capturePath);
                    //gán hình vừa chụp vào biến static lbPic(clalss ClientGui)
                    ClientGui.lbPic.setIcon(image);
                    //gán đường dẫn hình vừa chụp vào text của lbPic
                    ClientGui.lbPic.setText(capturePath);
                    capture.release(); //tắt webcam  
                    //HighGui.destroyAllWindows();
                    this.dispose();
                    //this.setVisible(false);
                    //this.dispose();

                }
            }
            //--------
        }
        HighGui.destroyAllWindows();
    }

    private void saveImage() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String name = dateFormat.format(new Date());

        Path dirDesktop = Paths.get(System.getProperty("user.home"), "Desktop");
        // Hình lưu ở Desktop
        Imgcodecs.imwrite(dirDesktop + "\\" + name + ".jpg", image);
        capturePath = dirDesktop + "\\" + name + ".jpg";
        clicked = false;
    }

    // Main driver method
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(new Runnable() {
            // Overriding existing run() method
            @Override
            public void run() {
                final Camera camera = new Camera();

                // Start camera in thread
                new Thread(camera::startCamera).start();
            }
        });
    }
}
