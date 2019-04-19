package test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;
import org.jfree.data.general.SeriesException;

import model.Balloon;
import model.Payload;
import model.VerticalModel;
import model.atmosphere.ISAAtmosphereModel;
import model.gas.Hydrogen;
import model.ode.ODEProblem;

public class ODESolverTest2 {
	private static double TIMESTEP = 1.0;
	private static double RESULT_TIMESTEP = 60.0;
	
	private static List<double[]> results = null;
	
	public static class TestProblem1 implements FirstOrderDifferentialEquations {
		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot)
				throws MaxCountExceededException, DimensionMismatchException {
			yDot[0] = -0.01 * y[0];
		}

		@Override
		public int getDimension() {
			return 1;
		}
		
		public double[] initialState(double y0) {
			return new double[] {
				y0
			};
		}
	}
	
	public static class TestProblem2 extends VerticalModel {
		public TestProblem2() throws Exception {
			super(
				new ISAAtmosphereModel(),
				new Balloon("Test Balloon", 1.000, 4.00).fillByNeckLift(new Hydrogen(), 2.500),
				new Payload("Test Payload", 1.000)
			);			
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestProblem2 model = new TestProblem2();
		
		FirstOrderIntegrator integrator = new MidpointIntegrator(TIMESTEP);
		double[] y0 = model.initialState(1); // initial state

		final FixedStepHandler resultHandler = new FixedStepHandler() {			
			@Override
			public void init(double t0, double[] y0, double t) {
				results = new ArrayList<double[]>();
				//results.add(y0.clone());
				System.out.printf("t0 = %.3f, y0 = %.3f\n", t0, y0[0]);
			}

			@Override
			public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
				results.add(y.clone());
				System.out.printf("t = %.3f, y = %.3f y' = %.3f\n", t, y[0], yDot[0]);
				//double y_true = Math.exp(-0.01 * t);
				//double err = y[0] - y_true;
				//System.out.printf("t = %.3f, err = %.6f rel_err = %.1f ppm\n", t, err, err/y_true * 1E6);
			}
		};
		integrator.addStepHandler(new StepNormalizer(RESULT_TIMESTEP, resultHandler));
		
		integrator.integrate(model, 0.0, y0, 3600.0, y0); // now y contains final state at time t=16.0
		
		for (int i = 0; i < results.size(); i++) {
			double t = i * RESULT_TIMESTEP;
			double[] y = results.get(i);
			try {
				//System.out.printf("t = %.3f, y = %.3f\n", t, y[0]);
				//series1.add(i, Math.log10(air.getKinematicViscosity(model.getState(i))));
			} catch (SeriesException e) {
				System.err.println("Error adding to series");
			}
		}		
	}
}
