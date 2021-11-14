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
    private String id;
    private String id_person;
    private String path;
    
    public Photo()
    {
        this.id = null;
        this.id_person = null;
        this.path = null;
    }
    public Photo(String id, String id_person, String path)
    {
        this.id = id;
        this.id_person = id_person;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId_person() {
        return id_person;
    }

    public void setId_person(String id_person) {
        this.id_person = id_person;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
}
