/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphique;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.annotation.processing.FilerException;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author imane
 */
public class MyButton extends JButton {

    protected String texte, path;
    protected ImageIcon image;
    
    public MyButton(String texte, String path) {
        try {
            image = new ImageIcon(ImageIO.read(new File(path)));
        } catch (FilerException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        Image img = image.getImage();
        Image newimg = img.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
        image = new ImageIcon(newimg);
        this.setText(texte);
        this.setIcon(image);
        this.setHorizontalTextPosition(AbstractButton.CENTER);
        this.setVerticalTextPosition(AbstractButton.BOTTOM);
    }
    
    
}