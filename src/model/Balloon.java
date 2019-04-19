package model;

import model.gas.Gas;
import model.gas.GasAmount;
import model.gas.GasState;

public class Balloon {
	private static Gas ambientGas = new Gas(Constants.M_air);

	public String name;
	public double mEnvelope;
	public double rBurst;

	public GasAmount gasAmount;

	public Balloon(String name, double mEnvelope, double rBurst) {
		this.name = name;
		this.mEnvelope = mEnvelope;
		this.rBurst = rBurst;
		
		this.gasAmount = new GasAmount(ambientGas, 0);
	}

	public void fillByMass(Gas gas, double mGas) {
		this.gasAmount = new GasAmount(gas, mGas);
	}

	public Balloon fillByNeckLift(Gas gas, double lift) throws Exception {
		GasState std = GasState.standard();
		double mGas = (lift + mEnvelope) * gas.getRho(std) / (ambientGas.getRho(std) - gas.getRho(std));
		if (mGas < 0) 
			throw new Exception("Negative gas mass to be filled");
		
		this.gasAmount = new GasAmount(gas, mGas);
		return this;
	}

	public double getVolume(GasState state) {
		return gasAmount.getVolume(state);
	}

	public double getMass() {
		return gasAmount.mass + mEnvelope;
	}

	public double getBuoyancy(GasState ambientState, GasState state) {
		return Constants.g * ambientGas.getRho(ambientState) * getVolume(state);
	}

	public double getLift(GasState ambientState, GasState state) {
		return getBuoyancy(ambientState, state) - (getMass() * Constants.g);
	}
}
