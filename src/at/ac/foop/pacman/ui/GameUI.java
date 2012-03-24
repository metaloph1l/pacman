package at.ac.foop.pacman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.ui.panels.EmptyField;
import at.ac.foop.pacman.ui.panels.WallField;

/**
 * The main entry point for the gui. It shows the
 * Game Window, displays the labyrinth and handles
 * user input.
 * 
 * @author
 * Sebastian Geiger: Intial Design
 *
 */
public class GameUI extends JFrame {
	// Constants
	public static String TITLE = "Multiplayer Pacman (2012)";

	// Fields
	int width, height;
	JPanel[][] squares;
	JPanel container;

	// Constructors
	public GameUI(int width, int height) {
		this.width = width;
		this.height = height;
		squares = new JPanel[width][height];
		showLabyrinth();
		this.addKeyListener(new DirectionListener());
	}

	private void showLabyrinth() {
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.container = new JPanel();
		this.container.setLayout(new GridLayout(height, width));

		// initialize the container
		for (JPanel[] squareRow : squares) {
			for (JPanel square : squareRow) {
				if (Math.random() < 0.5)
					square = new EmptyField();
				else
					square = new WallField();
				container.add(square);
			}
		}
		this.add(container, BorderLayout.CENTER);

		// adjust the size of the window
		this.setPreferredSize(new Dimension(width * 16, height * 16));
		this.pack();
		this.setLocationRelativeTo(null); // Position game on screen center
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * Listens to the arrow keys on the keyboard and
	 * forward them to the pacman controller. 
	 * @author Sebastian Geiger
	 *
	 */
	class DirectionListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent event) {
			int key = event.getKeyCode();
			Direction direction;
			switch(key) {
				case(KeyEvent.VK_LEFT):
					direction = Direction.LEFT;
					break;
				case(KeyEvent.VK_RIGHT): 
					direction = Direction.RIGHT;
					break;
				case(KeyEvent.VK_UP): 
					direction = Direction.UP;
					break;
				case(KeyEvent.VK_DOWN): 
					direction = Direction.DOWN;
					break;
				default: return;
			}
			//TODO: the the new direction in the pacman controller
			JOptionPane.showMessageDialog(null, "Direction adjusted: " + direction);
		}

		@Override
		public void keyReleased(KeyEvent event) { }

		@Override
		public void keyTyped(KeyEvent event) { }
	}
}
