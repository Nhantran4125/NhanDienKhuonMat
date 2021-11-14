/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facecompare;

import DAO.PersonDAO;
import DAO.PhotoDAO;
import DTO.Person;
import DTO.Photo;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Main {
    public static void main(String args[])
    {        
          File input = new File("src/photo/test.jpg").getAbsoluteFile();
          compareAllPhoto(input);
    }
    
    public static void compareAllPhoto(File input)
    {
        //MẢNG ĐỐI TƯỢNG PHOTO - chứa tất cả các hình lưu từ CSDL  
        ArrayList<Person> listPerson = new ArrayList<Person>();          
        listPerson = PersonDAO.loadPerson();
        
        //MẢNG ĐỐI TƯỢNG PHOTO - chứa tất cả các hình lưu từ CSDL  
        ArrayList<Photo> listPhoto = new ArrayList<Photo>();        
        listPhoto = PhotoDAO.loadPhoto();        

        //hình client gửi để so khớp
        //File input = new File("src/photo/test.jpg").getAbsoluteFile();
          
        double max = 0; // Kết quả khớp nhất
        double confidence = 0; // kết quả so sánh 2 ảnh
        Photo matchPhoto = null; // Hình ảnh giống nhất
        
        FaceCompare fc = new FaceCompare();
        long startTime = System.currentTimeMillis();
               
        //lần lượt so sánh hình client gửi (input) với từng hình trong CSDL (fileCompare)
        for(Photo photo : listPhoto)
        {
            File fileCompare = new File(photo.getPath()).getAbsoluteFile();
            confidence = fc.compareFace(input, fileCompare); // so khớp 2 hình -> kết quả giống nhau
            if(confidence > max) // tìm được hình có độ giống nhau lớn hơn max  -> gán matchPhoto là p
            {
                max = confidence;
                matchPhoto = photo;
            }
        }
        long duration = System.currentTimeMillis()-startTime;
        
        System.out.println("Thoi gian cho doi: "+ duration);
        if(max > 60) // max tren 60% thì trả về kết quả
        {
            
            Person personMatch = new Person();
            for(Person person : listPerson) // tìm Person theo ID
            {
                if(matchPhoto.getId_person() == person.getId())
                    personMatch = person;
            }
            System.out.println("Do chinh xac - Confidence: "+max);           
            System.out.println("Hinh anh giong nhat co ID: "+ matchPhoto.getId());
            System.out.println("Duong dan: "+ matchPhoto.getPath());  
            System.out.println("Ten cua nguoi do: "+ personMatch.getHoten());
            System.out.println("Nam sinh cua nguoi do: "+ personMatch.getNamsinh());            
        }
        else
        {
            System.out.println("Khong tim thay nguoi nay");
        }  
    }
        
}
