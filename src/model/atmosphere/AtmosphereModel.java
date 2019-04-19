package model.atmosphere;

import model.gas.GasState;

public interface AtmosphereModel {
	public abstract double getTemperature(double latitude, double longitude, double altitude, long epochSecond);
	public abstract double getPressure(double latitude, double longitude, double altitude, long epochSecond);
	public abstract GasState getState(double latitude, double longitude, double altitude, long epochSecond);
}
