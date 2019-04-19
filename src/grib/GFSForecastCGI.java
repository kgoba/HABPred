package grib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import ucar.ma2.Array;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class GFSForecastCGI {
	private String workingDirectory;

	public GFSForecastCGI() {
		//here, we assign the name of the OS, according to Java, to a variable...
		String OS = (System.getProperty("os.name")).toUpperCase();
		//to determine what the workingDirectory is.
		//if it is some version of Windows
		if (OS.contains("WIN"))
		{
		    //it is simply the location of the "AppData" folder
		    workingDirectory = System.getenv("AppData");
		}
		//Otherwise, we assume Linux or Mac
		else
		{
		    //in either case, we would start in the user's home directory
		    workingDirectory = System.getProperty("user.home");
		    if (OS.endsWith("OS X")) {
			    //if we are on a Mac, we are not done, we look for "Application Support"
			    workingDirectory += "/Library/Application Support";
		    }
		}
		workingDirectory += "/HABPred/cache";
		new File(workingDirectory).mkdirs();
	}
	
	public void download(LocalDateTime date_model, LocalDateTime date_forecast, LatLonRegion region, OutputStream os) throws Exception {
		if (date_model.isAfter(date_forecast)) {
			throw new Exception("GFS forecast date before model date!");
		}
		Duration dur = Duration.between(date_model, date_forecast);
		int hours_ahead = (int)Math.floor(dur.getSeconds() / 3600);

		URL url = new URL(String.format(Locale.US, "http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl"
				+ "?file=gfs.t%02dz.pgrb2.0p25.f%03d"
				+ "&lev_1_mb=on&lev_1000_mb=on&lev_100_mb=on&lev_10_mb=on&lev_150_mb=on&lev_2_mb=on&lev_200_mb=on&lev_20_mb=on&lev_250_mb=on"
				+ "&lev_3_mb=on&lev_300_mb=on&lev_30_mb=on&lev_350_mb=on&lev_400_mb=on&lev_450_mb=on&lev_5_mb=on&lev_500_mb=on&lev_50_mb=on"
				+ "&lev_550_mb=on&lev_600_mb=on&lev_650_mb=on&lev_7_mb=on&lev_700_mb=on&lev_70_mb=on&lev_750_mb=on&lev_800_mb=on"
				+ "&lev_850_mb=on&lev_900_mb=on&lev_925_mb=on&lev_950_mb=on&lev_975_mb=on"
				+ "&var_HGT=on&var_PRES=on&var_TMP=on&var_UGRD=on&var_VGRD=on"
				+ "&subregion=&leftlon=%.2f&rightlon=%.2f&toplat=%.2f&bottomlat=%.2f" + "&dir=%%2Fgfs.%04d%02d%02d%02d",
				date_model.getHour(), hours_ahead, region.min_lon, region.max_lon, region.max_lat, region.min_lat,
				date_model.getYear(), date_model.getMonthValue(), date_model.getDayOfMonth(), date_model.getHour()));
		
		IOUtils.copy(url.openStream(), os);
	}
	
	public void get(LocalDateTime date_model, LocalDateTime date_forecast, LatLonRegion region, OutputStream os) throws Exception {
		if (date_model.isAfter(date_forecast)) {
			throw new Exception("GFS forecast date before model date!");
		}
		Duration dur = Duration.between(date_model, date_forecast);
		int hours_ahead = (int) Math.floor(dur.getSeconds() / 3600);

		String cache_id = String.format(Locale.US, "gfs-%04d%02d%02d-%02dz-%03d-%.2f-%.2f-%.2f-%.2f",
				date_model.getYear(), date_model.getMonthValue(), date_model.getDayOfMonth(), date_model.getHour(),
				hours_ahead, region.min_lon, region.max_lon, region.min_lat, region.max_lat);

		try {
			// retrieve cached copy
			FileInputStream fis = new FileInputStream(workingDirectory + '/' + cache_id);
			IOUtils.copy(fis, os);
		} catch (IOException e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			download(date_model, date_forecast, region, bos);
			IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), os);

			// save cached copy
			FileOutputStream fos = new FileOutputStream(workingDirectory + '/' + cache_id);
			IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), fos);
			fos.close();
		}
	}
	
	public void open(String filename, byte[] byteArray) {
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&lev_10_mb=on&lev_350_mb=on&var_HGT=on&var_PRES=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&all_lev=on&var_HGT=on&var_PRES=on&var_TMP=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		// http://nomads.ncep.noaa.gov/cgi-bin/filter_gfs_0p25_1hr.pl?file=gfs.t12z.pgrb2.0p25.f005&lev_1_mb=on&lev_1000_mb=on&lev_100_mb=on&lev_10_mb=on&lev_150_mb=on&lev_2_mb=on&lev_200_mb=on&lev_20_mb=on&lev_250_mb=on&lev_3_mb=on&lev_300_mb=on&lev_30_mb=on&lev_350_mb=on&lev_400_mb=on&lev_450_mb=on&lev_5_mb=on&lev_500_mb=on&lev_50_mb=on&lev_550_mb=on&lev_600_mb=on&lev_650_mb=on&lev_7_mb=on&lev_700_mb=on&lev_70_mb=on&lev_750_mb=on&lev_800_mb=on&lev_850_mb=on&lev_900_mb=on&lev_925_mb=on&lev_950_mb=on&lev_975_mb=on&var_HGT=on&var_PRES=on&var_TMP=on&var_UGRD=on&var_VGRD=on&subregion=&leftlon=24&rightlon=25&toplat=57&bottomlat=56&dir=%2Fgfs.2018051412
		NetcdfFile ncfile = null;
		try {
			ncfile = NetcdfFile.openInMemory(filename, byteArray);
			process(ncfile);
		} catch (IOException ioe) {
			//log("trying to open " + filename, ioe);
		} finally {
			if (null != ncfile)
				try {
					ncfile.close();
				} catch (IOException ioe) {
					//log("trying to close " + filename, ioe);
				}
		}
	}
	
	public void process(NetcdfFile nc) throws IOException {
		Array lat_data = nc.findVariable("lat").read();
		Array lon_data = nc.findVariable("lon").read();
		Array pres_data = nc.findVariable("isobaric").read();
		Array height_data = nc.findVariable("Geopotential_height_isobaric").read();
		Array wind_u_data = nc.findVariable("u-component_of_wind_isobaric").read();
		Array wind_v_data = nc.findVariable("v-component_of_wind_isobaric").read();
		Array temp_data = nc.findVariable("Temperature_isobaric").read();
		
		for (Variable v : nc.getVariables()) {
			System.out.printf("Found '%s' (%s) [%s]\n", v.getShortName(), v.getFullName(), v.getDimensionsString());
			// lat, lon, reftime, time, isobaric, Geopotential_height_isobaric, Temperature_isobaric, u-component_of_wind_isobaric
			String varName = v.getDODSName();
			try {
				Array data = v.read();
				NCdumpW.printArray(data, varName, System.out, null);
			} catch (IOException ioe) {
				//log("trying to read " + varName, ioe);
			} 
		}
	}
}
