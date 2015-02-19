package Comm;

import Gamepad.GamePanel;
import java.awt.BorderLayout;
import java.io.PrintStream;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.java.games.input.Controller;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.java.games.input.Component;
import net.java.games.input.Controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jacob Huesman
 */
public class GUI extends javax.swing.JFrame {

    /**
     * Creates new form GUI
     */
    //private JInputJoystick joystick;
    
    private PrintStream printStream;
    private Serial serial;
    //private Gamepad gamepad;
    private GamePanel gamePanel;
    
    public GUI() {
        initComponents();
        initGamepad();
        initPrintStream();
        initSerial();


        
    }
    
    
    private void initGamepad(){
        gamePanel = new GamePanel();
        container.add(gamePanel, BorderLayout.CENTER);
        
        
    }
    
    private void initSerial(){
        serial = new Serial();
        serial.input();
    }
    
    
    //Create a logging print stream as well.
    private void initLogFile() {
        /*printStream = new PrintStream(new CustomOutputStream(consoleText));
        System.setOut(printStream);
        System.setErr(printStream);*/
    }
    
    private void initPrintStream(){
        printStream = new PrintStream(new CustomOutputStream(consoleText));
        System.setOut(printStream);
        System.setErr(printStream);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        consoleText = new javax.swing.JTextArea();
        input = new javax.swing.JTextField();
        sendCommand = new javax.swing.JButton();
        container = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        close = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        reconnect = new javax.swing.JMenuItem();
        listPorts = new javax.swing.JMenuItem();
        reconnectCont = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Send", "Data", "Receive", "Data"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        consoleText.setColumns(20);
        consoleText.setRows(5);
        jScrollPane2.setViewportView(consoleText);

        jTabbedPane1.addTab("Console", jScrollPane2);

        sendCommand.setText("Send");
        sendCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendCommandActionPerformed(evt);
            }
        });

        container.setAlignmentX(0.0F);
        container.setAlignmentY(0.0F);
        container.setMaximumSize(new java.awt.Dimension(400, 340));
        container.setMinimumSize(new java.awt.Dimension(400, 340));
        container.setPreferredSize(new java.awt.Dimension(400, 340));
        container.setLayout(new java.awt.BorderLayout());

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        close.setText("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        jMenu1.add(close);

        menuBar.add(jMenu1);

        jMenu2.setText("Edit");
        menuBar.add(jMenu2);

        jMenu3.setText("Commands");
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        reconnect.setText("Reconnect");
        reconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconnectActionPerformed(evt);
            }
        });
        jMenu3.add(reconnect);

        listPorts.setText("List Ports");
        listPorts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listPortsActionPerformed(evt);
            }
        });
        jMenu3.add(listPorts);

        reconnectCont.setText("Reconnect Controller");
        reconnectCont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconnectContActionPerformed(evt);
            }
        });
        jMenu3.add(reconnectCont);

        menuBar.add(jMenu3);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(container, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(input)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendCommand)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sendCommand)
                            .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(container, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_closeActionPerformed

    private void reconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconnectActionPerformed
        // TODO add your handling code here:
        serial.initialize();
    }//GEN-LAST:event_reconnectActionPerformed

    private void listPortsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listPortsActionPerformed
       serial.listPorts();
    }//GEN-LAST:event_listPortsActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void sendCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendCommandActionPerformed
        serial.output(input.getText());
        input.setText("");
    }//GEN-LAST:event_sendCommandActionPerformed

    private void reconnectContActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconnectContActionPerformed
        gamePanel.reconnect();
    }//GEN-LAST:event_reconnectContActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public synchronized static void main(String args[]) {
        
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }
        /* Create and display the form */
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
                gui.pack();
                gui.setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem close;
    private javax.swing.JTextArea consoleText;
    private javax.swing.JPanel container;
    private javax.swing.JTextField input;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem listPorts;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem reconnect;
    private javax.swing.JMenuItem reconnectCont;
    private javax.swing.JButton sendCommand;
    // End of variables declaration//GEN-END:variables
}