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
    private String id, hoten;
    private int namsinh;
    
    public Person()
    {
        
    }
    
    public Person(String id, String hoten, int namsinh)
    {
        this.id = id;
        this.hoten = hoten;
        this.namsinh = namsinh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
