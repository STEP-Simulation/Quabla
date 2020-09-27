package quabla.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import quabla.simulator.ComponentSetter;

public class DataInputFrame extends JFrame {

	private CardLayout LayoutOfPanel = new CardLayout();
	private JPanel CardPanel = new JPanel();

	private int contentPaneH, contentPaneW;

	private LinkedHashMap<String, LinkedHashMap<String, JTextField>> dataField = new LinkedHashMap<>();

	private static final String ShowCard = "showCard",
			StartCalculation = "StartCalculation",
			ChangeTextFieldColor = "ChangeTextFieldColor",
			SetExistingInputData = "SetInputData";

	public DataInputFrame(int width, int height) {

		// Setting Frame
		setTitle("QUABLA");

		// Setting Inner Size
		this.contentPaneH = height;
		this.contentPaneW = width;
		getContentPane().setPreferredSize(new Dimension(contentPaneH, contentPaneW));
		pack();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setting a frame in center
		setLocationRelativeTo(null);

		setComponent();
		setVisible(true);
	}

	private void setComponent() {
		ComponentSetter LayoutOfFrame = new ComponentSetter();
		setLayout(LayoutOfFrame);

		//入力値を既存のファイルから取得し,セット
		JButton setInputDataButton = new JButton("既存のデータファイルからパラメータをセットする");

		// combobox
		JComboBox combo = new JComboBox();
		//combo.addActionListener(this);
		combo.setActionCommand(ShowCard);
		LayoutOfFrame.setComponent(combo, 0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER);
		add(combo);

		// シミュレーション開始
		JButton calcStartButton = new JButton("Start simulation");
		//calcStartButton.addActionListener(this);
		calcStartButton.setActionCommand(StartCalculation);
		LayoutOfFrame.setComponent(calcStartButton, 0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER);
		add(calcStartButton);

		//カードレイアウトパネル
		CardPanel.setLayout(LayoutOfPanel);
		LayoutOfFrame.setComponent(CardPanel, 0, 2, 1, 1, 0, 1.0d, GridBagConstraints.CENTER);
		add(CardPanel);

		int minCardPaneH = contentPaneH
				- setInputDataButton.getPreferredSize().height
				- combo.getPreferredSize().height
				- calcStartButton.getPreferredSize().height;

		//カードレイアウトパネルに追加するパネルを作成
		//同時にtextFieldをメンバ変数のhashmap仁登録しておく
		ComponentSetter LayoutOfCards = new ComponentSetter();
		Dimension d = new Dimension(90, 30);

	}

}
