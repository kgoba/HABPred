package model.ode;

public interface ODEProblem {
	//public int getDimensions();
	public double[] getInitialState(double t);
	public double[] getDerivative(double t, double[] state);
}
