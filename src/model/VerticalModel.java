package model;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import model.atmosphere.AtmosphereModel;
import model.gas.Air;
import model.gas.Gas;
import model.gas.GasState;
import model.ode.ODEProblem;

public class VerticalModel implements FirstOrderDifferentialEquations {
	private static int N_EQUATIONS = 2;
	private static double kSPHERE = Math.pow(3 / (4 * Math.PI), 1.0 / 3);
	private static Gas AIR = new Air();

	private AtmosphereModel atmosphere;
	private Balloon balloon;
	private Payload payload;
	
	public VerticalModel(AtmosphereModel atmosphere, Balloon balloon, Payload payload) {
		this.atmosphere = atmosphere;
		this.balloon = balloon;
		this.payload = payload;
	}
	
	public class State {		
		public double z;
		public double z_dot;

		public State(double[] array) {
			z 		= array[0];
			z_dot 	= array[1];
		}
		
		public void getDerivatives(double[] array) {
			// TBD
		}
	}

	@Override
	public void computeDerivatives(double t, double[] y, double[] yDot)
			throws MaxCountExceededException, DimensionMismatchException {
		double z 		= y[0];
		double z_dot 	= y[1];

		GasState ambientState = atmosphere.getState(0, 0, z, 0);
		double ambientRho = AIR.getRho(ambientState);

		double m_total = balloon.getMass() + payload.getMass();
		double weight = m_total * Constants.g;

		GasState internalState = ambientState;
		double volume = balloon.getVolume(internalState);
		double buoyancy = Constants.g * ambientRho * volume;

		double v_air = z_dot;
		double Cd = 0.5;
		double radius = Math.pow(volume, 1.0/3) * kSPHERE;
		double area = Math.PI * radius * radius;
		double drag = area * Cd * ambientRho * (Math.abs(v_air) * v_air / 2);
		
		double force = buoyancy - drag - weight;
		double z_dot2 = force / m_total;

		yDot[0] = z_dot;
		yDot[1] = z_dot2;
	}

	@Override
	public int getDimension() {
		return N_EQUATIONS;
	}
	
	public double[] initialState(double altitude) {
		return new double[] {
				altitude,
				0
		};
	}
}
