/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.awt.Dimension;
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

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

        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Creating a camera
    public void startCamera() {
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;

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

                System.out.println(String.format("Detected %s faces",
                        faceDetections.toArray().length));

                // Drawing boxes
                for (Rect rect : faceDetections.toArray()) {
                    System.out.println(rect.area());

                    Imgproc.rectangle(
                            image, // where to draw the box
                            new Point(rect.x, rect.y), // bottom left
                            new Point(rect.x + rect.width, rect.y + rect.height), // top right
                            new Scalar(0, 0, 250),
                            3 // RGB colour
                    );
                    Imgcodecs.imencode(".jpg", image, buf);

                    Mat a = Imgcodecs.imread("Hello");
                    imageData = buf.toArray();
                    //  icon = new ImageIcon(imageData);
                    icon = new ImageIcon(imageData);
                    cameraScreen.setIcon(icon);

                }
                
                for (Rect rect : faceDetections.toArray()) {
                    System.out.println(rect.area());
                   
                    Imgproc.rectangle(
                            image, // where to draw the box
                            new Point(rect.x, rect.y), // bottom left
                            new Point(rect.x + rect.width, rect.y + rect.height), // top right
                            new Scalar(0, 0, 0),
                            3 // RGB colour
                    );
                    
                }
            }
           
        }
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startCamera();
                    }
                }).start();
            }
        });
    }
}
