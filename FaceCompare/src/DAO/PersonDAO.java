/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DTO.Person;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class PersonDAO {
  
    public static ArrayList loadPerson() {
        MySQLConnect ConnectData = new MySQLConnect();
        ArrayList<Person> listPerson = new ArrayList<Person>();
        try {
            String qry = "select * from person";
            ConnectData.st = ConnectData.conn.createStatement();
            ConnectData.rs = ConnectData.st.executeQuery(qry);

            while (ConnectData.rs.next()) {
                Person person = new Person();
                person.setId((ConnectData.rs.getInt(1)));
                person.setHoten((ConnectData.rs.getString(2)));
                person.setNamsinh((ConnectData.rs.getInt(3)));
                listPerson.add(person);
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());

        }
        ConnectData.MySQLDisconnect();
        return listPerson;
    }
    

    public void add(Person person) {
        try{
        
        MySQLConnect connect = new MySQLConnect();
        //int count=0;
        //count= demSlNguoi() +1;

            String qry = "insert into person(hoten,namsinh) value('";
            qry += person.getHoten() + "',";
            qry += person.getNamsinh() + ")";
            
            //System.out.println(qry); //test thử câu query viết đúng chưa
            connect.st = connect.conn.createStatement();
            connect.st.executeUpdate(qry);
            System.out.println(qry);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    public int demSlNguoi()
    {
        
         try {
             int count=0;
             MySQLConnect connect = new MySQLConnect();
             String qry="select count(id) from person";
             connect.st = connect.conn.createStatement();
             connect.rs = connect.st.executeQuery(qry);
             while(connect.rs.next())
             {
                 count = connect.rs.getInt(1);
             }
             return count;              
         } catch (SQLException ex) {
             Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
         }
         return 0;
        
    }

}
