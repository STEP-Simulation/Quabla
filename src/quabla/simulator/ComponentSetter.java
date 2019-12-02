package quabla.simulator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;

/**
 * レイアウトマネジャークラス
 * GribBagLayoutとGribBagConstraintsを統合し,Frameクラスの簡素化を図るクラス
 * */
public class ComponentSetter extends GridBagLayout {
	private GridBagConstraints gbc = new GridBagConstraints();

	public void setFill(int fillOption) {
		gbc.fill = fillOption;
	}

	public void setComponent(JComponent c, int gridx, int gridy, int gridwidth, int gridheight, double weightx,
			double weighty, int anchor) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.anchor = anchor;
		setConstraints(c, gbc);

		setDefaultParameter();
	}

	private void setDefaultParameter() {
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
	}
}
