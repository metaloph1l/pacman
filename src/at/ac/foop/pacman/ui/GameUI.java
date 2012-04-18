package at.ac.foop.pacman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import at.ac.foop.pacman.application.client.GameController;
import at.ac.foop.pacman.application.client.GameState;
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
public class GameUI extends JFrame implements Observer {
	// Constants
	public static String TITLE = "Multiplayer Pacman (2012)";

	// Fields
	private int width, height;
	private JPanel[][] squares;
	private JPanel container;
	private GameController controller;

	// Constructors
	public GameUI(int width, int height, GameController controller) {
		this.controller = controller;
		if(controller != null) {
			controller.addObserver(this);
		}
		//TODO: if the controller object is null, then the UI must inform
		//      the user that the client could not connect to the game server.
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
		JButton button = new JButton("HEADER HEADER HEADER");
		this.add(button, BorderLayout.NORTH);
		button = new JButton("SCORE BOARD");
		this.add(button, BorderLayout.EAST);

		// adjust the size of the window
		this.setPreferredSize(new Dimension(width * 16 + 100, height * 16 + 40));
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

	@Override
	public void update(Observable arg0, Object arg1) {
		GameState state = controller.removeState();
		switch(state) {
			case NEW_MAP:
				System.out.println("[UI] New map loadded");
				break;
			case NEW_COLOR:
				System.out.println("[UI] Color changed");
				break;
			case NEW_PLAYER:
				System.out.println("[UI] Player joined");
				break;
			case NEW_POSITION: break;
			case NEW_TURN: break;
			case PLAYER_READY: break;
			default: throw new RuntimeException("[Error] Unimplemented GameState type");
		}
	}
}
