/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gamepad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import net.java.games.input.Component;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * @author Jacob Huesman
 */
public class GamePanel extends javax.swing.JPanel {

    private Gamepad gamepad;
    private int poll;
    private Data storage;
    private Object[][] data;
    private String[] columnNames;
    
    public GamePanel() {
        initVar();
        initComponents();
        initButtons();
        initGamepad();
        //Make a table model
        
    }
    
    private void initVar(){
        poll = 100;
        storage = new Data();
        data = storage.getConfig();
        columnNames = new String[]{"Setting","Value"};
    }
    
    
    public void initGamepad(){
        gamepad = new Gamepad();
        if(gamepad.isFound()){
            controllerName.setText(gamepad.getContName());
            Thread thread = new Thread(){
                public void run(){
                    while(true){
                        if(gamepad.poll()){
                            updateComponents();
                            try{Thread.sleep(poll);} catch(Exception e){}
                        } else{
                            controllerName.setText("Controller Disconnected...");
                            return;
                        }
                    }
                }
            };
            thread.start();
        } else{
            controllerName.setText("Controller Disconnected...");
        }
    }
    
    public void setPoll(int poll){
        this.poll = poll;
    }
    
    private JToggleButton[] buttons;
    
    public void initButtons(){
        buttons = new JToggleButton[10];
        buttons[0] = new JToggleButton("A", false);
        buttons[1] = new JToggleButton("B", false);
        buttons[2] = new JToggleButton("X", false);
        buttons[3] = new JToggleButton("Y", false);
        buttons[4] = new JToggleButton("LT", false);
        buttons[5] = new JToggleButton("RT", false);
        buttons[6] = new JToggleButton("Bck", false);
        buttons[7] = new JToggleButton("Str", false);
        buttons[8] = new JToggleButton("LG", false);
        buttons[9] = new JToggleButton("RG", false);
        
        buttonsPanel.removeAll();
        for(int i=0; i < 10; i++) {
            buttons[i].setPreferredSize(new Dimension(48, 25));
            buttons[i].setEnabled(false);
            buttonsPanel.add(buttons[i]);
        }        
    }
    
    public void updateComponents(){
        checkLeftXYAxis();
        checkRightXYAxis();
        checkZAxis();
        checkButtons();
        checkHatSwitch();
    }
    
    private final int LYAXIS = 0, LXAXIS = 1, RYAXIS = 2, RXAXIS = 3, ZAXIS = 4, B0=5, B1=6, B2=7, B3=8, B4=9, B5=10, B6=11, B7=12, B8=13, B9=14;
    private int lxaxis, lyaxis, rxaxis, ryaxis, zaxis;
    
    public void checkButtons(){
        for(int i=0; i<this.buttons.length; i++){
           buttons[i].setSelected(gamepad.checkButton(i+5));
        }
    }
    
    public void checkZAxis(){
        int pzaxis = 99-getPercent(gamepad.checkAxis(ZAXIS));
        if(pzaxis != zaxis){
            setZAxis(pzaxis);
            zaxis = pzaxis;
        }
    }
    
    public void checkLeftXYAxis(){
        boolean refresh = false;
        int plxaxis = getPercent(gamepad.checkAxis(LXAXIS));
        int plyaxis = getPercent(gamepad.checkAxis(LYAXIS));
        if(lxaxis != plxaxis){
            //System.out.println(plxaxis);
            refresh = true;
            lxaxis = plxaxis;
        }
        if(lyaxis != plyaxis){
            //System.out.println(plyaxis);
            refresh = true;
            lyaxis = plyaxis;
        }
        if(refresh == true){
            setLeftXYAxis(lxaxis, lyaxis);
        }
    }
    
    public void checkRightXYAxis(){
        boolean refresh = false;
        int prxaxis = getPercent(gamepad.checkAxis(RXAXIS));
        int pryaxis = getPercent(gamepad.checkAxis(RYAXIS));
        if(rxaxis != prxaxis){
            //System.out.println(plxaxis);
            refresh = true;
            rxaxis = prxaxis;
        }
        if(ryaxis != pryaxis){
            //System.out.println(pryaxis);
            refresh = true;
            ryaxis = pryaxis;
        }
        if(refresh == true){
            setRightXYAxis(rxaxis, ryaxis);
        }
    }
    
    public void checkHatSwitch(){
        setHatSwitch(gamepad.checkAxis(15));
    }
    
    public void setLeftXYAxis(int xPercentage, int yPercentage){
        Graphics2D g = (Graphics2D)leftXYAxis.getGraphics();
        g.clearRect(1,1, leftXYAxis.getWidth()-2, leftXYAxis.getHeight()-2);
        g.fillOval(xPercentage, yPercentage, 10, 10);
    }
    
    public void setRightXYAxis(int xPercentage, int yPercentage){
        Graphics2D g = (Graphics2D)rightXYAxis.getGraphics();
        g.clearRect(1,1, rightXYAxis.getWidth()-2, rightXYAxis.getHeight()-2);
        g.fillOval(xPercentage, yPercentage, 10, 10);
    }
    
    public void setZAxis(int percentage){
        //System.out.println(percentage);
        zAxis.setValue(percentage);
    }
    
    private float hatSwitchPosition = 0;
    
    public void setHatSwitch(float hatSwitchPosition) {
        if(this.hatSwitchPosition == hatSwitchPosition){
            return;
        }
        this.hatSwitchPosition = hatSwitchPosition;

        int circleSize = 80;
        
        Graphics2D g2d = (Graphics2D)jPanelHatSwitch.getGraphics();
        g2d.clearRect(5, 15, jPanelHatSwitch.getWidth() - 10, jPanelHatSwitch.getHeight() - 22);
        g2d.drawOval(15, 19, circleSize, circleSize);
        
        if(Float.compare(hatSwitchPosition, Component.POV.OFF) == 0)
            return;
        
        int smallCircleSize = 8;
        int upCircleX = 51;
        int upCircleY = 15;
        int leftCircleX = 11;
        int leftCircleY = 55;
        int betweenX = 30;
        int betweenY = 18;
        
        int x = 0;
        int y = 0;
        
        //g2d.setColor(Color.blue);
                        
        if(Float.compare(hatSwitchPosition, Component.POV.UP) == 0){
            x = upCircleX;
            y = upCircleY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.DOWN) == 0){
            x = upCircleX;
            y = upCircleY + circleSize;
        }else if(Float.compare(hatSwitchPosition, Component.POV.LEFT) == 0){
            x = leftCircleX;
            y = leftCircleY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.RIGHT) == 0){
            x = leftCircleX + circleSize;
            y = leftCircleY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.UP_LEFT) == 0){
            x = upCircleX - betweenX;
            y = upCircleY + betweenY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.UP_RIGHT) == 0){
            x = upCircleX + betweenX;
            y = upCircleY + betweenY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.DOWN_LEFT) == 0){
            x = upCircleX - betweenX;
            y = upCircleY + circleSize - betweenY;
        }else if(Float.compare(hatSwitchPosition, Component.POV.DOWN_RIGHT) == 0){
            x = upCircleX + betweenX;
            y = upCircleY + circleSize - betweenY;
        }
        
        g2d.fillOval(x, y, smallCircleSize, smallCircleSize);
    }
    
    //Takes the axis value between -1f and 1f and returns an integer percentage
    private int getPercent(float axis){
        return (int)(((2 - (1 - axis)) * 100)/2);
    }    
    
    public void reconnect(){
        if (System.getProperty("os.name").equals("Windows 8.1") &&
            System.getProperty("os.arch").equals("x86")){

            try {
                Class<?> clazz = Class.forName("net.java.games.input.DefaultControllerEnvironment");
                Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
                defaultConstructor.setAccessible(true); // set visibility to public

                Field defaultEnvironementField = ControllerEnvironment.class.getDeclaredField("defaultEnvironment");
                defaultEnvironementField.setAccessible(true);
                defaultEnvironementField.set(ControllerEnvironment.getDefaultEnvironment(), defaultConstructor.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initGamepad();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelAxes = new javax.swing.JPanel();
        jLabelXYAxis = new javax.swing.JLabel();
        leftXYAxis = new javax.swing.JPanel();
        progressBarLabel3 = new javax.swing.JLabel();
        rightXYAxis = new javax.swing.JPanel();
        jLabelXYAxis1 = new javax.swing.JLabel();
        zAxis = new javax.swing.JProgressBar();
        buttonsPanel = new javax.swing.JPanel();
        jPanelHatSwitch = new javax.swing.JPanel();
        controllerName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setDoubleBuffered(false);
        setMinimumSize(new java.awt.Dimension(400, 500));
        setPreferredSize(new java.awt.Dimension(400, 500));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelAxes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Axes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 51, 204)));
        jPanelAxes.setDoubleBuffered(false);

        jLabelXYAxis.setText("Left X  / Y Axis");
        jLabelXYAxis.setToolTipText("");

        leftXYAxis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        leftXYAxis.setPreferredSize(new java.awt.Dimension(111, 111));

        javax.swing.GroupLayout leftXYAxisLayout = new javax.swing.GroupLayout(leftXYAxis);
        leftXYAxis.setLayout(leftXYAxisLayout);
        leftXYAxisLayout.setHorizontalGroup(
            leftXYAxisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );
        leftXYAxisLayout.setVerticalGroup(
            leftXYAxisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        progressBarLabel3.setText("Z Axis");

        rightXYAxis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rightXYAxis.setPreferredSize(new java.awt.Dimension(111, 111));

        javax.swing.GroupLayout rightXYAxisLayout = new javax.swing.GroupLayout(rightXYAxis);
        rightXYAxis.setLayout(rightXYAxisLayout);
        rightXYAxisLayout.setHorizontalGroup(
            rightXYAxisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );
        rightXYAxisLayout.setVerticalGroup(
            rightXYAxisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabelXYAxis1.setText("Right X / Y Axis");

        zAxis.setMaximum(99);
        zAxis.setOrientation(1);
        zAxis.setToolTipText("");
        zAxis.setDoubleBuffered(true);
        zAxis.setPreferredSize(new java.awt.Dimension(29, 111));

        javax.swing.GroupLayout jPanelAxesLayout = new javax.swing.GroupLayout(jPanelAxes);
        jPanelAxes.setLayout(jPanelAxesLayout);
        jPanelAxesLayout.setHorizontalGroup(
            jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAxesLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftXYAxis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelAxesLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabelXYAxis)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rightXYAxis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAxesLayout.createSequentialGroup()
                        .addComponent(jLabelXYAxis1)
                        .addGap(21, 21, 21)))
                .addGap(34, 34, 34)
                .addGroup(jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(progressBarLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zAxis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        jPanelAxesLayout.setVerticalGroup(
            jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAxesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelXYAxis1)
                    .addComponent(jLabelXYAxis)
                    .addComponent(progressBarLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rightXYAxis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftXYAxis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zAxis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        add(jPanelAxes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 380, 180));

        buttonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buttons", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 51, 204)));
        buttonsPanel.setDoubleBuffered(false);
        add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 250, 110));

        jPanelHatSwitch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hat Switch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 51, 204)));
        jPanelHatSwitch.setDoubleBuffered(false);
        jPanelHatSwitch.setPreferredSize(jPanelHatSwitch.getSize());

        javax.swing.GroupLayout jPanelHatSwitchLayout = new javax.swing.GroupLayout(jPanelHatSwitch);
        jPanelHatSwitch.setLayout(jPanelHatSwitchLayout);
        jPanelHatSwitchLayout.setHorizontalGroup(
            jPanelHatSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelHatSwitchLayout.setVerticalGroup(
            jPanelHatSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 87, Short.MAX_VALUE)
        );

        add(jPanelHatSwitch, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 220, 110, 110));

        controllerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        controllerName.setText("Controller name");
        controllerName.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(controllerName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 390, -1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 337, 380, 150));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel controllerName;
    private javax.swing.JLabel jLabelXYAxis;
    private javax.swing.JLabel jLabelXYAxis1;
    private javax.swing.JPanel jPanelAxes;
    private javax.swing.JPanel jPanelHatSwitch;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel leftXYAxis;
    private javax.swing.JLabel progressBarLabel3;
    private javax.swing.JPanel rightXYAxis;
    private javax.swing.JTable table;
    private javax.swing.JProgressBar zAxis;
    // End of variables declaration//GEN-END:variables
}
