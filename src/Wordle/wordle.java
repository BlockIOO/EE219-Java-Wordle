package Wordle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// uses the dictionary class provided
public class wordle extends Dictionary implements ActionListener {
	private JFrame frame, temp, settings;
	private JLabel[][] labels;
	private JLabel msg;
	private JButton button;
	private int width, height, size, guesses;
	private int[][] results;
	private int[] hints;
	private String[] guess;
	private String secret;
	private boolean play, difficulty, dark, contrast;
	private JCheckBox[] checks;
	
	public wordle(int w, int h, int s) {
		super("dict.txt");
		width = w;
		height = h;
		size = s;
		
		frame = new JFrame("Wordle");
		
		frame.setSize(size*width+2+40, size*height+2+40);
		frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		frame.setLayout(new GridLayout(height+1, width, 0, 0));
		
		temp = new JFrame();
		msg = new JLabel("", 0);
		temp.add(msg);		
		temp.addKeyListener(
				new KeyAdapter() {
					public void keyTyped(KeyEvent e) {
						temp.setVisible(false);
				}
			}
		);
		
		settings = new JFrame("Settings");
		settings.setSize(300, 200);
		settings.getRootPane().setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		settings.setLayout(new GridLayout(3, 1, 0, 0));
		settings.setResizable(false);
		checks = new JCheckBox[5];
		checks[0] = new JCheckBox("Hard Mode");
		checks[1] = new JCheckBox("Dark Theme");
		checks[2] = new JCheckBox("High Contrast Mode");
		for (int i = 0; i < 3; i++) {
			checks[i].addActionListener(this);
			settings.add(checks[i]);
		}

		
		// detects keyboard presses for letters, backspace and delete
		frame.addKeyListener(
			new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					if (play == true) {
						if (('a' <= e.getKeyChar()) && (e.getKeyChar() <= 'z') && (guess[guesses].length() < width)) {
							guess[guesses] += "a".replace('a', e.getKeyChar()).toUpperCase();
//							System.out.println(guess[guesses]);
						}
						if ((e.getKeyChar() == KeyEvent.VK_BACK_SPACE) && (guess[guesses].length() > 0)){
							guess[guesses] = new String(guess[guesses].substring(0, (guess[guesses].length())-1));
//							System.out.println(guess[guesses]);
						}
						if (e.getKeyChar() == KeyEvent.VK_ENTER){
							if (guess[guesses].length() == width) {
								if (contains(guess[guesses]) == true) {
									boolean playable = true;
									results[guesses] = result(guess[guesses], secret);
									//hard mode
									if (difficulty == true) {
										for (int i = 0; i < width; i++) {
											if (hints[i] > results[guesses][i]) {
												if (hints[i] == 2) {
													msg.setText("Text must contain " + secret.substring(i, i+1));
												}
												else {
													msg.setText("Position " + (i+1) + " must contain " + secret.substring(i, i+1));
												}
												temp.setVisible(true);
												playable = false;
											}
										}
									}
									if (playable == true) {
										if (difficulty == true) {
											hints = results[guesses];											
										}
										else {
											for (int i = 0; i < width; i++) {
												if (results[guesses][i] > hints[i]) {
													hints[i] = results[guesses][i];
												}
											}
										}
										results[guesses] = result(secret, guess[guesses]);
										if ((guesses+1 >= height) || (guess[guesses].equals(secret))) {
											frame.add(button);
											frame.setVisible(true);
											play = false;
											if (guess[guesses].equals(secret)) {
												switch (guesses) {
													case 0:
														msg.setText("Genius");
														break;
													case 1:
														msg.setText("Magnificent");
														break;
													case 2:
														msg.setText("Impressive");
														break;
													case 3:
														msg.setText("Splendid");
														break;
													case 4:
														msg.setText("Great");
														break;
													case 5:
														msg.setText("Phew");
														break;
												}
												if (guesses > 5) {
													msg.setText("Good Job");
												}
												temp.setVisible(true);
											}
											else {
												System.out.println(guesses);
												msg.setText(secret);
												temp.setVisible(true);
											}
										}
										guesses++;
									}
									else {
										results[guesses] = new int[width];
									}
	//							System.out.println(guess[guesses]);
								}
								else {
									msg.setText("Not in word list");
									temp.setVisible(true);
								}
							}
							else {
								msg.setText("Not enough letters");
								temp.setVisible(true);
							}
						}
						update();
					}
					if (e.getKeyChar() == KeyEvent.VK_ESCAPE){
						settings.setVisible(true);
						update();
					}
				}
			}
		);
					
		// add panels
		labels = new JLabel[height+1][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				labels[i][j] = new JLabel("", 0);
				labels[i][j].setLayout(new FlowLayout(FlowLayout.CENTER, size/2, size/2));
				labels[i][j].setOpaque(true);
				int left = 2, right = left, top = left, bottom = left;
				if (i == 0) {
					top *= 2;
				}
				if (i+1 == height) {
					bottom *= 2;
				}
				if (j == 0) {
					left *= 2;
				}
				if (j+1 == width) {
					right *= 2;
				}
				labels[i][j].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.LIGHT_GRAY));
				labels[i][j].setBackground(Color.WHITE);
				frame.add(labels[i][j]);
			}
		}
		
		// add play button in centre
		for (int i = 0; i < width/2; i++) {
			labels[height][i] = new JLabel("", 0);
			frame.add(labels[height][i]);
		}
		button = new JButton("PLAY");
		
		// Start game
		newgame();

		temp.setSize(200, 80);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	
	// updates colours
	public void update() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//need to check length because null
				if (j < guess[i].length()) {
					labels[i][j].setText(guess[i].substring(j, j+1));
				}
				else {
					labels[i][j].setText("");					
				}
				if ((results[i][j] == 0) && (dark == false)) {
					labels[i][j].setForeground(Color.BLACK);
				}
				else {
					labels[i][j].setForeground(Color.WHITE);				
				}
				switch(results[i][j]) {
				case 0:
					if (dark == false) {
						labels[i][j].setBackground(Color.WHITE);							
					}
					else {
						labels[i][j].setBackground(Color.BLACK);						
					}
					break;
				case 1:
					labels[i][j].setBackground(Color.GRAY);
					break;
				case 2:
					if (contrast == false) {
						labels[i][j].setBackground(new Color(255, 196, 37));
					}
					else {
						labels[i][j].setBackground(Color.CYAN);
					}
					break;
				case 3:
					if (contrast == false) {
						labels[i][j].setBackground(Color.GREEN);
					}
					else {
						labels[i][j].setBackground(Color.ORANGE);
					}
					break;
				}
			}
		}
	}
	
	
	// checks guess against answer
	public int[] result(String secretword, String guessword) {
		//0 = empty, 1 = unused, 2 = right letter wrong place, 3 = right letter right place
		int[] output = new int[width];
		String remaining = new String();
		for (int i = 0; i < width ;i++) {
			if (secretword.charAt(i) == guessword.charAt(i)) {
				output[i] = 3;
				remaining += " ";
			}
			else {
				output[i] = 1;
				remaining += secretword.charAt(i);
			}
		}
		for (int i = 0; i < width; i++) {
			if ((output[i] == 1) && (remaining.contains(guessword.substring(i, i+1)))) {
				output[i] = 2;
				remaining = remaining.substring(0, remaining.indexOf(guessword.substring(i, i+1))) + " " + remaining.substring(remaining.indexOf(guess[guesses].substring(i, i+1))+1, remaining.length());
			}
//		System.out.println(remaining);
		}
		return output;
	}
	
	
	// resets everything
	public void newgame() {
		results = new int[height][width];
		guess = new String[height];
		for (int i = 0; i < height; i++) {
			guess[i] = new String("");
		}
		guesses = 0;
		hints = new int[width];
		secret = new String(pickRandomWord());
		System.out.println(secret);
		update();
		play = true;
		update();
		frame.remove(button);
		frame.setVisible(false);
		frame.setVisible(true);
		// add play button in centre
		button = new JButton("PLAY");
		button.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if ((e.getActionCommand().equals("PLAY")) && (play == false)) {
			newgame();
		}
		if (e.getActionCommand().equals("Hard Mode")) {
			difficulty = !difficulty;
			update();
		}
		if (e.getActionCommand().equals("Dark Theme")) {
			dark = !dark;
			update();
		}
		if (e.getActionCommand().equals("High Contrast Mode")) {
			contrast = !contrast;
			update();
		}
	}
	
	
	public static void main(String args[]) {
		wordle game = new wordle(5, 6, 97);
	}
}