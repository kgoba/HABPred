package gui;

public class PayloadSettings extends SettingsPanel {
	private static final long serialVersionUID = 8988938106211979787L;

	PayloadSettings(String title) {
		super(title);
		
		addTextField("Mass", "1.000", "kg");
		addTextField("D_chute", "0.70", "m");
		addTextField("Cd_chute", "0.6", null);
		
		addExpandingSpace();
	}
}
