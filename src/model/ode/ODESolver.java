package model.ode;

public interface ODESolver {
	public void reset(double t0);
	public double[] solve(double t1);
}
