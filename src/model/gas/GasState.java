package model.gas;

import model.Constants;

public class GasState {
	public double p;	//< Pressure in Pascals
	public double T;	//< Temperature in Kelvins
	
	public GasState(double p, double T) {
		this.p = p;
		this.T = T;
	}
	
	public static GasState standard() {
		return new GasState(Constants.p_std, Constants.T_std);
	}
}
