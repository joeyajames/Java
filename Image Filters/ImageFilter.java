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
			//display(img);
			img = toGrayScale(img);
			//img = toGrayScale2(img);
			display(img);
			img = pixelate(img);
			//img = pixelate2(img, 3);
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
		int rgb=0, r=0, g=0, b=0;
		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				rgb = (int)(img.getRGB(x, y));
				r = ((rgb >> 16) & 0xFF);
				g = ((rgb >> 8) & 0xFF);
				b = (rgb & 0xFF);
				rgb = (int)((r+g+b)/3);
				//rgb = (int)(0.299 * r + 0.587 * g + 0.114 * b);
				rgb = (255<<24) | (rgb<<16) | (rgb<<8) | rgb;
				grayImage.setRGB(x,y,rgb);
			}
		}
		return grayImage;
	}

	// apply 2x2 pixelation to a grayscale image
	public static BufferedImage pixelate (BufferedImage img) {
		BufferedImage pixImg = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0, p=0;
		for (int y=0; y<img.getHeight()-2; y+=2) {
			for (int x=0; x<img.getWidth()-2; x+=2) {
				pix = (int)((img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x+1, y)& 0xFF)
				+ (img.getRGB(x, y+1)& 0xFF)
				+ (img.getRGB(x+1, y+1)& 0xFF))/4;
				p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
				pixImg.setRGB(x,y,p);
				pixImg.setRGB(x+1,y,p);
				pixImg.setRGB(x,y+1,p);
				pixImg.setRGB(x+1,y+1,p);
			}
		}
		return pixImg;
	}

	// apply nxn pixelation to a grayscale image
	public static BufferedImage pixelate2 (BufferedImage img, int n) {
		BufferedImage pixImg = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0, p=0;
		for (int y=0; y<img.getHeight()-n; y+=n) {
			for (int x=0; x<img.getWidth()-n; x+=n) {
				for (int a=0; a<n; a++) {
					for (int b=0; b<n; b++) {
						pix += (img.getRGB(x+a, y+b)& 0xFF);
					}
				}
				pix = (int)(pix/n/n);
				for (int a=0; a<n; a++) {
					for (int b=0; b<n; b++) {
						p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
						pixImg.setRGB(x+a,y+b,p);
					}
				}
				pix = 0;
			}
		}
		return pixImg;
	}	

}











