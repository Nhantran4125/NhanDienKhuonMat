/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DTO.Photo;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class PhotoDAO {

    public static ArrayList loadPhoto() {
        MySQLConnect ConnectData = new MySQLConnect();
        ArrayList<Photo> listPhoto = new ArrayList<Photo>();
        try {
            String qry = "select * from photo";
            ConnectData.st = ConnectData.conn.createStatement();
            ConnectData.rs = ConnectData.st.executeQuery(qry);

            while (ConnectData.rs.next()) {
                Photo photo = new Photo();

//                tacgia.setId((ConnectData.rs.getString(1)));                
                photo.setId((ConnectData.rs.getInt(1)));
                photo.setId_person((ConnectData.rs.getInt(2)));
                photo.setPath((ConnectData.rs.getString(3)));
                listPhoto.add(photo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());

        }
        ConnectData.MySQLDisconnect();
        return listPhoto;
    }
    

    public void add(Photo photo) {
        MySQLConnect connect = new MySQLConnect();
        try {
            String qry = "insert into photo value(";
            qry += photo.getId() + ",";
            qry += photo.getId_person() + ",'";
            qry += photo.getPath() + "')";

            //System.out.println(qry); //test thử câu query viết đúng chưa
            connect.st = connect.conn.createStatement();
            connect.st.executeUpdate(qry);
            System.out.println(qry);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

}
