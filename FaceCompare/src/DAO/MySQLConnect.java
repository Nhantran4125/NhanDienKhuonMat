/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class MySQLConnect {
    String user = "root";
    String password="";
    String url="jdbc:mysql://localhost:3306/dbfacedetection?useUnicode=yes&characterEncoding=UTF-8";
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    MySQLConnect(){
        if(conn==null) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected database successfully");
            }
            catch(ClassNotFoundException e){
                JOptionPane.showMessageDialog(null,e.toString());
            }
            catch(SQLException e){
                
                JOptionPane.showMessageDialog(null,e.toString());               
            }
        }
    }
    void  MySQLDisconnect(){
        try{
            if(rs!=null)rs.close();
            if(st!=null)st.close();
            if(conn!=null)conn.close();
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null,e.toString());
        }
    }
    
}
