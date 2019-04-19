package gui;

public class LaunchSettings extends SettingsPanel {
	private static final long serialVersionUID = 3268844221039085172L;

	LaunchSettings(String title) {
		super(title);
		
		addTextField("Altitude", "0", "m");
		addTextField("Latitude", "", "deg");
		addTextField("Longitude", "", "deg");
		
		addExpandingSpace();
	}
}
