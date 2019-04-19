package model.gas;

import model.Constants;

public class Hydrogen extends GasMixture {
	public Hydrogen(double impurity) {
		super(new Air(), impurity, new Gas(Constants.M_H2));
	}

	public Hydrogen() {
		super(new Air(), 0, new Gas(Constants.M_H2));
	}
}
