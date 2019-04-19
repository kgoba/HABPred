package gui;

public class GasSettings extends SettingsPanel {
	private static final long serialVersionUID = -5900817666911248290L;

	GasSettings(String title) {
		super(title);
		
		addChoiceField("Type", new String[] { "Helium", "Hydrogen" } );
		addTextField("Impurity", "1.5", "%");
		addSeparator();
		addChoiceField("Target", new String[] { "Burst altitude", "Ascent speed", "Neck lift" } );
		addTextField("h_burst", "30000", "m");
		addTextField("v_ascent", "5.0", "m/s");
		addTextField("Neck lift", "4.234", "kg");
		
		addExpandingSpace();
	}
}
