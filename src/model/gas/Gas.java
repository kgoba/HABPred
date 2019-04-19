package model.gas;

import model.Constants;

public class Gas {    
	public double M;
	
	public Gas(double M) {
		this.M = M;
	}
	
	public double getRho(double p, double T) {
		return p * M / (Constants.R * T); 
	}
	
	public double getRho(GasState state) {
		return getRho(state.p, state.T);
	}
	
	public double getVolume(GasState state, double m) {
		return (m / M) * Constants.R * state.T / state.p;
	}
}
