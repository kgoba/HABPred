package model.ode;

public class Vector {
	private double[] x;

	public Vector(double[] array) {
		x = array;
	}

	public double[] asArray() {
		return x;
	}
	
	public double get(int i) {
		return x[i];
	}
	
	public void set(int i, double value) {
		x[i] = value;
	}

	public Vector add(Vector v) {
		for (int i = 0; i < x.length; i++) {
			x[i] += v.x[i];
		}
		return this;
	}

	public Vector multiply(double k) {
		for (int i = 0; i < x.length; i++) {
			x[i] *= k;
		}
		return this;
	}

	public static Vector zeros(int length) {
		return new Vector(new double[length]);
	}
	
	public static Vector ones(int length) {
		double[] x = new double[length];
		for (int i = 0; i < x.length; i++) {
			x[i] = 1;
		}
		return new Vector(x);
	}
}
