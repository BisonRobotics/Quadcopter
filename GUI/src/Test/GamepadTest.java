/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

/**
 *
 * @author Jacob Huesman
 */
public class GamepadTest extends Thread {
    public void run(){
        for(int i=0; i<100; i++){
            System.out.println("MyThread running");
            try{Thread.sleep(1000);} catch(Exception e){}
        }
        
    }
}
