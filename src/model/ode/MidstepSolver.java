package model.ode;

public class MidstepSolver implements ODESolver {
	private double[] 	y;
	private double 		t;
	private double 		dtMax;
	private ODEProblem problem;
	
	public MidstepSolver(ODEProblem problem, double maxTimestep) {
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
		double[] ydot1 = problem.getDerivative(t, y);
		
		double[] y1 = y.clone();
		double t1 = t;
		for (int i = 0; i < y.length; i++) {
			y1[i] += ydot1[i] * dt / 2;
		}
		t1 += dt / 2;
		
		double[] ydot = problem.getDerivative(t1, y1);
		for (int i = 0; i < y.length; i++) {
			y[i] += ydot[i] * dt;
		}
		
		t += dt;
	}
}
