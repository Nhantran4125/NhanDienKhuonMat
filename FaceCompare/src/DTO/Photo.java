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
public class Photo {
    private int id;
    private int id_person;
    private String path;

    public Photo()
    {
        
    }
    public Photo(int id, int id_person, String path)
    {
        this.id = id;
        this.id_person = id_person;
        this.path = path;
    }
    
     public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId_person() {
        return id_person;
    }

    public void setId_person(int id_person) {
        this.id_person = id_person;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    } 
}
