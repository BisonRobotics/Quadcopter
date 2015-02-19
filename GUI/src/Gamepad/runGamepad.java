/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gamepad;

import Gamepad.Gamepad;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * @author Jacob Huesman
 */
public class runGamepad {
    public static void main(String[] args){
        Gamepad gamepad = new Gamepad();
        
        while(!gamepad.isFound()){
            System.out.println("Couldn't find a controller!");
            
            
            gamepad = new Gamepad();

        }
        gamepad.printComponents();
        gamepad.pollComponent(gamepad.xbox.getComponents()[0]);
    }
}
