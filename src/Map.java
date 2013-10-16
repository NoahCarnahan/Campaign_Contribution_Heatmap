import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * @author Noah Carnahan
 *
 */
public class Map {
	
	private DBConnect dbConnection;
	
	private BufferedImage mapImage;
	/**HeatArray representing the heat on the map*/
	private HeatArray heatOverlay;
	/**A small heat array representing one point on the map.*/
	private HeatArray heatSpot;
	
	
	/**Pixel height of the map image*/
	private int mapHeight;
	/**Pixel width of the map image*/
	private int mapWidth;
	
	private double lonLeft;
	private double latBottom;
	private double lonRight;
	private double latTop;
	
	private double lonDelta;
	private double latBottomDegree;
	
	
	/**
	 * 
	 * @param path The path of an image file.
	 * @param left Longitude of the left edge of the map
	 * @param bottom Latitude of the bottom edge of the map
	 * @param right Longitude of the right edge of the map
	 * @param top Latitude of the top edge of the map
	 * @param logColoring Flag specifying weather log coloring should be used or not 
	 */
	public Map(String path, double left, double bottom, double right, double top, boolean logColoring) {
		
		mapImage = null;
		try {
			mapImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dbConnection = new DBConnect();
		
		mapHeight = mapImage.getHeight();
		mapWidth = mapImage.getWidth();
		lonLeft = left;
		latBottom = bottom;
		lonRight = right;
		latTop = top;
		lonDelta = lonRight - lonLeft;
		latBottomDegree = latBottom * Math.PI / 180 ;
		heatOverlay = new HeatArray(mapWidth, mapHeight, logColoring);
	}
	
	/**
	 * This function must be called when finished using the map. It closes all database connections that were
	 * established in the creation of the map.
	 */
	public void cleanup(){
		dbConnection.cleanup();
	}
	
	/**
	 * Convert the given point in lat/long coordinates to map pixel coordinates.
	 * Algorithm used here from http://stackoverflow.com/questions/2103924/mercator-longitude-and-latitude-calculations-to-x-and-y-on-a-cropped-map-of-the
	 * 
	 * @param geopoint A point in latitude and longitude coordinates.
	 * @return A point in map pixel coordinates
	 */
	public Point convertLatLonToPoint(LatLon geopoint){
		double lat = geopoint.lat;
		double lon = geopoint.lon;
		
		double x = (lon - lonLeft) * (mapWidth / lonDelta);
		lat = lat * Math.PI / 180;
	    //$worldMapWidth = (($mapWidth / $mapLonDelta) * 360) / (2 * M_PI);
		double worldMapWidth = ((mapWidth / lonDelta) * 360) / (2 * Math.PI);
		//$mapOffsetY = ($worldMapWidth / 2 * log((1 + sin($mapLatBottomDegree)) / (1 - sin($mapLatBottomDegree))));
		double mapOffsetY = (worldMapWidth / 2 * Math.log((1 + Math.sin(latBottomDegree)) / (1-Math.sin(latBottomDegree))));
		//$y = $mapHeight - (($worldMapWidth / 2 * log((1 + sin($lat)) / (1 - sin($lat)))) - $mapOffsetY);
		double y = mapHeight - ((worldMapWidth / 2 * Math.log((1+ Math.sin(lat)) / (1 -Math.sin(lat)))) - mapOffsetY);
		
		return new Point(Math.round(Math.round(x)), Math.round(Math.round(y)));
	}
	
	private void applyHeatOverlay(){
		heatOverlay.draw(mapImage);
	}
	
	private void addHeat(String zip){
		LatLon loc = dbConnection.getLatLon(zip);
		if (loc != null)
		{
			addHeat(loc);
		}
	}
	
	private void addHeat(LatLon geopoint){
		Point p = convertLatLonToPoint(geopoint);
		heatOverlay.addHeat(getHeatSpot(), p.x, p.y);
	}
	
	/**
	 * Get the HeatArray representing one data point.
	 * @return
	 */
	private HeatArray getHeatSpot(){
		if (heatSpot == null){
			heatSpot = HeatArray.fromImage("src/dot.png");
		}
		return heatSpot;
	}

	public void mapCandidateDonors(String name){
		ArrayList<String> zips = dbConnection.getDonorLocations(name);
		for(String zip : zips){
				addHeat(zip);
		}
		
	}
	
	/**
	 * Export the map as a png with the given filename
	 * @param filename
	 */
	public void export(String filename){
		
		applyHeatOverlay();
		
		try {
		File outputfile = new File(filename);
		ImageIO.write(mapImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){

		boolean logColoring = false;
		String name = "Peter Welch";
		
		Map map = new Map("src/map.png", -125.1562, 24.2870, -66.2695, 50.1206, logColoring);
		map.mapCandidateDonors(name);
		
		String pathSuffix = "-map";
		if (logColoring){
			pathSuffix = "-log-map";
		}
		map.export("examples/"+name+pathSuffix+".png");
		map.dbConnection.cleanup();
		System.out.println("Done.");
	}
}
