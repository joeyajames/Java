// Automatic License Plate Recognition (ALPR)
// (c) 2023, Joe James

/*******************************************************************************
 * This Automatic License Plate Reader program is mainly intended to 
 * accompany my series of YouTube tutorial videos here, 
 * https://www.youtube.com/user/joejamesusa and is mainly 
 * intended for educational purposes. You are invited to subscribe to my 
 * video channel, and to download and use any code in this Java 
 * repository, according to the MIT License. Feel free to post any comments 
 * on my YouTube channel. 
 * 
 * ****************************************************************************/

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.stream.*;
import java.lang.Math;

public class ALPR {
	// Penitentiary Gothic font, 20x43 pix = 860 pixels
	int[] CHARSIZE = {20, 43};
	int NUMPIXELS = CHARSIZE[0] * CHARSIZE[1];
	ArrayList<String> trainLabels = new ArrayList<>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"));
	ArrayList<Integer[]> trainData = new ArrayList<>(trainLabels.size());

	public static void main(String[] args) {
		ALPR alpr = new ALPR();
		File[] files = new File[4];
		files[0] = new File("Training Data/letters-1 image.png");
		files[1] = new File("Training Data/letters-2 image.png");
		files[2] = new File("Training Data/letters-3 image.png");
		files[3] = new File("Training Data/numbers image.png");
		alpr.train(files);

		//File file = new File("License Plate Photos/ca_aug2012.png");
		//File file = new File("License Plate Photos/ca_12.jpeg");
		File file = new File("License Plate Photos/ca_10.jpeg");
		//File file = new File("License Plate Photos/ca2004.jpg");
		//File file = new File("License Plate Photos/ca2005.jpg");
		//File file = new File("License Plate Photos/ca2006.jpg");
		//File file = new File("License Plate Photos/ca_blue_1.jpeg");
		//File file = new File("License Plate Photos/tx_4.jpeg");
		//File file = new File("License Plate Photos/ny_1.jpeg");
		//File file = new File("License Plate Photos/fl_1.jpeg");
		alpr.readPlate(file);
	}

	public void readPlate(File file) {
		System.out.println("Loading file.");
		BufferedImage img = null;
		int height1 = 200;
		ArrayList<Integer[]> testData = new ArrayList<>();
		try { img = ImageIO.read(file); } 
		catch (IOException e) { e.printStackTrace(System.out); }

		if (img != null) {
			display(img);
			img = toGrayScale(img);
			display(img);
			img = getScaledImage(img, height1);

			// Crop out top and bottom margins
			int[] vertBounds = getTopBottom(img);
			img = img.getSubimage(0, vertBounds[0], img.getWidth(), vertBounds[1]-vertBounds[0]);

			// Scale image to CHARSIZE height
			img = getScaledImage(img, CHARSIZE[1]);
			System.out.println("  Scaled image: " + img.getHeight() + "x" + img.getWidth());
			display(img);

			// boost contrast
			img = boostContrast(img);

			ArrayList<Integer> edges = getEdges(img);
			System.out.println(" Found edges: " + edges);
			/*display(img.getSubimage(edges.get(0)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(1)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(2)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(3)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(4)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(5)+2, 0, CHARSIZE[0], img.getHeight()));
			display(img.getSubimage(edges.get(6)+2, 0, CHARSIZE[0], img.getHeight()));*/

			// add character data to ArrayList for comparisons to trainData
			testData = imgToArray(img, testData, edges);

			// identify characters in testData
			String plateNum = identify(testData);
			System.out.println(plateNum);
		}
	}

	// receives an image of a plate, splits it into digits
	public void train(File[] files) {
		BufferedImage img = null;
		
		for (int i=0; i<files.length; i++) {
		try {
			img = ImageIO.read(files[i]);
		} catch (IOException e) { }

		if (img != null) {
			//System.out.println("  Training Image Loaded " + img.getHeight() + "x" + img.getWidth());
			// convert to grayscale img
			img = toGrayScale(img);

			// find top and bottom edges of characters to trim whitespace
			int[] rowSums = new int[img.getHeight()];
			for (int y=0; y<img.getHeight(); y++) {
				for (int x=0; x<img.getWidth(); x++) {
					rowSums[y] += img.getRGB(x, y)& 0xFF;
				}
			}

			int[] vertBounds = new int[2];
			vertBounds = getBounds(vertBounds, rowSums);
			System.out.println("  Top row = " + ++vertBounds[0] + ".  Bottom row = " + vertBounds[1] + ".");
			
			// Crop out top and bottom white margins
			img = img.getSubimage(0, vertBounds[0], img.getWidth(), vertBounds[1]-vertBounds[0]);

			// Scale image to CHARSIZE height
			img = getScaledImage(img, CHARSIZE[1]);
			//System.out.println("  Height = " + img.getHeight() + ".  Width = " + img.getWidth() + ".");
			//display(img);

			// get edges between characters (find left edge of each character)
			ArrayList<Integer> edges = getEdges(img);
			
			// add grayscale data for each character to training data set
			trainData = imgToArray(img, trainData, edges);
			
			//int[] intArray = Arrays.stream(trainData.get(trainData.size()-1)).mapToInt(Integer::intValue).toArray();
			//display(intArray, CHARSIZE[0], CHARSIZE[1]);
		}
		}
		// add a few hard-coded chars to training data 
		addMoreTrainingData();

		System.out.println(trainData.size() + " characters loaded.");
	}

	// convert imageto grayscale 
	public BufferedImage toGrayScale(BufferedImage img) {
		System.out.println("  Converting to GrayScale.");
		BufferedImage grayImage = new BufferedImage(
			img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = grayImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return grayImage;
	}

	// display image in a JPanel popup
	public void display (BufferedImage img) {
		//System.out.println("  Displaying image.");
		JFrame frame = new JFrame();
	    JLabel label = new JLabel();
		frame = new JFrame();
		frame.setSize(img.getWidth(), img.getHeight());
		label.setIcon(new ImageIcon(img));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	// display image in a JPanel popup, from int array
	private void display(int[] pixels, int width, int height) {
		System.out.println("  Printing image for array length: " + pixels.length + ", " + width + "x" + height);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = img.getRaster();
		raster.setPixels(0, 0, width, height, pixels);
		display(img);
	}

	// scale image
	private BufferedImage getScaledImage (BufferedImage img, int newHeight) {
		System.out.println("  Scaling image.");
		double scaleFactor = (double) newHeight/img.getHeight();
		BufferedImage scaledImg = new BufferedImage(
			(int)(scaleFactor*img.getWidth()), newHeight, BufferedImage.TYPE_BYTE_GRAY);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(img, scaledImg);
	}

	// convert grayscale image to BW
	private BufferedImage boostContrast(BufferedImage img) {
		// compute average pixel darkness
		int avg = 0;
		for (int y=0; y<img.getHeight(); y++) {
				for (int x=0; x<img.getWidth(); x++) {
					avg += img.getRGB(x, y)& 0xFF;
				}
			}
		avg /= img.getHeight() * img.getWidth();

		// convert grayscale pixels in img to BW
		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				int p = img.getRGB(x, y)& 0xFF;
				if (p>avg)
					p = (255<<24) | (255<<16) | (255<<8) | 255;
				else
					p = (255<<24) | (0<<16) | (0<<8) | 0;
				img.setRGB(x, y, p);
			}
		}
		return img;
	}

	private Integer[] boostContrast(Integer[] data) { 
		// compute average pixel darkness
		int avg = 0;
		for (int i=0; i<data.length; i++) {
			avg += data[i];
		}
		avg /= data.length;

		// convert grayscale to BW
		for (int i=0; i<data.length; i++) {
			if (data[i]>avg)
				data[i] = 255;
			else
				data[i] = 0;
		}
		//int[] bwChar = Arrays.stream(data).mapToInt(Integer::intValue).toArray();
		//display(bwChar, CHARSIZE[0], CHARSIZE[1]);
		return data;
	}

	// use trainData to identify each character in testData
	private String identify(ArrayList<Integer[]> testData) { 
		System.out.println("  Identifying characters in plate.");
		String plateNum = "";
		for (Integer[] testChar : testData) {
			int[] distances = new int[trainData.size()];
			for (int t=0; t<trainData.size(); t++) {
				for (int p=0; p<NUMPIXELS; p++) {
					distances[t] += Math.abs(trainData.get(t)[p] - testChar[p]);
				}
			}

			//pick shortest distance in distances
			//for (int d : distances)
				//System.out.print(d + " ");
			int minVal=distances[0], minIndex=0;
			for (int i=0; i<distances.length; i++) {
				//System.out.println(trainLabels.get(i) + " " + distances[i]);
				if (distances[i] < minVal) {
					minVal = distances[i];
					minIndex = i;
				}
			}
			plateNum += trainLabels.get(minIndex);
		/*	int[] trainCharArray = Arrays.stream(trainData.get(minIndex)).mapToInt(Integer::intValue).toArray();
			display(trainCharArray, CHARSIZE[0], CHARSIZE[1]);
			int[] testCharArray = Arrays.stream(testChar).mapToInt(Integer::intValue).toArray();
			display(testCharArray, CHARSIZE[0], CHARSIZE[1]); */
		} /*
		for (Integer[] testChar : testData) {
			for (Integer i : testChar)
				System.out.print(i + ",");
			System.out.println();
		}*/
		return plateNum;
	}

	// append hard-coded character data to trainingData to improve accuracy
	private void addMoreTrainingData() { 
		// Need to hard code in training data for 7, T, Y, V, 8, 5, B, 2, Z, E, F using int[] from a test image to improve accuracy
		Integer[] seven = {255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255};
		trainData.add(seven);
		trainLabels.add("7");
		Integer[] why = {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,0,0,0,0,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,0,0,0,0,0,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,0,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255};
		trainData.add(why);
		trainLabels.add("Y");
		Integer[] eff = {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255};
		trainData.add(eff);
		trainLabels.add("F");
		Integer[] two = {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,0,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		trainData.add(two);
		trainLabels.add("2"); 
		Integer[] bee = {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,255,255,255,255,255};
		trainData.add(bee);
		trainLabels.add("B"); /*
		Integer[] two = {};
		trainData.add(two);
		trainLabels.add("2");
		Integer[] two = {};
		trainData.add(two);
		trainLabels.add("2");
		Integer[] two = {};
		trainData.add(two);
		trainLabels.add("2");
		Integer[] two = {};
		trainData.add(two);
		trainLabels.add("2");*/
		return;
	}

	// find top and bottom edges of characters on plate
	private int[] getTopBottom(BufferedImage img) {
		// calculate row sums to find top and bottom edges of characters
		int[] rowSums = new int[img.getHeight()];
		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				rowSums[y] += img.getRGB(x, y)& 0xFF;
			}
		}

		// find top and bottom bounds of plate numbers
		int[] deltaGray = new int[200];
		for (int i=0; i<img.getHeight()-2; i++) {
			deltaGray[i] = (rowSums[i] - rowSums[i+2])/1000;
		}
		int[] vertBounds = new int[2];
		for (int i=120; i>50; i--) {
			if (deltaGray[i] > 5) {
				vertBounds[0] = i-2;
				break;
			}
		}
		for (int i=120; i<180; i++) {
			if (deltaGray[i] < -5) {
				vertBounds[1] = i+2;
				break;
			}
		}
		System.out.println("Vert bounds: " + vertBounds[0] + " " + vertBounds[1]);
		return vertBounds;
	}

	// add grayscale data for each character to ArrayList
	private ArrayList<Integer[]> imgToArray (BufferedImage img, ArrayList<Integer[]> data, ArrayList<Integer> edges) {
		for (Integer e : edges) {
			data.add(new Integer[NUMPIXELS]);
			for (int j=0; j<img.getHeight(); j++) {
				for (int i=0; i<CHARSIZE[0]; i++) {
					data.get(data.size() - 1)[j*CHARSIZE[0] + i] = img.getRGB(e + i, j)& 0xFF;
				}
			}
		}
		return data;
	}

	// find upper and lower whitespace bounds given a grayscale sums array
	private int[] getBounds (int[] bounds, int[] sums) {
		int upper = 0, lower = 0;
		boolean upperFound = false;
		for (int i=bounds[0]; i<sums.length; i++) {
			if ((upper <= sums[i]) && !(upperFound)) {
				upper = sums[i];
				bounds[0] = i;
			}
			else {
				upperFound = true;
				if (lower < sums[i]) {
					lower = sums[i];
					bounds[1] = i;
				}
			}
		}
		return bounds;
	}

	// find gaps between characters
	private ArrayList<Integer> getEdges(BufferedImage img) {
		System.out.println("  Getting edges between characters.");
		// Locate whitespace delimiters between characters (left & rt bounds for each character)
		int[] colSums = new int[img.getWidth()];
		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				colSums[x] += (img.getRGB(x, y)& 0xFF);
			}
		}

		//for (int i=0; i<colSums.length-1; i++)
		//	System.out.print(colSums[i] + " ");

		// find gaps and locate center of gaps
		ArrayList<Integer> edges = new ArrayList<>();
		int col = 0; 
		while (col<colSums.length-1) {
			int whiteCols = 0;
			while ((col+whiteCols<colSums.length-1) & (colSums[col+whiteCols] > 250*CHARSIZE[1])) {
				whiteCols += 1;
			}
			if ((whiteCols > 1) & (col+whiteCols+CHARSIZE[0] < img.getWidth()))
				edges.add((int)(col + whiteCols -1));
			col += whiteCols + 1;
		}
		//System.out.println(edges);

		return edges; 
	}

}

