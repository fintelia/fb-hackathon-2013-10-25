package imageToStl;

import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class ImageToStl {

	static int min(int i1, int i2) {
		return i1 < i2 ? i1 : i2;
	}

	static int max(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}

	static BufferedImage distanceMap(BufferedImage img) {

		BufferedImage newImage = new BufferedImage(img.getWidth(),
				img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		for (int x = 0; x < img.getWidth(); x++) {

			System.out.println(x);

			for (int y = 0; y < img.getHeight(); y++) {

				int minDistanceSquared = Integer.MAX_VALUE;
				for (int h = max(0, x - 16); h <= min(x + 16,
						img.getWidth() - 1); h++) {

					for (int k = max(0, y - 16); k <= min(y + 16,
							img.getHeight() - 1); k++) {

						if ((img.getRGB(h, k) & 0xffffff) == 0) {
							minDistanceSquared = Math.min(minDistanceSquared,
									(x - h) * (x - h) + (y - k) * (y - k));
						}

					}
				}

				double strength = 0.5 + 0.5 * Math.cos(Math.PI / 16.0
						* Math.min(Math.sqrt(minDistanceSquared), 16.0));
				int iStrength = (int) (255 * strength);
				newImage.setRGB(x, y, iStrength << 16 | iStrength << 8
						| iStrength);
			}
		}

		return newImage;

	}

	public static BufferedImage scaleImage(BufferedImage img, int newX, int newY) {
		BufferedImage newImage = new BufferedImage(newX, newY,
				BufferedImage.TYPE_INT_RGB);

		double sx = (double) img.getWidth() / newX;
		double sy = (double) img.getHeight() / newY;

		for (int x = 0; x < newX; x++) {
			for (int y = 0; x < newY; y++) {
				newImage.setRGB(x, y, img.getRGB(
						min((int) (sx * x + 0.5), img.getWidth()),
						min((int) (sy * y + 0.5), img.getHeight())));
			}
		}

		return newImage;
	}

	
	public static Vector3d getNormal(BufferedImage img, int x, int y){
		
		double vx;
		double vz;
		
		if(x == 0){
			vx = 2.0 * ((img.getRGB(x, y) & 0xff) - (img.getRGB(x+1,y) & 0xff));
		}
		else if(x == img.getWidth()-1){
			vx = 2.0 * ((img.getRGB(x-1, y) & 0xff) - (img.getRGB(x,y) & 0xff));
		}else{
			vx = ((img.getRGB(x-1, y) & 0xff) - (img.getRGB(x+1,y) & 0xff));
		}
		
		return new Vector3d(0,0,0);
		
	}
	
	/**
	 * 
	 * file format:
	 * 
	 * solid name facet normal ni nj nk outer loop vertex v1x v1y v1z vertex v2x
	 * v2y v2z vertex v3x v3y v3z endloop endfacet endsolid name
	 * 
	 * @param img
	 * @return
	 * 
	 */
	public static String makeStl(BufferedImage img) {
		StringBuilder s = new StringBuilder();

		s.append("solid SolidNameHere\n");


		
		for (int x = 0; x < img.getWidth()-1; x++) {
			for (int y = 0; y < img.getHeight()-1; y++) {
				
				/*
				 *    A ----- B    y
				 *    |     / |    |
				 *    |   /   |    |
				 *    | /     |    |
				 *    C ----- D    *------ x
				 */
				
				int A = img.getRGB(x,y) & 0xff;
				int B = img.getRGB(x,y) & 0xff;
				int C = img.getRGB(x,y) & 0xff;
				int D = img.getRGB(x,y) & 0xff;
				
				
					
				
				
				s.append("    facet normal" + "ni nj nk" + "\n");		
				s.append("        outer loop\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("        endloop\n");
				s.append("    endfacet\n");
				
				s.append("    facet normal" + "ni nj nk" + "\n");		
				s.append("        outer loop\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("            vertex" + "vx vy vz" + "\n");
				s.append("        endloop\n");
				s.append("    endfacet\n");
				
			}
		}
		

		s.append("solid SolidNameHere");

		return s.toString();
	}

	public static void main(String[] Args) throws IOException {
		BufferedImage img = null;

		img = ImageIO.read(new File(Args[0]));
		img = scaleImage(img, 128, 128);
		img = distanceMap(img);

		ImageIO.write(img, "jpg", new File("C:/map.jpg"));

	}
}
