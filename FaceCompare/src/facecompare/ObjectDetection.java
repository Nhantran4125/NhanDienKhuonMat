
package facecompare;
import java.io.File;
import org.opencv.core.*;
import org.opencv.dnn.DetectionModel;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjectDetection {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    File file;
 
    public ObjectDetection(File file) {
        this.file = file;
    }
    
    // return mang chua ten cac doi tuong duoc phat hien
    public ArrayList<String> ObjDetection(File file) throws IOException{
        //tao mang cac doi tuong
        ArrayList<String> obj = new ArrayList<String>();
        //lay duong dan cua image
        Mat img = Imgcodecs.imread(file.getPath());
        
        List<String> classes = Files.readAllLines(Paths.get("yolo/coco.names"));
        Net net = Dnn.readNetFromDarknet("yolo/yolov4.cfg", "yolo/yolov4.weights");
        DetectionModel model = new DetectionModel(net);
        model.setInputParams(1 / 255.0, new Size(416, 416), new Scalar(0), true);
        
        //quet anh va xac dinh doi tuong trong anh
        MatOfInt classIds = new MatOfInt();
        MatOfFloat scores = new MatOfFloat();
        MatOfRect boxes = new MatOfRect();
        model.detect(img, classIds, scores, boxes, 0.6f, 0.4f);
        
        //get name cua tung doi tuong
        for (int i = 0; i < classIds.rows(); i++) {
            int classId = (int) classIds.get(i, 0)[0];
            String text = String.format(classes.get(classId));
            if(!obj.contains(text)) {
                obj.add(text);
            }
        }
        return obj;
    }
}
