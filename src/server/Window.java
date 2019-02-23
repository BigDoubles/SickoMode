package server;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Window extends JFrame{

	private static final long serialVersionUID = 1L;
	private static JTextArea output;
	private static JTextField input;
	private static JScrollPane scroll;
	
	public void start(){
		this.setTitle("Sickomode Card Server");
		output = new JTextArea();
		input = new JTextField();
		scroll = new JScrollPane(output);
		this.add(scroll, BorderLayout.CENTER);
		this.add(input, BorderLayout.SOUTH);
		output.setEditable(false);
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void showMessage(String message) {
		output.setText(output.getText() + "\n" + message);
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
	}

}
