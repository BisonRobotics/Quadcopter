/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gamepad;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Jacob Huesman
 */
public class Data implements Serializable{
    private Object[][] data;
    
    public static void main(String[] args){
        Object[][] object = new Object[2][0];
        Data data = new Data(object);
        data.save();
        System.out.println("worked");
    }
    
    public Data(){
        load();
    }
    
    public Data(Object[][] data){
        this.data = data;
    }
    
    public void save( ){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/gamepad", false));
            oos.writeObject(data);
            oos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void load(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/gamepad"));
            data = (Object[][])ois.readObject();
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public Object[][] getConfig(){
        return data;
    }
    
    public void setConfig(Object[][] config){
        data = config;
    }
}
