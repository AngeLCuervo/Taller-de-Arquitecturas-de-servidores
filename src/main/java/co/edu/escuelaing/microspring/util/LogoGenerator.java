package co.edu.escuelaing.microspring.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utility class for generating the MicroSpring logo PNG image.
 * Run this class once to generate the logo.png file in the static resources.
 * 
 * @author Angel Cuervo
 */
public class LogoGenerator {
    
    public static void main(String[] args) {
        generateLogo();
    }
    
    /**
     * Generates the MicroSpring logo as a PNG file.
     */
    public static void generateLogo() {
        int size = 200;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Create gradient background circle
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(102, 126, 234),  // #667eea
            size, size, new Color(118, 75, 162)  // #764ba2
        );
        
        g2d.setPaint(gradient);
        g2d.fill(new Ellipse2D.Double(10, 10, size - 20, size - 20));
        
        // Draw text "μS"
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "μS";
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (size - textWidth) / 2, 90);
        
        // Draw "MicroSpring" text
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g2d.getFontMetrics();
        String subtitle = "MicroSpring";
        textWidth = fm.stringWidth(subtitle);
        g2d.drawString(subtitle, (size - textWidth) / 2, 130);
        
        g2d.dispose();
        
        // Save the image
        try {
            File outputDir = new File("src/main/resources/static/images");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            File outputFile = new File(outputDir, "logo.png");
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("Logo generated successfully at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to generate logo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
