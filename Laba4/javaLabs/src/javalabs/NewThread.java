/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Максим
 */
public class NewThread extends Thread {
    public DefaultTableModel model;
    public RecIntegral integral;
    public int index;
    
    public NewThread(DefaultTableModel model, RecIntegral integral, int index) {
        this.model = model;
        this.integral = integral;
        this.index = index;
    }
    
    @Override
    public void run() {
        double resNum = integral.Result();
        Object resObj = resNum;
        model.setValueAt(resObj, index, 3);
    }
}
