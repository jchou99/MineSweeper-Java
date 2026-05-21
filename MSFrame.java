package MineSweeper;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class MSFrame extends JFrame implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;

	private static final String RED_FLAG = "src/MineSweeper/Red Flag.png";
	public static final String MINE = "src/MineSweeper/Mine.png";
	private ImageIcon mineImage = new ImageIcon(MINE);
	private ImageIcon flagImage = new ImageIcon(RED_FLAG);

	private HashMap<JButton, Integer> board = new HashMap<JButton, Integer>();
	private HashMap<Integer, JButton> reverseBoard = new HashMap<Integer, JButton>();

	private HashMap<Integer, JButton> mineBoard = new HashMap<Integer, JButton>();
	private JPanel buttonBoard = new JPanel();
	private GridBagConstraints gbc;

	private Font font = new Font("Times New Roman", Font.BOLD, 24);

	private boolean isWin = true;
	private boolean firstClick = true;

	private JToggleButton dig;
	private JToggleButton flag;
	private JPanel selection;

	private int difficulty = 15;
	private JMenu difficultyMenu;
	private JMenuItem easy;
	private JMenuItem medium;
	private JMenuItem hard;

	private int correctFlag = 0;
	private int flagNumber;
	private int flagAmount;
	private JLabel flags;
	private JPanel flagMenu;
	private HashMap<JButton, Boolean> flagArr = new HashMap<JButton, Boolean>();

	private JMenuBar mb;
	private JMenu restartMenu;
	private JMenuItem restart;
	private Font menuFont = new Font("Times New Roman", Font.BOLD, 15);
	private Color menuColor = new Color(85, 107, 47);

	public MSFrame() {

		buttonBoard.setLayout(new GridBagLayout());
		buttonBoard.setOpaque(false);
		gbc = new GridBagConstraints();

		flagNumber = (int) (Math.pow(difficulty, 2) / 10) - (int) ((Math.pow(difficulty, 2) / 10) % 5);
		flagAmount = flagNumber;
		flags = new JLabel("Flags: " + flagNumber);
		flags.setFont(font);
		flags.setForeground(Color.white);
		flagMenu = new JPanel();
		flagMenu.setOpaque(false);
		flagMenu.setLayout(new FlowLayout(FlowLayout.CENTER));
		flagMenu.add(flags);

		boardSetUp();

		dig = new JToggleButton("Dig", true);
		dig.setFont(font);
		dig.setBackground(new Color(154, 205, 50));
		dig.addChangeListener(this);
		flag = new JToggleButton("Flag", false);
		flag.setFont(font);
		flag.setBackground(new Color(154, 205, 50));
		flag.addChangeListener(this);

		flagImage = resizeImageFlag(flagImage);
		mineImage = resizeImageMine(mineImage);

		selection = new JPanel();
		selection.setPreferredSize(new Dimension(200, 75));
		selection.add(dig);
		selection.add(flag);
		selection.setOpaque(false);

		restart = new JMenuItem("Restart");
		restart.setFont(menuFont);
		restart.setForeground(Color.white);
		restart.setBackground(menuColor);
		restart.addActionListener(this);
		restartMenu = new JMenu("Options");
		restartMenu.setForeground(Color.white);
		restartMenu.setFont(menuFont);
		restartMenu.setBackground(menuColor);
		restartMenu.add(restart);

		easy = new JMenuItem("Easy");
		easy.setFont(menuFont);
		easy.setForeground(Color.white);
		easy.setBackground(menuColor);
		easy.addActionListener(this);
		medium = new JMenuItem("Medium");
		medium.setFont(menuFont);
		medium.setForeground(Color.white);
		medium.setBackground(menuColor);
		medium.addActionListener(this);
		hard = new JMenuItem("Hard");
		hard.setForeground(Color.white);
		hard.setFont(menuFont);
		hard.setBackground(menuColor);
		hard.addActionListener(this);

		difficultyMenu = new JMenu("Difficulty");
		difficultyMenu.setFont(menuFont);
		difficultyMenu.setForeground(Color.white);
		difficultyMenu.add(easy);
		difficultyMenu.add(medium);
		difficultyMenu.add(hard);

		mb = new JMenuBar();
		mb.add(restartMenu);
		mb.add(difficultyMenu);
		mb.setBackground(menuColor);

		setPreferredSize(new Dimension(1000, 1000));
		setLayout(new BorderLayout());
		add(flagMenu, BorderLayout.NORTH);
		add(buttonBoard);
		add(selection, BorderLayout.SOUTH);
		getContentPane().setBackground(menuColor);

		setJMenuBar(mb);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}

	public void boardSetUp() {

		JButton temp = null;

		for (int i = 0; i < Math.pow(difficulty, 2); i++) {

			temp = new JButton("");
			temp.setHorizontalTextPosition(SwingConstants.CENTER);
			temp.setVerticalTextPosition(SwingConstants.CENTER);
			switch (difficulty) {
			case 10 -> {
				temp.setPreferredSize(new Dimension(80, 80));
				break;
			}
			case 15 -> {
				temp.setPreferredSize(new Dimension(50, 50));
				break;
			}
			case 20 -> {
				temp.setPreferredSize(new Dimension(40, 40));
				break;
			}
			}
			temp.setFont(font);
			temp.addActionListener(this);
			temp.setBorder(new EmptyBorder(0, 0, 0, 0));

			if (checkerBoard(i)) {
				temp.setBackground(new Color(154, 205, 50));
			} else {
				temp.setBackground(new Color(107, 142, 35));
			}

			gbc.gridwidth = 1;
			gbc.gridx = i % difficulty;
			gbc.gridy = i / difficulty;

			buttonBoard.add(temp, gbc);
			reverseBoard.put(i, temp);
			board.put(temp, i);

		}
	}

	/**
	 * Generates an array of ints with the location of the mines
	 * 
	 * @param click - the location of the first click
	 * @return an array of ints with the location of the mines
	 */
	public int[] minesSetUp(JButton button) {
		int mine[] = new int[flagAmount];
		HashSet<Integer> mineSet = new HashSet<Integer>();
		Random rng = new Random();
		int firstClick = board.get(button);
		int rand = rng.nextInt((int) Math.pow(difficulty, 2));

		for (int i = 0; i < mine.length; i++) {
			while (mineSet.contains(rand) || rand == firstClick) {
				rand = rng.nextInt((int) Math.pow(difficulty, 2));
			}

			mine[i] = rand;
			mineSet.add(rand);
		}

		return mine;
	}

	/**
	 * Determines if the location of the board is the darker shade or the lighter
	 * shade
	 * 
	 * @param x - the location of the point
	 * @return - false if it's a darker shade true if it's a lighter shade.
	 */
	public boolean checkerBoard(int x) {
		if (((x % difficulty) % 2) == 0 && ((x / difficulty) % 2) == 0) {
			return true;
		} else if (((x % difficulty) % 2) == 1 && ((x / difficulty) % 2) == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the background color to a shade of tan based on the position of the
	 * button
	 * 
	 * @param button - the button being clicked
	 */
	public void backGroundColor(JButton button) {
		if (checkerBoard(board.get(button))) {
			button.setBackground(new Color(255, 235, 205));
		} else {
			button.setBackground(new Color(210, 180, 140));
		}
		button.setEnabled(false);
	}

	public ImageIcon resizeImageFlag(ImageIcon image) {
		Image img = image.getImage();
		Image resizedImage = null;
		switch (difficulty) {
		case 10 -> {
			resizedImage = img.getScaledInstance(140, 80, Image.SCALE_SMOOTH);
			break;
		}
		case 15 -> {
			resizedImage = img.getScaledInstance(100, 60, Image.SCALE_SMOOTH);
			break;
		}
		case 20 -> {
			resizedImage = img.getScaledInstance(80, 40, Image.SCALE_SMOOTH);
			break;
		}
		}
		image = new ImageIcon(resizedImage);
		return image;
	}

	public ImageIcon resizeImageMine(ImageIcon image) {
		Image img = image.getImage();
		Image resizedImage = null;

		switch (difficulty) {
		case 10 -> {
			resizedImage = img.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
			break;
		}
		case 15 -> {
			resizedImage = img.getScaledInstance(250, 150, Image.SCALE_SMOOTH);
			break;
		}
		case 20 -> {
			resizedImage = img.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
			break;
		}
		}
		image = new ImageIcon(resizedImage);
		return image;
	}

	public void updateBoard(JButton button) {

		if (flagArr.get(button) != null) {
			if (flagArr.get(button)) {
				flagNumber++;
				flags.setText("Flags: " + flagNumber);
				flagArr.remove(button);
				button.setIcon(null);
			}
		}

		if (firstClick) {
			int[] mine = minesSetUp(button);
			for (int i = 0; i < mine.length; i++) {
				reverseBoard.get(mine[i]).setText("");
				mineBoard.put(mine[i], reverseBoard.get(mine[i]));
			}
			firstClick = false;
		}

		int coord = board.get(button);
		int x = coordinateX(coord);
		int y = coordinateY(coord);
		int bomb = mineDetection(coord);

		if (!button.isEnabled()) {
			return;
		}

		if (bomb > 0) {
			button.setText(bomb + "");
			backGroundColor(button);
		} else if (bomb < 0) {
			button.setIcon(mineImage);
			backGroundColor(button);
		} else {
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if ((x + j) % difficulty >= 0 && (x + j) % difficulty < difficulty && (y + i) % difficulty >= 0
							&& (y + i) % difficulty < difficulty && bomb == 0) {
						if (difficulty > y + i && y + i > -1 && difficulty > x + j && x + j > -1) {
							backGroundColor(button);
							updateBoard(reverseBoard.get(((y + i) * difficulty + (x + j))));
						}
					}
				}
			}

		}

	}

	public void flagCorrectness(JButton button) {
		int coord = board.get(button);
		if (mineChecker(coord)) {
			correctFlag++;
		}
		if (correctFlag == flagAmount) {
			endFrame();
			endGame();
		}
	}

	public void endGame() {
		for (int i = 0; i < reverseBoard.size(); i++) {
			JButton tempB = reverseBoard.get(i);
			if (mineChecker(i) && (flagArr.get(tempB) == null || !flagArr.get(tempB))) {
				tempB.setIcon(mineImage);
			}
			backGroundColor(tempB);

		}
	}

	public JFrame endFrame() {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(500, 250));
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.setBackground(new Color(255, 235, 205, 120));
		JLabel endLabel;
		if (!isWin) {
			endLabel = new JLabel("You Lose");
		} else {
			endLabel = new JLabel("You Win!");
		}
		endLabel.setFont(new Font("Times New Roman", Font.BOLD, 100));
		endLabel.setHorizontalAlignment(SwingConstants.CENTER);
		endLabel.setOpaque(false);

		frame.add(endLabel, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(buttonBoard);
		frame.setVisible(true);

		return frame;
	}

	public int mineDetection(int coord) {
		int bombCounter = 0;
		int x = coordinateX(coord);
		int y = coordinateY(coord);

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (mineChecker(y * difficulty + x)) {
					isWin = false;
					endFrame();
					endGame();
					return -1;
				} else if (difficulty > y + i && y + i > -1 && difficulty > x + j && x + j > -1
						&& mineChecker((y + i) * difficulty + (x + j))) {
					bombCounter++;
				}
			}
		}

		return bombCounter;
	}

	public boolean mineChecker(int coord) {
		for (int i = 0; i < mineBoard.size(); i++) {
			if (mineBoard.containsKey(coord)) {
				return true;
			}
		}

		return false;

	}

	public int coordinateX(int input) {
		int x;
		if (input % difficulty == 0) {
			x = 0;
		} else {
			x = input % difficulty;
		}
		return x;
	}

	public int coordinateY(int input) {
		int y;
		if (input < difficulty) {
			y = 0;
		} else if (input % difficulty == 0) {
			y = input / difficulty;
		} else {
			y = input / difficulty;
		}
		return y;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object click = e.getSource();
		if (click == dig && dig.isSelected()) {
			flag.setSelected(false);
		} else if (click == flag && flag.isSelected()) {
			dig.setSelected(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object click = e.getSource();

		if (click instanceof JButton tempB && board.containsKey(click)) {
			if (dig.isSelected()) {
				updateBoard(tempB);
			} else if (flag.isSelected() && flagNumber != 0) {
				if (flagArr.get(click) == null) {
					flagNumber--;
				}
				flags.setText("Flags: " + flagNumber);
				tempB.setIcon(flagImage);
				flagArr.put(tempB, true);
				flagCorrectness(tempB);
			}
		} else if (click == restart) {
			restart();
		} else if (click == easy) {
			difficulty = 10;
			restart();
		} else if (click == medium) {
			difficulty = 15;
			restart();
		} else if (click == hard) {
			difficulty = 20;
			restart();
		}
	}

	public void restart() {
		isWin = true;
		firstClick = true;
		correctFlag = 0;
		flagNumber = (int) (Math.pow(difficulty, 2) / 10) - (int) ((Math.pow(difficulty, 2) / 10) % 5);
		flagAmount = flagNumber;
		flags.setText("Flags: " + flagNumber);
		flag.setSelected(false);
		dig.setSelected(true);

		flagImage = resizeImageFlag(flagImage);
		mineImage = resizeImageMine(mineImage);

		flagArr.clear();
		mineBoard.clear();
		board.clear();
		reverseBoard.clear();

		buttonBoard.removeAll();
		boardSetUp();
		buttonBoard.revalidate();
		buttonBoard.repaint();

	}

}
