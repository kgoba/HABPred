package model.gas;

public class GasAmount {
	public Gas 		gas;
	public double   mass;
	
	public GasAmount(Gas gas, double mass) {
		this.gas = gas;
		this.mass = mass;
	}
		
	public double getRho(GasState state) {
		return gas.getRho(state.p, state.T);
	}
	
	public double getVolume(GasState state) {
		return gas.getVolume(state, mass);
	}
}
