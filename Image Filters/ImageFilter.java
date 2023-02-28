/*******************************************************************************
 * Filter Algorithms for Java BufferedImage
 * (c) 2023, Joe James
 * 
 * This program loads an image file. It has methods to:  
 *  - display the image in a JPanel popup window; 
 *  - convert the image to grayscale;
 *  - scale a grayscale image to a different size;
 *  - pixelate a grayscale image;
 *  - apply a Gaussian blur to a grayscale image;
 *  - detect edges of a grayscale image;
 *  - brighten a color image.
 * 
 * These files are mainly intended to accompany my series of YouTube tutorial 
 * videos here, https://www.youtube.com/user/joejamesusa and are mainly 
 * intended for educational purposes. You are invited to subscribe to my 
 * video channel, and to download and use any code in this Java 
 * repository, according to the MIT License. Feel free to post any comments 
 * on my YouTube channel. 
 * 
 * ****************************************************************************/

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
		//File file = new File("License Plate Photos/ca_10.jpeg");
		BufferedImage img = null;

		try { img = ImageIO.read(file); } 
		catch (IOException e) { e.printStackTrace(System.out); }

		if (img != null) {
			display(img);
			//img = brighten(img);
			img = toGrayScale(img);
			//img = toGrayScale2(img);
			//display(img);
			//img = pixelate(img);
			//img = pixelate2(img, 3);
			//img = resize(img, 150);
			//img = blur(img);
			img = blur(blur(img));
			img = heavyblur(img);
			img = detectEdges(img);
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

	// scale a grayscale image
	public static BufferedImage resize (BufferedImage img, int newHeight) {
		System.out.println("  Scaling image.");
		double scaleFactor = (double) newHeight/img.getHeight();
		BufferedImage scaledImg = new BufferedImage(
			(int)(scaleFactor*img.getWidth()), newHeight, BufferedImage.TYPE_BYTE_GRAY);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(img, scaledImg);
	}

	// apply 3x3 Gaussian blur to a grayscale image
	public static BufferedImage blur (BufferedImage img) {
		BufferedImage blurImg = new BufferedImage(
			img.getWidth()-2, img.getHeight()-2, BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0;
		for (int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth(); x++) {
				pix = (int)(4*(img.getRGB(x+1, y+1)& 0xFF)
				+ 2*(img.getRGB(x+1, y)& 0xFF)
				+ 2*(img.getRGB(x+1, y+2)& 0xFF)
				+ 2*(img.getRGB(x, y+1)& 0xFF)
				+ 2*(img.getRGB(x+2, y+1)& 0xFF)
				+ (img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x, y+2)& 0xFF)
				+ (img.getRGB(x+2, y)& 0xFF)
				+ (img.getRGB(x+2, y+2)& 0xFF))/16;
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
				blurImg.setRGB(x,y,p);
			}
		}
		return blurImg;
	}

	// apply 5x5 Gaussian blur to a grayscale image
	public static BufferedImage heavyblur (BufferedImage img) {
		BufferedImage blurImg = new BufferedImage(
			img.getWidth()-4, img.getHeight()-4, BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0;
		for (int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth(); x++) {
				pix = (int)(
				10*(img.getRGB(x+3, y+3)& 0xFF)
				+ 6*(img.getRGB(x+2, y+1)& 0xFF)
				+ 6*(img.getRGB(x+1, y+2)& 0xFF)
				+ 6*(img.getRGB(x+2, y+3)& 0xFF)
				+ 6*(img.getRGB(x+3, y+2)& 0xFF)
				+ 4*(img.getRGB(x+1, y+1)& 0xFF)
				+ 4*(img.getRGB(x+1, y+3)& 0xFF)
				+ 4*(img.getRGB(x+3, y+1)& 0xFF)
				+ 4*(img.getRGB(x+3, y+3)& 0xFF)
				+ 2*(img.getRGB(x, y+1)& 0xFF)
				+ 2*(img.getRGB(x, y+2)& 0xFF)
				+ 2*(img.getRGB(x, y+3)& 0xFF)
				+ 2*(img.getRGB(x+4, y+1)& 0xFF)
				+ 2*(img.getRGB(x+4, y+2)& 0xFF)
				+ 2*(img.getRGB(x+4, y+3)& 0xFF)
				+ 2*(img.getRGB(x+1, y)& 0xFF)
				+ 2*(img.getRGB(x+2, y)& 0xFF)
				+ 2*(img.getRGB(x+3, y)& 0xFF)
				+ 2*(img.getRGB(x+1, y+4)& 0xFF)
				+ 2*(img.getRGB(x+2, y+4)& 0xFF)
				+ 2*(img.getRGB(x+3, y+4)& 0xFF)
				+ (img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x, y+2)& 0xFF)
				+ (img.getRGB(x+2, y)& 0xFF)
				+ (img.getRGB(x+2, y+2)& 0xFF))/74;
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
				blurImg.setRGB(x,y,p);
			}
		}
		return blurImg;
	}

	// detect edges of a grayscale image using Sobel algorithm 
	// (for best results, apply blur before finding edges)
	public static BufferedImage detectEdges (BufferedImage img) {
		int h = img.getHeight(), w = img.getWidth(), threshold=30, p = 0;
		BufferedImage edgeImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		int[][] vert = new int[w][h];
		int[][] horiz = new int[w][h];
		int[][] edgeWeight = new int[w][h];
		for (int y=1; y<h-1; y++) {
			for (int x=1; x<w-1; x++) {
				vert[x][y] = (int)(img.getRGB(x+1, y-1)& 0xFF + 2*(img.getRGB(x+1, y)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
					- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x-1, y)& 0xFF) - img.getRGB(x-1, y+1)& 0xFF);
				horiz[x][y] = (int)(img.getRGB(x-1, y+1)& 0xFF + 2*(img.getRGB(x, y+1)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
					- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x, y-1)& 0xFF) - img.getRGB(x+1, y-1)& 0xFF);
				edgeWeight[x][y] = (int)(Math.sqrt(vert[x][y] * vert[x][y] + horiz[x][y] * horiz[x][y]));
				if (edgeWeight[x][y] > threshold)
					p = (255<<24) | (255<<16) | (255<<8) | 255;
				else 
					p = (255<<24) | (0<<16) | (0<<8) | 0; 
				edgeImg.setRGB(x,y,p);
			}
		}
		return edgeImg;
	}

	// brighten color image by a percentage 
	public static BufferedImage brighten (BufferedImage img, int percentage) {
		int r=0, g=0, b=0, rgb=0, p=0;
		int amount = (int)(percentage * 255 / 100); // rgb scale is 0-255, so 255 is 100%
		BufferedImage newImage = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y=0; y<img.getHeight(); y+=1) {
			for (int x=0; x<img.getWidth(); x+=1) {
				rgb = img.getRGB(x, y);
				r = ((rgb >> 16) & 0xFF) + amount;
				g = ((rgb >> 8) & 0xFF) + amount;
				b = (rgb & 0xFF) + amount;
				if (r>255) r=255;
				if (g>255) g=255;
				if (b>255) b=255;
				p = (255<<24) | (r<<16) | (g<<8) | b;
				newImage.setRGB(x,y,p);
			}
		}
		return newImage;
	}

}











