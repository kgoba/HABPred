package model.gas;

import model.Constants;

public class Air extends Gas {
	public Air() {
		super(Constants.M_air);
	}

	public double getDynamicViscosity(GasState state) {
		// Sutherland's formula
		double C 		= 120;
		double lambda 	= 1.512041288e-6;
		//double T0 = 291.15;
		//double mu0 = 18.27e-6;
		//return mu0 * (T0 + C) / (state.T + C) * Math.pow(state.T / T0, 3.0/2);
		return lambda * Math.pow(state.T, 3.0/2) / (state.T + C);
	}

	public double getKinematicViscosity(GasState state) {
		return getDynamicViscosity(state) / getRho(state);
	}
}
