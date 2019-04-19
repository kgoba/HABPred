package model.gas;

import model.Constants;

public class Helium extends GasMixture {
	public Helium(double impurity) {
		super(new Air(), impurity, new Gas(Constants.M_He));
	}

	public Helium() {
		super(new Air(), 0, new Gas(Constants.M_He));
	}
}
