
public class LatLon {
	
	double lat;
	double lon;
	
	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public String toString() {
		return "<LatLon: Lat: "+lat+" Lon: "+lon+" >";
	}
}
