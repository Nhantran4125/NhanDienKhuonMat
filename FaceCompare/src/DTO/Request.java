/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class Request implements Serializable {

    int type;
    Person person;
    File file;

    public Request(int type, File file) {
        this.type = type;
        this.file = file;
    }

    public Request(int type, Person person, File file) {
        this.type = type;
        this.person = person;
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    

}
