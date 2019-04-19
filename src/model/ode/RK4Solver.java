package model.ode;

public class RK4Solver implements ODESolver {
	private double[] 	y;
	private double 		t;
	private double 		dtMax;
	private ODEProblem problem;
	
	public RK4Solver(ODEProblem problem, double maxTimestep) {
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
		for (int i = 0; i < y.length; i++) {
			y1[i] += ydot1[i] * dt / 2;
		}
		
		double[] y2 = y.clone();
		double[] ydot2 = problem.getDerivative(t + dt/2, y1);
		for (int i = 0; i < y.length; i++) {
			y2[i] += ydot2[i] * dt / 2;
		}

		double[] y3 = y.clone();
		double[] ydot3 = problem.getDerivative(t + dt/2, y2);
		for (int i = 0; i < y.length; i++) {
			y3[i] += ydot3[i] * dt;
		}

		double[] ydot4 = problem.getDerivative(t + dt, y3);

		for (int i = 0; i < y.length; i++) {
			y[i] += (ydot1[i] + 2 * ydot2[i] + 2 * ydot3[i] + ydot4[i]) * dt / 6;
		}
		t += dt;
	}
}
