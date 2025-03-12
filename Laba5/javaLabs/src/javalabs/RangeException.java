/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

import javax.swing.JOptionPane;

/**
 *
 * @author Максим
 */
public class RangeException extends Exception {
    public RangeException(String information) {
        JOptionPane.showMessageDialog(null, information, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
