import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;

public class ImageFilter {

	public static void main(String[] args) {
		File file = new File("photo.png");
		BufferedImage img = null;

		try { img = ImageIO.read(file); } 
		catch (IOException e) { e.printStackTrace(System.out); }

		if (img != null) {
			display(img);
			img = toGrayScale2(img);
			display(img);
		}
	}

	// display image in a JPanel popup
	public static void display (BufferedImage img) {
		System.out.println("  Displaying image.");
		JFrame frame = new JFrame();
	    JLabel label = new JLabel();
		frame.setSize(img.getWidth(), img.getHeight());
		label.setIcon(new ImageIcon(img));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	// convert image to grayscale
	public static BufferedImage toGrayScale (BufferedImage img) {
		System.out.println("  Converting to GrayScale.");
		BufferedImage grayImage = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = grayImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return grayImage;
	}
	public static BufferedImage toGrayScale2 (BufferedImage img) {
		System.out.println("  Converting to GrayScale2.");
		BufferedImage grayImage = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int p=0, rgb=0, r=0, g=0, b=0;
		for (int y=0; y<img.getHeight()-1; y++) {
			for (int x=0; x<img.getWidth()-1; x++) {
				rgb = (int)(img.getRGB(x, y));
				r = ((rgb >> 16) & 0xFF);
				g = ((rgb >> 8) & 0xFF);
				b = (rgb & 0xFF);
				p = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
				p = (255<<24) | (p<<16) | (p<<8) | p;
				grayImage.setRGB(x,y,p);
			}
		}
		return grayImage;
	}
}











