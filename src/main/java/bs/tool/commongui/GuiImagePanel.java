package bs.tool.commongui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * How to add an image to a JPanel?
 * https://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 */
public class GuiImagePanel extends JPanel {

    private BufferedImage image;
    private int imageWidth;
    private int imageHeight;

    public GuiImagePanel() {
    }

    public GuiImagePanel(String imagePath) {
        setImage(imagePath);
    }

    public GuiImagePanel(int imageWidth, int imageHeight) {
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
    }

    public GuiImagePanel(String imagePath, int imageWidth, int imageHeight) {
        this(imageWidth, imageHeight);
        setImage(imagePath);
    }

    private void readImage(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            GuiUtils.log(e);
        }
    }

    public void setImage(String imagePath) {
        readImage(imagePath);
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, imageWidth, imageHeight, this);
        }
    }

    public void repaint(String imagePath) {
        setImage(imagePath);
        super.repaint();
    }

}