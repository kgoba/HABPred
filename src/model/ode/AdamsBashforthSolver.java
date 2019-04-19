package model.ode;

public class AdamsBashforthSolver implements ODESolver {
	private double[] 	y;
	private double[] 	ydotPrevious;
	private double 		t;
	private double 		dtMax;
	private ODEProblem problem;
	
	public AdamsBashforthSolver(ODEProblem problem, double maxTimestep) {
		this.dtMax = maxTimestep;
		this.problem = problem;
		reset(0);
	}
	
	@Override
	public void reset(double t0) {
		t = t0;
		y = problem.getInitialState(t0);
		ydotPrevious = new double[y.length];
	}
	
	@Override
	public double[] solve(double t1) {
		while (t + dtMax < t1) {
			step(dtMax);
		}
		if (t1 - t > 0) {
			step(t1 - t);
		}
		return y;
	}
	
	private void step(double dt) {
		double[] ydot = problem.getDerivative(t, y);
		for (int i = 0; i < y.length; i++) {
			y[i] += (3 * ydot[i] - ydotPrevious[i]) * dt / 2;
		}		
		t += dt;
		ydotPrevious = ydot;
	}
}
