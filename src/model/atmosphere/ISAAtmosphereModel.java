package model.atmosphere;

import model.Constants;
import model.gas.GasState;

public class ISAAtmosphereModel implements AtmosphereModel {
	private static double GMR = Constants.g * Constants.M_air / Constants.R;
	
	private static int n_layers = 6;
	private static double[] h_b = { 0, 11000, 20000, 32000, 47000, 51000 };
	private static double[] p_b = { 101325, 22632.10, 5474.89, 868.02, 110.91, 66.94 };
	private static double[] T_b = { 288.15, 216.65, 216.65, 228.65, 270.65, 270.65 };
	private static double[] L_b = { -0.0065, 0, 0.001, 0.0028, 0, -0.0028 };

	private static int findLayer(double h) {
		int i = 0;
		while (i < n_layers - 1) {
			if (h_b[i + 1] > h) return i;
			i++;
		}
		// TODO: throw exception
		return -1;
	}
	
	@Override
	public double getTemperature(double latitude, double longitude, double altitude, long epochSecond) {
		int i = findLayer(altitude);
		if (L_b[i] == 0)
			return T_b[i];
		else
			return T_b[i] + (altitude - h_b[i]) * L_b[i];
	}

	@Override
	public double getPressure(double latitude, double longitude, double altitude, long epochSecond) {
		int i = findLayer(altitude);
		if (L_b[i] == 0)
			return p_b[i] * Math.exp(-GMR * (altitude - h_b[i]) / T_b[i]);
		else {
			double T = T_b[i] + (altitude - h_b[i]) * L_b[i]; // getTemperature(altitude);
			return p_b[i] * Math.pow(T_b[i] / T, GMR / L_b[i]);			
		}
	}

	@Override
	public GasState getState(double latitude, double longitude, double altitude, long epochSecond) {
		int i = findLayer(altitude);
		if (L_b[i] == 0)
			return new GasState(p_b[i] * Math.exp(-GMR * (altitude - h_b[i]) / T_b[i]), T_b[i]);
		else {
			double T = T_b[i] + (altitude - h_b[i]) * L_b[i]; // getTemperature(altitude);
			return new GasState(p_b[i] * Math.pow(T_b[i] / T, GMR / L_b[i]), T);			
		}		
	}
}
