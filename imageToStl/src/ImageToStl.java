

import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class ImageToStl {

	static double minThickness = 1.0;
	static double maxThickness = 3.0;
	static double xyScale = 10.0;
	static double fallOffRadius = 4.0; //max of 16
	
	static int min(int i1, int i2) {
		return i1 < i2 ? i1 : i2;
	}

	static int max(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}

	static BufferedImage distanceMap(BufferedImage img) {

		BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		for (int x = 0; x < img.getWidth(); x++) {

			System.out.println(x);

			for (int y = 0; y < img.getHeight(); y++) {

				int minDistanceSquared = Integer.MAX_VALUE;
				for (int h = max(0, x - 16); h <= min(x + 16, img.getWidth() - 1); h++) {

					for (int k = max(0, y - 16); k <= min(y + 16, img.getHeight() - 1); k++) {

						if ((img.getRGB(h, k) & 0xffffff) == 0) {
							minDistanceSquared = Math.min(minDistanceSquared, (x - h) * (x - h) + (y - k) * (y - k));
						}

					}
				}

				double strength = 0.5 + 0.5 * Math.cos(Math.PI / fallOffRadius * Math.min(Math.sqrt(minDistanceSquared), fallOffRadius));
				int iStrength = (int) (255 * strength);
				newImage.setRGB(x, y, iStrength << 16 | iStrength << 8 | iStrength);
			}
		}

		return newImage;
	}

	/**
	 * precondition: newX <= img.width; newY <= img.height
	 */
	public static BufferedImage scaleImage(BufferedImage img, int newX, int newY) {
		BufferedImage newImage = new BufferedImage(newX, newY, BufferedImage.TYPE_INT_RGB);

		double sx = (double) img.getWidth() / newX;
		double sy = (double) img.getHeight() / newY;

		for (int x = 0; x < newX; x++) {
			for (int y = 0; y < newY; y++) {
				newImage.setRGB(x, y, img.getRGB(min((int) (sx * x + 0.5), img.getWidth() - 1), min((int) (sy * y + 0.5), img.getHeight() - 1)));
			}
		}

		return newImage;
	}

	
	public static BufferedImage mirrorImage(BufferedImage img){
	    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    tx.translate(-img.getWidth(null), 0);
	    AffineTransformOp op = new AffineTransformOp(tx,
	        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    return op.filter(img, null);
	}
	
	/**
	 * 
	 * @param img
	 * @param x
	 * @param y
	 * @return normal of point at (x,y) in image, vector is not actually normalized!!!
	 */
	public static Vector3d getNormal(BufferedImage img, int x, int y) {

		double vx;
		double vy;

		if (x == 0) {
			vx = 2.0 * ((img.getRGB(x, y) & 0xff) - (img.getRGB(x + 1, y) & 0xff));
		} else if (x == img.getWidth() - 1) {
			vx = 2.0 * ((img.getRGB(x - 1, y) & 0xff) - (img.getRGB(x, y) & 0xff));
		} else {
			vx = ((img.getRGB(x - 1, y) & 0xff) - (img.getRGB(x + 1, y) & 0xff));
		}

		if (y == 0) {
			vy = 2.0 * ((img.getRGB(x, y) & 0xff) - (img.getRGB(x, y + 1) & 0xff));
		} else if (y == img.getHeight() - 1) {
			vy = 2.0 * ((img.getRGB(x, y - 1) & 0xff) - (img.getRGB(x, y) & 0xff));
		} else {
			vy = ((img.getRGB(x, y - 1) & 0xff) - (img.getRGB(x, y + 1) & 0xff));
		}

		Vector3d vec = new Vector3d(vx, vy, 2);
		return vec;

	}



	static double getThickness(BufferedImage img, int x, int y) {
		int h = (img.getRGB(min(x, img.getWidth()-1) , min(y, img.getHeight() - 1)) & 0xff);
		return Math.min((h / 255.0 * (maxThickness - minThickness)) * 1.05, maxThickness - minThickness) + minThickness;
	}

	public static void writeStl(BufferedImage img, String filename) throws IOException {

		int width = img.getWidth();
		int height = img.getHeight();

		Writer out = new BufferedWriter(new FileWriter(filename));

		out.write("solid SolidNameHere\n");

		out.write("facet normal 0.0 0.0 -1.0 \n");
		out.write("outer loop\n");
		out.write("vertex 0.0 0.0 0.0 \n");
		out.write("vertex " + xyScale * (width-1) + " 0.0 0.0 \n");
		out.write("vertex " + xyScale * (width-1) + " " + xyScale * (height-1) + " 0.0 \n");
		out.write("endloop\n");
		out.write("endfacet\n");

		out.write("facet normal 0.0 0.0 -1.0 \n");
		out.write("outer loop\n");
		out.write("vertex 0.0 0.0 0.0 \n");
		out.write("vertex " + xyScale * (width-1) + " " + xyScale * (height-1) + " 0.0 \n");
		out.write("vertex 0.0 " + xyScale * (height-1) + " 0.0 \n");
		out.write("endloop\n");
		out.write("endfacet\n");

		for (int x = 0; x < width - 1; x++) {
			for (int y = 0; y < height; y += height-1) {
				if (y == 0)
					out.write("facet normal 0.0 -1.0 0.0 \n");
				else
					out.write("facet normal 0.0 1.0 0.0 \n");

				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " 0.0 \n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " " + getThickness(img, x, y) + " \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * y + " " + getThickness(img, x + 1, y) + " \n");
				out.write("endloop\n");
				out.write("endfacet\n");

				if (y == 0)
					out.write("facet normal 0.0 -1.0 0.0 \n");
				else
					out.write("facet normal 0.0 1.0 0.0 \n");
				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " 0.0 \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * y + " " + getThickness(img, x+1, y) + " \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * y + " 0.0 \n");
				out.write("endloop\n");
				out.write("endfacet\n");
			}
		}
		out.flush();
		for (int y = 0; y < height - 1; y++) {
			for (int x = 0; x < width; x += width-1) {
				if (x == 0)
					out.write("facet normal -1.0 0.0 0.0 \n");
				else
					out.write("facet normal 1.0 0.0 0.0 \n");

				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " 0.0 \n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " " + getThickness(img, x, y) + " \n");
				out.write("vertex " + xyScale * x + " " + xyScale * (y + 1) + " " + getThickness(img, x, y+1) + " \n");
				out.write("endloop\n");
				out.write("endfacet\n");

				if (x == 0)
					out.write("facet normal 0.0 -1.0 0.0 \n");
				else
					out.write("facet normal 0.0 1.0 0.0 \n");
				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " 0.0 \n");
				out.write("vertex " + xyScale * x + " " + xyScale * (y + 1) + " " + getThickness(img, x, y+1) +" \n");
				out.write("vertex " + xyScale * x + " " + xyScale * (y + 1) + " 0.0 \n");
				out.write("endloop\n");
				out.write("endfacet\n");
			}
		}
		out.flush();
		for (int x = 0; x < width - 1; x++) {
			for (int y = 0; y < height - 1; y++) {

				//
				// A ----- B *------x
				// | \  1  | |
				// |   \   | |
				// |  2  \ | |
				// C ----- D y

				double A = getThickness(img, x, y);
				double B = getThickness(img, x+1, y);
				double C = getThickness(img, x, y+1);
				double D = getThickness(img, x+1, y+1);

				Vector3d normal1 = getNormal(img, x, y).add(getNormal(img, x + 1, y)).add(getNormal(img, x + 1, y + 1)).normal();
				Vector3d normal2 = getNormal(img, x, y).add(getNormal(img, x, y + 1)).add(getNormal(img, x + 1, y + 1)).normal();

				out.write("facet normal " + normal1 + "\n");
				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " " + A + " \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * (y + 1) + " " + D + " \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * y + " " + B + " \n");
				out.write("endloop\n");
				out.write("endfacet\n");

				out.write("facet normal " + normal2 + "\n");
				out.write("outer loop\n");
				out.write("vertex " + xyScale * x + " " + xyScale * y + " " + A + " \n");
				out.write("vertex " + xyScale * x + " " + xyScale * (y + 1) + " " + C + " \n");
				out.write("vertex " + xyScale * (x + 1) + " " + xyScale * (y + 1) + " " + D + " \n");
				out.write("endloop\n");
				out.write("endfacet\n");

			}
			out.flush();
		}

		out.write("endsolid SolidNameHere");
		out.flush();
		out.close();
	}

	public static void main(String[] Args) throws IOException {
		BufferedImage img = null;

		
		
		img = ImageIO.read(new File(Args[0]));
		img = mirrorImage(img);
		// img = scaleImage(img, 32, 32);
		img = distanceMap(img);

		// ImageIO.write(img, "jpg", new File("C:/map.jpg"));
		writeStl(img, Args[1]);
		System.out.println("success.");
	}
}
