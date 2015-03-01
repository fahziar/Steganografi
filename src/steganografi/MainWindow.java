/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Fahziar
 */
public class MainWindow extends javax.swing.JApplet {

    /**
     * Initializes the applet MainWindow
     */
    
    BufferedImage image1;
    BufferedImage image2;
    NineDiffStego nineDiffStego;
    @Override
    public void init() {
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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnLoadGambar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmbAlgoritma = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtKapasitas = new javax.swing.JTextField();
        btnExtractData = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        btnPutData = new javax.swing.JButton();
        btnPSNR = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        btnLoadGambar.setText("Load Gambar");
        btnLoadGambar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLoadGambarMouseClicked(evt);
            }
        });
        btnLoadGambar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadGambarActionPerformed(evt);
            }
        });

        jLabel1.setText("Algoritma: ");

        cmbAlgoritma.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LSB", "5 Pixel Diff", "9 Pixel Diff" }));

        jLabel2.setText("Kapasitas");

        txtKapasitas.setEditable(false);

        btnExtractData.setText("Extract Data");
        btnExtractData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnExtractDataMouseClicked(evt);
            }
        });
        btnExtractData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtractDataActionPerformed(evt);
            }
        });

        jLabel3.setText("Password: ");

        btnPutData.setText("Put Data");
        btnPutData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPutDataMouseClicked(evt);
            }
        });

        btnPSNR.setText("PSNR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPSNR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassword))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnLoadGambar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPutData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtKapasitas))
                            .addComponent(btnExtractData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbAlgoritma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(cmbAlgoritma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtKapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLoadGambar))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExtractData)
                    .addComponent(btnPutData))
                .addGap(18, 18, 18)
                .addComponent(btnPSNR)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadGambarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadGambarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoadGambarActionPerformed

    private void btnExtractDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtractDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExtractDataActionPerformed

    private void btnLoadGambarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoadGambarMouseClicked
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        
        fc.setDialogTitle("Open Image");
        fc.showOpenDialog(this);
        try{
            image1 = ImageIO.read(fc.getSelectedFile().getAbsoluteFile());
            image2 = ImageIO.read(fc.getSelectedFile().getAbsoluteFile());
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Gagal membuka file: " + e.getMessage());
        }
        
        switch (cmbAlgoritma.getSelectedIndex())
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                nineDiffStego = new NineDiffStego();
                nineDiffStego.setImage(image1);
                nineDiffStego.setImage2(image2);
                txtKapasitas.setText(Integer.toString(nineDiffStego.getCapacity()));
                break;
        }
        
    }//GEN-LAST:event_btnLoadGambarMouseClicked

    private void btnPutDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPutDataMouseClicked
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        
        fc.setDialogTitle("Open File to Hide");
        fc.showOpenDialog(this);
        
        try
        {
            byte[] inputData = Files.readAllBytes(Paths.get(fc.getSelectedFile().getAbsolutePath()));
            
            //Encrypt
            byte[] encrypted = VigenereCipher.vigenereExtendedEncryptBytes(txtPassword.getText(), inputData);
            ByteBuffer bf = ByteBuffer.allocate(10 + encrypted.length );
            
            switch (cmbAlgoritma.getSelectedIndex())
            {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    bf.putChar('t');
                    bf.putChar('x');
                    bf.putChar('t');
                    bf.putInt(encrypted.length);
                    bf.put(encrypted);
                    nineDiffStego.stego("hello", bf.array());
                    fc.setDialogTitle("Save Stego Image");
                    fc.showSaveDialog(this);
                    ImageIO.write(nineDiffStego.getImage(), "bmp", fc.getSelectedFile().getAbsoluteFile());
                    break;
            }
        } catch (Exception e)
        {
            
        }
    }//GEN-LAST:event_btnPutDataMouseClicked

    private void btnExtractDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExtractDataMouseClicked
        // TODO add your handling code here:
        try {        
        switch (cmbAlgoritma.getSelectedIndex())
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                nineDiffStego.setImage(image1);
                nineDiffStego.setImage2(image2);
                byte[] metadata = nineDiffStego.unStego("hello", 80);
            
                ByteBuffer bf = ByteBuffer.allocate(11);
                bf.put(metadata);
                bf.flip();
                char a, b, c;
                int size;
                //bf.flip();
                a = bf.getChar();
                b = bf.getChar();
                c = bf.getChar();

                size = bf.getInt();
                byte[] notStegoed = nineDiffStego.unStego("hello", (size + 10) * 8);
                bf.clear();
                ByteBuffer bf2 = ByteBuffer.allocate(notStegoed.length);
                bf2.put(notStegoed);
                bf2.flip();
                FileOutputStream fos = new FileOutputStream("D://halo2.txt");

                byte[] out = new byte[size];

                int i;
                for(i=0; i<3;i++)
                {
                    bf2.getChar();
                }
                bf2.getInt();
                bf2.get(out);
                fos.write(VigenereCipher.vigenereExtendedDecryptBytes(txtPassword.getText(), out));
                break;
        }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuka file: " + e.getMessage());
        }
    }//GEN-LAST:event_btnExtractDataMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExtractData;
    private javax.swing.JButton btnLoadGambar;
    private javax.swing.JButton btnPSNR;
    private javax.swing.JButton btnPutData;
    private javax.swing.JComboBox cmbAlgoritma;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField txtKapasitas;
    private javax.swing.JTextField txtPassword;
    // End of variables declaration//GEN-END:variables
}
