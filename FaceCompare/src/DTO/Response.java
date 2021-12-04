/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class Response implements Serializable {

    Person person;
    Photo photo;
    String message;
    ArrayList<String> obj;

    public Response(Person person, Photo photo, String message) {
        this.person = person;
        this.photo = photo;
        this.message = message;
    }
    
    public Response(String message) {
        this.message = message;
    }
    
    public Response(ArrayList<String> obj) {
        this.obj = obj;
    }

    public void setObj(ArrayList<String> obj) {
        this.obj = obj;
    }

    public ArrayList<String> getObj() {
        return obj;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
