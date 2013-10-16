import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.sun.corba.se.pept.transport.InboundConnectionCache;


public class HeatArray {

	/** Width of the 2d array*/
	private int width;
	/** Height of the 2d array*/
	private int height;
	/** 2d Array storing heat values*/
	private int[][] vals;
	
	/**If this flag is true, log coloring is used, otherwise linear coloring is used*/
	private boolean logColoring;
	
	Color highColor = new Color(255,0,0);
	Color lowColor = new Color(0,0,255);
	int drawingOpacity = Math.round(255/(float)2);
	
	public HeatArray(int w, int h, boolean l){
		width = w;
		height = h;
		vals = new int[width][height];
		logColoring = l;
		
		//Fill with zeros
		for(int[] column : vals){
			Arrays.fill(column, 0);
		}
	}
	
	/**
	 * If no logColoring flag is specified for the constructor, set it to false.
	 * @param w
	 * @param h
	 */
	public HeatArray(int w, int h){
		this(w, h, false);
	}
	
	/**
	 * Create a new HeatArray from the image file at the specified path.
	 * Presently the HeatArray will be comprised of the ALPHA values of each pixel in the image.
	 * @param path
	 */
	public static HeatArray fromImage(String path){
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HeatArray ha = new HeatArray(img.getWidth(), img.getHeight()); 
		
		for (int x=0; x< img.getWidth(); x++){
			for(int y=0; y < img.getHeight(); y++){
				Color clr = new Color(img.getRGB(x, y), true);
				int alpha = clr.getAlpha();
				ha.vals[x][y] = alpha;
			}
		}
		return ha;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getPixel(int x, int y){
		return vals[x][y];
	}
	
	/**
	 * Returns true if the given coordinate is in bounds.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean coordInBounds(int x, int y){
		if(x >= 0 && x < width && y >= 0 && y < height){
			return true;
		}
		return false;
	}
	
	/**
	 * Add the values from the source PixelArray to this PixelArray centered at the given position.
	 * 
	 * @param source The HeatArray to be added to this HeatArray
	 * @param x The x position of this HeatArray where the source will be centered
	 * @param y The y position of this HeatArray where the source will be centered
	 */
	public void addHeat(HeatArray source, int destX, int destY){
		
		int left = destX - (source.getWidth()/2);
		int top = destY - (source.getHeight()/2);
		
		for (int x = 0; x < source.getWidth(); x++){
			for (int y = 0; y < source.getHeight(); y++){
				if(coordInBounds(left+x,top+y)){
					vals[left+x][top+y] = vals[left+x][top+y] + source.getPixel(x, y);
				}
			}
		}
	}
	
	/**
	 * Print out a visual representation of the HeatArray
	 */
	public void print(){
		print(0,width, 0, height);
	}
	
	public void print(int left, int right, int top, int bottom){
		if (coordInBounds(left, top) && coordInBounds(right-1, bottom-1)){
			for(int y=top; y< bottom; y++){
				for (int x=left; x<right; x++){
					String str = vals[x][y]+"";
					while (str.length()<3)
						str = "-"+str;
					System.out.print(str+" ");
				}
				System.out.print("\n");
			}
		}
		else {
			System.out.println("Given coordinates are out of bounds");
		}
	}
	
	/**
	 * Converts the given value to a rgba color based on the given maximum value in the array.
	 * @param val
	 * @param maxVal
	 * @return
	 */
	private Color valToColor(int val, int maxVal){
		float percentOfMax = val/(float) maxVal;
		int red = Math.round(lowColor.getRed() + (percentOfMax * (highColor.getRed() - lowColor.getRed())));
		int green = Math.round(lowColor.getGreen() + (percentOfMax * (highColor.getGreen() - lowColor.getGreen())));
		int blue = Math.round(lowColor.getBlue() + (percentOfMax * (highColor.getBlue() - lowColor.getBlue())));
		int alpha = Math.round(percentOfMax * 255);
		return new Color(red, green, blue, alpha);
	}
	
	private Color logValToColor(int val, int maxVal){
		val = val - 30;
		double cRed = (highColor.getRed()-lowColor.getRed()) / Math.log(maxVal);
		double cGreen = (highColor.getGreen()-lowColor.getGreen()) / Math.log(maxVal);
		double cBlue = (highColor.getBlue()-lowColor.getBlue()) / Math.log(maxVal);
		double cAlpha = 255 / Math.log(maxVal);
		
		int red = Math.round(Math.round(lowColor.getRed() + cRed * Math.log(val)));
		int green = Math.round(Math.round(lowColor.getGreen() + cGreen * Math.log(val)));
		int blue = Math.round(Math.round(lowColor.getBlue() + cBlue * Math.log(val)));
		int alpha = Math.round(Math.round(cAlpha * Math.log(val)));
		
		return new Color (red, green, blue, alpha);
	}
	
	/**
	 * Draw the HeatArray onto the given BufferedImage
	 * @param dest Where to draw the HeatArray. This should have the same dimensions as the HeatArray.
	 */
	public void draw(BufferedImage dest){
		Graphics2D g = dest.createGraphics();
		int maxVal = getMax();
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(vals[x][y] > 30){ //(if(vals[x][y] != 0))
					if (logColoring){
						g.setColor(logValToColor(vals[x][y], maxVal));
					} else {
						g.setColor(valToColor(vals[x][y], maxVal));
					}
					g.fillRect(x, y, 1, 1);
				}
			}
		}
		
		g.dispose();
	}
	
	/**
	 * Returns the greatest value in the HeatArray. This method iterates over the whole HeatArray so use
	 * sparingly.
	 * @return Maximum value in the HeatArray
	 */
	private int getMax(){
		int max = 0;
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if (vals[x][y] > max){
					max = vals[x][y];
				}
			}
		}
		return max;
	}
	
	public static void main(String args[]){
		HeatArray base = new HeatArray(50,50);
		HeatArray dot = HeatArray.fromImage("src/dot.png");
		base.addHeat(dot, 0, 0);
		base.addHeat(dot, 18, 0);
		base.print();
	}
	
}
