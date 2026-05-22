package gui;

import javax.swing.*;

public class Home {
	private JPanel panel;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Home");
		frame.setContentPane(new Home().panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
