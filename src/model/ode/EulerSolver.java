package model.ode;

public class EulerSolver implements ODESolver {
	private double[] 	y;
	private double 		t;
	private double 		dtMax;
	private ODEProblem problem;
	
	public EulerSolver(ODEProblem problem, double maxTimestep) {
		this.dtMax = maxTimestep;
		this.problem = problem;
		reset(0);
	}
	
	@Override
	public void reset(double t0) {
		t = t0;
		y = problem.getInitialState(t0);
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
			y[i] += ydot[i] * dt;
		}		
		t += dt;
	}
}
