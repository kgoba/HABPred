package test;

import model.ode.AdamsBashforthSolver;
import model.ode.EulerSolver;
import model.ode.ODEProblem;
import model.ode.ODESolver;
import model.ode.RK4Solver;
import model.ode.MidstepSolver;

public class ODESolverTest {
	public static class TestProblem implements ODEProblem {
		@Override
		public double[] getInitialState(double t) {
			double[] result = { 1 };
			return result;
		}

		@Override
		public double[] getDerivative(double t, double[] state) {
			double[] result = { -state[0] };
			return result;
		}
	}
	
	public static void main(String[] args) {
		double dt = 0.1;
		ODESolver solver1 = new AdamsBashforthSolver(new TestProblem(), dt);
		//ODESolver solver2 = new RK4Solver(new TestProblem(), dt);
		ODESolver solver2 = new MidstepSolver(new TestProblem(), dt);
		//ODESolver solver2 = new EulerSolver(new TestProblem(), dt);
		double[] y1 = null;
		double[] y2 = null;
		double t = 0;
		while (t <= 3) {
			y1 = solver1.solve(t);
			y2 = solver2.solve(t);
			t += dt;
		}
		t -= dt;
		//double y_true = 1.0/(t + 1.0);
		double y_true = Math.exp(-t);
		System.out.format("t = %.3f y1 = %.5f y2 = %.5f\n", t, y1[0] - y_true, y2[0] - y_true);
	}
}
