package model.gas;

import model.Constants;

public class GasMixture extends Gas {

	/// Allows mixing two gases and treating them as a single gas
	public GasMixture(Gas gas1, double fraction1, Gas gas2) {
		super(gas1.M * fraction1 + gas2.M * (1 - fraction1));
	}

	@SuppressWarnings("unused")
	private static void test() {
		Gas gas = new GasMixture(new Gas(Constants.M_air), 0.015, new Gas(Constants.M_H2));
	}
}
