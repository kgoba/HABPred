package model.atmosphere;

import model.Constants;
import model.gas.GasState;

public class GFSAtmosphereModel implements AtmosphereModel {
	private static double GMR = Constants.g * Constants.M_air / Constants.R;

	public GFSAtmosphereModel() {
		
	}
	
	@Override
	public double getTemperature(double latitude, double longitude, double altitude, long epochSecond) {
		return Constants.T_std;
	}

	@Override
	public double getPressure(double latitude, double longitude, double altitude, long epochSecond) {
		return Constants.p_std;
	}

	@Override
	public GasState getState(double latitude, double longitude, double altitude, long epochSecond) {
		return GasState.standard();
	}
}
