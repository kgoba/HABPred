package grib;

public class LatLonRegion {
	public double min_lat;
	public double max_lat;
	public double min_lon;
	public double max_lon;
	
	public LatLonRegion(double min_lat, double max_lat, double min_lon, double max_lon) {
		this.min_lat = min_lat;
		this.max_lat = max_lat;
		this.min_lon = min_lon;
		this.max_lon = max_lon;
	}
}
