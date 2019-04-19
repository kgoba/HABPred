package gui;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

public class BalloonSettings extends SettingsPanel {
	private static final long serialVersionUID = 5169556943469704923L;
	
	BalloonSettings(String title) {
		super(title);

		addTextField("Mass", "1.200", "kg");
		addTextField("D_burst", "8.00", "m");
		addTextField("Cd", "0.4", null);

		addExpandingSpace();
	}
}
