package grib;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.ma2.Section;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dods.DODSNetcdfFile;

public class GFSForecastDAP {
	final static int CYCLE_HOURS = 6;
	final static int SECONDS_PER_DAY = 24 * 3600;
	final static int GFS_EPOCH_OFFSET = 719164;		// Days between 1-1-1 00:00:00 and 1-1-1970 00:00:00
	//final static LocalDateTime epochStart = LocalDateTime.of(1, 1, 1, 0, 0);
	
	public void get(LocalDateTime forecast_start, LocalDateTime forecast_end, LatLonRegion region, OutputStream os) throws IOException, InvalidRangeException {
		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		
		// Find the closest previous prediction cycle  
		int cycle_hour = CYCLE_HOURS * (int)(now.getHour() / CYCLE_HOURS);
		LocalDateTime date_model = now.withHour(cycle_hour);
		
		for (int n_try = 0; n_try < 3; n_try++) {
			try {
				// Return on success 
				get(date_model, forecast_start, forecast_end, region, os);
				return;
			} catch (IOException e) {
				// Ignore exceptions
			} catch (InvalidRangeException e) {
				// Ignore exceptions
			}
			// Move to the previous cycle
			date_model = date_model.minusHours(CYCLE_HOURS);
		}

		// Last try - without exception catching
		get(date_model, forecast_start, forecast_end, region, os);
	}
	
	public void get(LocalDateTime date_model, LocalDateTime forecast_start, LocalDateTime forecast_end, LatLonRegion region, OutputStream os) throws IOException, InvalidRangeException {		
		//String url = "http://nomads.ncep.noaa.gov:80/dods/gfs_0p25_1hr/gfs20180515/gfs_0p25_1hr_06z";
		String url = String.format("http://nomads.ncep.noaa.gov:80/dods/gfs_0p25_1hr/gfs%04d%02d%02d/gfs_0p25_1hr_%02dz",
				date_model.getYear(), date_model.getMonthValue(), date_model.getDayOfMonth(), date_model.getHour());
		NetcdfFile ncfile = new DODSNetcdfFile(url);

//		// TODO Auto-generated method stub
//		for (Variable v : ncfile.getVariables()) {
//			int[] shape = v.getShape();
//			String shapeString = Arrays.toString(shape);
//
//			System.out.printf("Found '%s' (%s) [%s] %s\n", v.getDODSName(), v.getFullName(), v.getDimensionsString(),
//					shapeString);
//		}
		
		// 'time', 'lev', 'lat', 'lon'
		// 'hgtprs', 'ugrdprs', 'vgrdprs', 'vvelprs', 'tmpprs'

		String[] varNames = new String[] { "time", "lev", "lat", "lon" };
		List<Variable> vars = new ArrayList<>();
		for (String varName : varNames) {
			vars.add(ncfile.findVariable(varName));
		}
		
		Array[] arrays = ncfile.readArrays(vars).toArray(new Array[0]);

		NCdumpW.printArray(arrays[0], "time", System.out, null);
		
		double time_min = UTCTimeToGFSTime(forecast_start.toEpochSecond(ZoneOffset.UTC));
		double time_max = UTCTimeToGFSTime(forecast_end.toEpochSecond(ZoneOffset.UTC));
		
		Range time_rng = findInclusiveRange(arrays[0], time_min, time_max);
		Range lev_rng = new Section(arrays[1].getShape()).getRange(0);
		Range lat_rng = findInclusiveRange(arrays[2], region.min_lat, region.max_lat);
		Range lon_rng = findInclusiveRange(arrays[3], region.min_lon, region.max_lon);
		
		Array time_data = arrays[0].section(Arrays.asList(new Range[] { time_rng }));
		Array lev_data = arrays[1].section(Arrays.asList(new Range[] { lev_rng }));
		Array lat_data = arrays[2].section(Arrays.asList(new Range[] { lat_rng }));
		Array lon_data = arrays[3].section(Arrays.asList(new Range[] { lon_rng }));
		
		NCdumpW.printArray(time_data, "time", System.out, null);
		NCdumpW.printArray(lev_data, "lev", System.out, null);
		NCdumpW.printArray(lat_data, "lat", System.out, null);
		NCdumpW.printArray(lon_data, "lon", System.out, null);
		
		String[] varNames2 = new String[] { "hgtprs", "ugrdprs", "vgrdprs", "vvelprs", "tmpprs" };

		for (String varName : varNames2) {
			Variable v = ncfile.findVariable(varName);

			Array data = v.read(Arrays.asList(new Range[] { time_rng, lev_rng, lat_rng, lon_rng }));
			//NCdumpW.printArray(data, varName, System.out, null);
			System.out.printf("%s: %s\n", varName, data.shapeToString());
			
			//float[][] data_array = (float[][]) data.reduce().copyToNDJavaArray();
			//System.out.println(Arrays.toString(data_array[0]) + Arrays.toString(data_array[1]));						
		}
		
		//float[][] data_array = (float[][]) v.read(new int[] {0, 0, 0, 0}, new int[] {10, 3, 1, 1}).reduce().copyToNDJavaArray();
	}
	
	private static double UTCTimeToGFSTime(long utcTime) {		
		// The GFS timestmp is a floating point number of days from the epoch,
		// day '0' appears to be January 1st 1 AD.
		return (utcTime / (double)SECONDS_PER_DAY) + GFS_EPOCH_OFFSET;
	}
	
	private static long GFSTimeToUTCTime(double gfsTime) {
		return (long)((gfsTime - GFS_EPOCH_OFFSET) * SECONDS_PER_DAY);
	}
	
	private static Range findInclusiveRange(Array array, double min, double max) throws InvalidRangeException {
		double[] lat = (double[]) array.copyTo1DJavaArray();
		int idx_lat1 = Arrays.binarySearch(lat, min);
		int idx_lat2 = Arrays.binarySearch(lat, max);

		if (idx_lat1 < 0)
			idx_lat1 = -idx_lat1 - 2;

		if (idx_lat2 < 0)
			idx_lat2 = -idx_lat2 - 1;
		
		return new Range(idx_lat1, idx_lat2);		
	}
}
