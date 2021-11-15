/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

/**
 *
 * @author user
 */
public class Person {
    private int id;
    private String hoten;
    private int namsinh;
    
    public Person()
    {
        
    }
    
    public Person(int id, String hoten, int namsinh)
    {
        this.id = id;
        this.hoten = hoten;
        this.namsinh = namsinh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public int getNamsinh() {
        return namsinh;
    }

    public void setNamsinh(int namsinh) {
        this.namsinh = namsinh;
    }
    
    
    
}
