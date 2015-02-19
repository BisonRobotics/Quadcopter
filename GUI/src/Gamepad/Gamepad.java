/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gamepad;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import net.java.games.input.*;

/**
 *
 * @author Jacob Huesman
 */
public class Gamepad {
    
    private static final int DELAY = 1000; // ms (polling interval)
    private static final float EPSILON = 0.0001f;
 // to deal with values near to 0.0 
    public Controller xbox = null;
    private Component[] components = null;
    private boolean found = false;
    boolean[] buttonsValues;
    
    public Gamepad(){
        win8Fix();
        init();
        
    }
    
    private boolean init(){
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        Controller[] controllers = ce.getControllers();
        
        //print name and type of each controller
        for (Controller c : controllers) {
            System.out.println(c.getName());
            if (c.getName().contains("XBOX 360 For Windows")) {
            
//if (c.getName().contains("Controller (XBOX 360 For Windows)")) {
                xbox = c;
                found = true;
                components = xbox.getComponents();
                System.out.println(components.length);
                return true;
            }
        }
        found = false;
        return false;
    }
    
    public float checkAxis(int i){
        return components[i].getPollData();
    }
    
    public boolean checkButton(int i){
        if(components[i].getPollData() == 1){
            return true;
        }
        return false;
    }
    
    public void sleep(int i){
        try {
            Thread.sleep(i);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public boolean poll(){
        return xbox.poll();
    }
    
    public String getContName(){
        return xbox.getName();
    }
    
    public void pollComponent(Component component){ 
        float prevValue = 0.0f;
        float currValue;
        int i = 1; // used to format the output
        while (true) {
            try {
                Thread.sleep(DELAY); // wait a while
            }
            catch (Exception ex) {}
            xbox.poll(); // update the controller's components
            currValue = component.getPollData(); // get current value
            if (currValue != prevValue) { // the value has changed
                if (Math.abs(currValue) > EPSILON) {
                    // only show values not near to 0.0f
                    System.out.print(currValue + "; ");
                    i++;
                }
                prevValue = currValue;
            }
            if (i%10 == 0) { // after several outputs, put in a newline
                System.out.println();
                i = 1;
            }
        }
    } // end of pollComponent()    
   
    public boolean isFound(){
        return found == true;
    }
    

    public void printComponents(){
        components = xbox.getComponents();
        if (components.length == 0){
            System.out.println("No Components");
        }
        else {
            System.out.println("Components: (" + components.length + ")");
            for (int i = 0; i < components.length; i++){
                System.out.println( i + ". " +
                    components[i].getName() + ", " +
                    getIdentifierName(components[i]) + ", " +
                    (components[i].isRelative() ? "relative" : "absolute") + ", " +
                    (components[i].isAnalog() ? "analog" : "digital") + ", " +
                    components[i].getDeadZone());
            }
        } 
    }
        
    private static String getIdentifierName(Component comp){
	 Component.Identifier id = comp.getIdentifier();
	 if (id == Component.Identifier.Button.UNKNOWN){
		return "button"; // an unknown button
	}
	 else if(id == Component.Identifier.Key.UNKNOWN){
		return "key"; // an unknown key
	}
	 else{
		return id.getName();
	}
    }
    
    private static void win8Fix() {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                String os = System.getProperty("os.name", "").trim();
                if (os.startsWith("Windows 8")) {  // 8, 8.1 etc.

                    // disable default plugin lookup
                    System.setProperty("jinput.useDefaultPlugin", "false");

                    // set to same as windows 7 (tested for windows 8 and 8.1)
                    System.setProperty("net.java.games.input.plugins", "net.java.games.input.DirectAndRawInputEnvironmentPlugin");
                }
                return null;
            }
        });

}
}
