package gui;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public SettingsPanel(String title) {
		super();
		
		setBorder(BorderFactory.createTitledBorder(title));

		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setLayout(new GridBagLayout());
		setAlignmentY(Component.TOP_ALIGNMENT);
		//setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	protected void addSeparator() {
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = param_idx;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(new Dimension(8, 8));
		this.add(separator, c);
		param_idx++;
	}
	
	protected void addExpandingSpace() {
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = param_idx;
		c.weighty = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		this.add(new JPanel(), c);		
		param_idx++;
	}

	protected void addTextField(String title, String value, String units) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = param_idx;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel(title), c);
		
		c.gridx = 1;
		c.gridy = param_idx;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_END;
		JTextField field = new JTextField(value, 5);
		field.setHorizontalAlignment(SwingConstants.RIGHT);
		//field.setEnabled(false);
		this.add(field, c);

		if (units != null) {
			c.gridx = 2;
			c.gridy = param_idx;
			c.anchor = GridBagConstraints.LINE_START;
			this.add(new JLabel(units), c);			
		}
		param_idx++;
	}
	
	protected void addChoiceField(String title, String[] choices) {
		JLabel label = new JLabel(title);
		GridBagConstraints c = new GridBagConstraints();
		
		Box box = new Box(BoxLayout.PAGE_AXIS);
		//JPanel box = new JPanel();
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < choices.length; i++) {
			JRadioButton button = new JRadioButton(choices[i], i == 0);
			box.add(button);
			group.add(button);
		}
		
		c.gridx = 0;
		c.gridy = param_idx;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		this.add(label, c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		c.gridy = param_idx;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(box, c);
		
		param_idx++;
	}
	
	protected void addChoiceField2(String title, String[] choices) {
		JPanel box = new JPanel();
		box.setBorder(BorderFactory.createTitledBorder(title));
		
		GridBagConstraints c = new GridBagConstraints();
		
		ButtonGroup group = new ButtonGroup();
		for (String choice: choices) {
			JToggleButton button = new JToggleButton(choice);
			box.add(button);
			group.add(button);
		}
		
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = param_idx;
		this.add(box, c);
		
		param_idx++;
	}

	private int param_idx = 0;
}
