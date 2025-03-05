/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javalabs;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

/**
 *
 * @author Максим
 */
public class MainForm extends javax.swing.JFrame {
    
    private ArrayList<RecIntegral> collection = new ArrayList<>();

    JFileChooser fileChooser = new JFileChooser();
    
    JPopupMenu loadMenu = new JPopupMenu();
    JMenuItem textLoad = new JMenuItem("Текстовый");
    JMenuItem binaryLoad = new JMenuItem("Двоичный");
    JMenuItem jsonLoad = new JMenuItem("Json");
    
    JPopupMenu saveMenu = new JPopupMenu();
    JMenuItem textSave = new JMenuItem("Текстовый");
    JMenuItem binarySave = new JMenuItem("Двоичный");
    JMenuItem jsonSave = new JMenuItem("Json");
    
    void Read(FileReader reader, String path) { 
        
        char[] buffer = new char[2048];
        try {
            reader.read(buffer);
        } catch (Exception e) {return;}
        
        DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        model.setRowCount(0);
        collection.clear();
        String temp = String.copyValueOf(buffer);
        String datas = String.copyValueOf(buffer, 0, temp.indexOf('\0'));
        
        String[] rows = datas.split("\n");
        for (String row : rows) {
            if (row.isEmpty()) break;
            try {
                String[] values = row.split("\t");

                RecIntegral info = new RecIntegral(Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]));

                collection.add(info);
                double[] integralFields = info.GetResultFields();
                model.addRow(new Object[] {integralFields[1], integralFields[2], integralFields[3], integralFields[0]});
            } catch (RangeException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void ReadJson(FileReader reader, String path) { 

    char[] buffer = new char[2048];
    try {
        reader.read(buffer);
    } catch (Exception e) {return;}

    DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
    model.setRowCount(0);
    collection.clear();
    String temp = String.copyValueOf(buffer);
    String datas = String.copyValueOf(buffer, 0, temp.indexOf('\0'));

    try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(datas);
            JSONObject jsonObject = (JSONObject) obj;

            var jsonArray = (JSONArray) jsonObject.get("results");
            var it = jsonArray.iterator();

            while (it.hasNext()) {
                var result = (JSONObject) it.next();
                String low = result.get("low").toString(),
                        high = result.get("high").toString(),
                        step = result.get("step").toString(),
                        res = result.get("result").toString();
                try {
                    RecIntegral info = new RecIntegral(Double.parseDouble(low),
                            Double.parseDouble(high),
                            Double.parseDouble(step));
                    info.SetResult(Double.parseDouble(res));

                    collection.add(info);
                    double[] integralFields = info.GetResultFields();
                    model.addRow(new Object[] {integralFields[1], integralFields[2], integralFields[3], integralFields[0]});
                } catch (RangeException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void Read(ObjectInputStream reader) {
        String buffer = new String();
        try {
            buffer = (String)reader.readObject();
        } catch (Exception e) {return;}

        DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        model.setRowCount(0);

        String[] rows = buffer.split("\n");
        for (String row : rows) {
            if (row.isEmpty()) break;
            try {
                String[] values = row.split("\t");

                RecIntegral info = new RecIntegral(Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]));

                collection.add(info);
                double[] integralFields = info.GetResultFields();
                model.addRow(new Object[] {integralFields[1], integralFields[2], integralFields[3], integralFields[0]});
            } catch (RangeException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void Write(FileWriter writer) {
        String buffer = new String();
        
        for (RecIntegral row : collection) {
            double[] integralFields = row.GetResultFields();
            buffer += Double.toString(integralFields[1]) + "\t"
                    + Double.toString(integralFields[2]) + "\t"
                    + Double.toString(integralFields[3]) + "\t"
                    + Double.toString(integralFields[0]) + "\n";
        }
        try {
            writer.write(buffer);
        } catch (Exception e) {
            new RangeException(e.getLocalizedMessage());
        }
        try {
            writer.close();
        } catch (Exception e) {}
    }
    
    void WriteJson(FileWriter writer) {
        var jsonObject = new JSONObject();
        
        var jsonArray = new JSONArray();
        
        
        for (RecIntegral row : collection) {
            double[] integralFields = row.GetResultFields();
            var obj = new JSONObject();
            
            obj.put("low", integralFields[1]);
            obj.put("high", integralFields[2]);
            obj.put("step", integralFields[3]);
            obj.put("result", integralFields[0]);
            
            jsonArray.add(obj);
        }
        
        jsonObject.put("results", jsonArray);
        
        try {
            writer.write(jsonObject.toString());
        } catch (Exception e) {
            new RangeException(e.getLocalizedMessage());
        }
        try {
            writer.close();
        } catch (Exception e) {}
    }
    
    void Write(ObjectOutputStream writer) {
        String buffer = new String();
        
        for (RecIntegral row : collection) {
            double[] integralFields = row.GetResultFields();
            buffer += Double.toString(integralFields[1]) + "\t"
                    + Double.toString(integralFields[2]) + "\t"
                    + Double.toString(integralFields[3]) + "\t"
                    + Double.toString(integralFields[0]) + "\n";
        }
        
        try {
            writer.writeObject(buffer);
        } catch (Exception e) {
            new RangeException(e.getLocalizedMessage());
        }
        try {
            writer.close();
        } catch (Exception e) {}
    }
    
    /**
     * Creates new form mainForm
     */
    public MainForm() {
        initComponents();
        
        loadMenu.add(textLoad);
        loadMenu.add(binaryLoad);
        loadMenu.add(jsonLoad);
        textLoad.addActionListener((ActionEvent e) -> {
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                Read(new FileReader(file.getAbsolutePath()), file.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        binaryLoad.addActionListener((ActionEvent e) -> {
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                Read(new ObjectInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file.getAbsolutePath()))));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        jsonLoad.addActionListener((ActionEvent e) -> {
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                ReadJson(new FileReader(file.getAbsolutePath()), file.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        saveMenu.add(textSave);
        saveMenu.add(binarySave);
        saveMenu.add(jsonSave);
        textSave.addActionListener((ActionEvent e) -> {
            if (collection.isEmpty()) return;
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                Write(new FileWriter(file.getAbsolutePath()));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        binarySave.addActionListener((ActionEvent e) -> {
            if (collection.isEmpty()) return;
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                Write(new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file.getAbsolutePath()))));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        jsonSave.addActionListener((ActionEvent e) -> {
            if (collection.isEmpty()) return;
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fileChooser.getSelectedFile();
            
            try {
                WriteJson(new FileWriter(file.getAbsolutePath()));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoTable = new javax.swing.JTable();
        addEntryButton = new javax.swing.JButton();
        deleteEntryButton = new javax.swing.JButton();
        getResultButton = new javax.swing.JButton();
        lowerBorderTextField = new javax.swing.JTextField();
        upperBorderTextField = new javax.swing.JTextField();
        stepTextField = new javax.swing.JTextField();
        lowerBorderLabel = new javax.swing.JLabel();
        upperBorderLabel = new javax.swing.JLabel();
        stepLabel = new javax.swing.JLabel();
        cleanButton = new javax.swing.JButton();
        fillButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Вычисление определенного интегралла - 1/x");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBackground(new java.awt.Color(211, 183, 216));

        infoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Нижняя граница", "Верхняя граница", "Шаг", "Результат"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(infoTable);
        infoTable.getAccessibleContext().setAccessibleDescription("");

        addEntryButton.setBackground(new java.awt.Color(161, 62, 151));
        addEntryButton.setText("Добавить");
        addEntryButton.setToolTipText("Добавить данные в таблицу");
        addEntryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addEntryButtonMouseClicked(evt);
            }
        });

        deleteEntryButton.setBackground(new java.awt.Color(161, 62, 151));
        deleteEntryButton.setText("Удалить");
        deleteEntryButton.setToolTipText("Удалить выбранную строку");
        deleteEntryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteEntryButtonMouseClicked(evt);
            }
        });

        getResultButton.setBackground(new java.awt.Color(161, 62, 151));
        getResultButton.setText("Вычислить");
        getResultButton.setToolTipText("Вычислить выбранную строку");
        getResultButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getResultButtonMouseClicked(evt);
            }
        });

        lowerBorderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lowerBorderLabel.setText(" Нижняя граница");

        upperBorderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        upperBorderLabel.setText("Верхняя граница");

        stepLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stepLabel.setText("Шаг");

        cleanButton.setBackground(new java.awt.Color(161, 62, 151));
        cleanButton.setText("Очистить");
        cleanButton.setToolTipText("Очистить таблицу");
        cleanButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cleanButtonMouseClicked(evt);
            }
        });

        fillButton.setBackground(new java.awt.Color(161, 62, 151));
        fillButton.setText("Заполнить");
        fillButton.setToolTipText("Заполнить таблицу данными");
        fillButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fillButtonMouseClicked(evt);
            }
        });

        saveButton.setBackground(new java.awt.Color(161, 62, 151));
        saveButton.setText("Сохранить");
        saveButton.setToolTipText("Сохранить файл");
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveButtonMouseClicked(evt);
            }
        });

        loadButton.setBackground(new java.awt.Color(161, 62, 151));
        loadButton.setText("Загрузить");
        loadButton.setToolTipText("Загрузить файл");
        loadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(upperBorderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lowerBorderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stepLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lowerBorderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(upperBorderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteEntryButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addEntryButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(getResultButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cleanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(fillButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEntryButton)
                    .addComponent(lowerBorderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lowerBorderLabel))
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteEntryButton)
                    .addComponent(upperBorderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(upperBorderLabel))
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getResultButton)
                    .addComponent(stepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stepLabel))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cleanButton)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(fillButton)
                        .addComponent(loadButton)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addEntryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addEntryButtonMouseClicked
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        
        String temp = lowerBorderTextField.getText();
        if (temp.isEmpty()) return;
        double low = Double.parseDouble(temp);
        
        temp = upperBorderTextField.getText();
        if (temp.isEmpty()) return;
        double high = Double.parseDouble(temp);
        
        temp = stepTextField.getText();
        if (temp.isEmpty()) return;
        double step = Double.parseDouble(temp);
        
        RecIntegral info;
        try {
            info = new RecIntegral(low, high, step);
        } catch (RangeException exception) {
            return;
        }
        
        model.addRow(new Object[] {low, high, step, 0});
        collection.add(info);
    }//GEN-LAST:event_addEntryButtonMouseClicked

    private void deleteEntryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteEntryButtonMouseClicked
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        
        int index = infoTable.getSelectedRow();
        
        if (index == -1) return;
        
        model.removeRow(index);
        
        collection.remove(index);
    }//GEN-LAST:event_deleteEntryButtonMouseClicked

    private void getResultButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getResultButtonMouseClicked
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        
        int index = infoTable.getSelectedRow();

        if (index == -1) return;
        
        RecIntegral integral = collection.get(index);
        
        NewThread thread = new NewThread(model, integral, index);
        thread.start();
        //double resNum = integral.Result();
        //Object resObj = resNum;
        //model.setValueAt(resObj, index, 3);
    }//GEN-LAST:event_getResultButtonMouseClicked

    private void cleanButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cleanButtonMouseClicked
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_cleanButtonMouseClicked

    private void fillButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fillButtonMouseClicked
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)infoTable.getModel();
        
        int count = collection.size();
        if (count == 0) return;
        
        model.setRowCount(0);

        for (int i = 0; i < count; i++) {
            RecIntegral integral = collection.get(i);
            double[] integralFields = integral.GetResultFields();
            model.addRow(new Object[] {integralFields[1], integralFields[2], integralFields[3], integralFields[0]});
        }
    }//GEN-LAST:event_fillButtonMouseClicked

    private void saveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseClicked
        saveMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_saveButtonMouseClicked

    private void loadButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadButtonMouseClicked
        collection.clear();
        loadMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_loadButtonMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEntryButton;
    private javax.swing.JButton cleanButton;
    private javax.swing.JButton deleteEntryButton;
    private javax.swing.JButton fillButton;
    private javax.swing.JButton getResultButton;
    private javax.swing.JTable infoTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton loadButton;
    private javax.swing.JLabel lowerBorderLabel;
    private javax.swing.JTextField lowerBorderTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel stepLabel;
    private javax.swing.JTextField stepTextField;
    private javax.swing.JLabel upperBorderLabel;
    private javax.swing.JTextField upperBorderTextField;
    // End of variables declaration//GEN-END:variables
}
