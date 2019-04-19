package test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.util.IOUtils;

import grib.GFSForecastCGI;
import grib.GFSForecastDAP;
import grib.LatLonRegion;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.ma2.Section;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dods.DODSNetcdfFile;

public class GRIBTest {
	public static void main(String[] args) {
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&lev_10_mb=on&lev_350_mb=on&var_HGT=on&var_PRES=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&all_lev=on&var_HGT=on&var_PRES=on&var_TMP=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&lev_1_mb=on&lev_1000_mb=on&lev_100_mb=on&lev_10_mb=on&lev_150_mb=on&lev_2_mb=on&lev_200_mb=on&lev_20_mb=on&lev_250_mb=on&lev_3_mb=on&lev_300_mb=on&lev_30_mb=on&lev_350_mb=on&lev_400_mb=on&lev_450_mb=on&lev_5_mb=on&lev_500_mb=on&lev_50_mb=on&lev_550_mb=on&lev_600_mb=on&lev_650_mb=on&lev_7_mb=on&lev_700_mb=on&lev_70_mb=on&lev_750_mb=on&lev_800_mb=on&lev_850_mb=on&lev_900_mb=on&lev_925_mb=on&lev_950_mb=on&lev_975_mb=on&var_HGT=on&var_PRES=on&var_TMP=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		String filename = args[0];
		System.out.println(filename);
		DODSNetcdfFile ncfile = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GFSForecastDAP forecast = new GFSForecastDAP();

		try {

			// FileInputStream fis = new FileInputStream(filename);
			// IOUtils.copy(fis, bos);

			LocalDateTime now 				= LocalDateTime.now(Clock.systemUTC());
			LocalDateTime forecast_start 	= now.plusHours(3);
			LocalDateTime forecast_end   	= forecast_start.plusHours(3);

			LatLonRegion region = new LatLonRegion(56, 58, 22, 28);
			
			forecast.get(forecast_start, forecast_end, region, bos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

//		try {
//			// forecast.open(filename, bos.toByteArray());
//			// ncfile = NetcdfFile.open(filename);
//			String url = "http://nomads.ncep.noaa.gov:80/dods/gfs_0p25_1hr/gfs20180515/gfs_0p25_1hr_00z";
//			ncfile = new DODSNetcdfFile(url);
//			// ncfile = NetcdfFile.openInMemory(filename, bos.toByteArray());
//			process(ncfile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			// if (null != ncfile)
//			// try {
//			// ncfile.close();
//			// } catch (IOException ioe) {
//			// log("trying to close " + filename, ioe);
//			// }
//		}
	}

	private static void log(String string, Exception ioe) {
		// TODO Auto-generated method stub
		System.out.printf("ERROR: %s (%s)\n", string, ioe.toString());
	}

	private static void process(DODSNetcdfFile ncfile) throws IOException {
		// TODO Auto-generated method stub
		for (Variable v : ncfile.getVariables()) {
			int[] shape = v.getShape();
			String shapeString = Arrays.toString(shape);

			System.out.printf("Found '%s' (%s) [%s] %s\n", v.getDODSName(), v.getFullName(), v.getDimensionsString(),
					shapeString);
		}
		
		// 'time', 'lev', 'lat', 'lon'
		// 'hgtprs', 'ugrdprs', 'vgrdprs', 'vvelprs', 'tmpprs'
		
		// The GFS timestmp is a floating point number of days from the epoch,
		// day '0' appears to be January 1st 1 AD.

		String[] varNames = new String[] { "time", "lev", "lat", "lon" };
		List<Variable> vars = new ArrayList<>();
		for (String varName : varNames) {
			Variable v = ncfile.findVariable(varName);
//			try {
//				Range[] ranges = new Range[] { new Range(1, 5) };
//				v = v.section(Arrays.asList(ranges));
//			} catch (InvalidRangeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			vars.add(v);
		}
		
		Array[] arrays = ncfile.readArrays(vars).toArray(new Array[0]);
		//Array lon = arrays[3];
		
		double[] lat = (double[]) arrays[2].copyTo1DJavaArray();
		int idx_lat1 = Arrays.binarySearch(lat, 55.95f);
		int idx_lat2 = Arrays.binarySearch(lat, 58.05f);

		if (idx_lat1 < 0)
			idx_lat1 = -idx_lat1 - 2;

		if (idx_lat2 < 0)
			idx_lat2 = -idx_lat2 - 1;

		System.out.printf("Idx lat = [%d, %d] [%f, %f]\n", idx_lat1, idx_lat2, lat[idx_lat1], lat[idx_lat2]);
		
		for (int i = 0; i < varNames.length; i++) {
			Array data = arrays[i];
			String varName = varNames[i];
			// Variable v = ncfile.findVariable(varName);

			// Section s = new Section(v.getShapeAsSection());
			// s.replaceRange(0, new Range(1, 5));
			// s.appendRange(0, 5);
			// v = v.section(s);

			// Range[] ranges = new Range[] { new Range(1, 5) };

			// Array data = v.read(Arrays.asList(ranges));
			NCdumpW.printArray(data, varName, System.out, null);
		}
		
		try {
			//Array data = ncfile.readData(ncfile.findVariable("time"), new Section());
			Variable v = ncfile.findVariable("hgtprs");
			//v = v.section();
			//Array data = v.read(new Section(new int[] {3}));
			//ArrayFloat.D1 data = (ArrayFloat.D1)v.read(new int[] {0, 0, 0, 0}, new int[] {1, 31, 1, 1}).reduce();			
			//NCdumpW.printArray(data, "lon_sub", System.out, null);

			float[][] data_array = (float[][]) v.read(new int[] {0, 0, 0, 0}, new int[] {10, 3, 1, 1}).reduce().copyToNDJavaArray();
			System.out.println(Arrays.toString(data_array[0]) + Arrays.toString(data_array[1]));			
		} catch (InvalidRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
