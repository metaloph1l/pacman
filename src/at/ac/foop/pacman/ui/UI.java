package at.ac.foop.pacman.ui;


import java.awt.Graphics;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.ui.drawings.Drawing;
import at.ac.foop.pacman.ui.drawings.OvalButton;
import at.ac.foop.pacman.ui.drawings.PacmanAnimationTimer;

/**
 * The UI is responsible to create the Window Frame and handle the keyboard inputs.
 * It relies on the Drawing class to paint a Java Graphics2D based game graphic.
 * The keyboard commands are sent to the Client controller. 
 *
 * @author
 * Stefan Gahr (Design and implementation) <br/>
 * Ralph Hoch (Refactoring and Fixes) <br/>
 * Sebastian Geiger (Documentation)
 */
public class UI extends JPanel {

	private static final long serialVersionUID = -5053684701514536944L;
	public static final int SCREEN_SIZE_WIDTH = 600;
	public static final int SCREEN_SIZE_HEIGHT = 505;
	private Client uiController;
	private final Logger logger = Logger.getLogger(UI.class);
	private final Timer repaintTimer; // repaints Labyrinth every time is called
	private PacmanAnimationTimer pacmanAnimation;
	
	private boolean showStatisticScreen;
	private boolean showGameOverScreen;

	//also used for shape
	//TODO: move m,n, ... into Drawing after pacmanshape
	public int labHeight;
	public int labStepY;
	public int labStartY;
	public int labWidth;
	public int labStepX;
	public int labStartX;
	
	private Label loadingLabel;

	//Constructors
	public UI(Client newparent){
		//initialize
		this.initialize(newparent);
		JFrame frame = new JFrame("Pacman");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(UI.SCREEN_SIZE_WIDTH, UI.SCREEN_SIZE_HEIGHT);
		frame.setSize(UI.SCREEN_SIZE_WIDTH, UI.SCREEN_SIZE_HEIGHT);
		frame.setResizable(false);
		frame.add(this);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setFocusable(true);

		pacmanAnimation = new PacmanAnimationTimer(this);
		this.repaintTimer = new Timer(5, pacmanAnimation);

		/*this.colorChangeTimer=new Timer((this.parent.colorChangeSpeed/100),new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (colorChangeAnimation<100){
					colorChangeAnimation++;
				} else {
					colorChangeTimer.stop();
					colorChangeAnimation=100;
				}
			}
		});*/

		addKeyListener(new DirectionListener(uiController));
	} //end constructor
	
	private void initialize(Client newparent){
		this.uiController = newparent;
		this.showStatisticScreen = false;

		logger.debug("before shape");
	}
	
	public void initGameBoard() {
		logger.info("INITIALIZING GAME BOARD");
		//this.colorChangeTimer.start();
		this.repaintTimer.start();
		this.repaint();
	}
	
	public void initializePacmans() {
		this.pacmanAnimation.initializePacmans();
	}

	public void paint(Graphics g) {
		//Renderingoptions
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		if(this.showGameOverScreen == true) {
			Drawing drawing = new Drawing(g, this);
			drawing.drawBackground();
			drawing.drawHead();
			drawing.drawGameStatistics();
		}
		else if(this.showStatisticScreen == true) {
			Drawing drawing = new Drawing(g, this);
			drawing.drawBackground();
			drawing.drawHead();
			drawing.drawRoundStatistics();
		}
		else if(this.repaintTimer != null && this.repaintTimer.isRunning()) {
			try {
				Drawing drawing = new Drawing(g, this);
				drawing.initializeMapValues();
				drawing.drawBackground();
				drawing.drawHead();
				drawing.drawLabyrinth();
				drawing.drawPacmans();
				drawing.drawCookies();
				drawing.drawStatistics();
			} catch(Exception e) {
				Drawing drawing = new Drawing(g, this);
				drawing.drawLoadingScreen();
			}
		}
		else {
			Drawing drawing = new Drawing(g, this);
			drawing.drawBackground();
			drawing.drawHead();
			drawing.drawLoadingScreen();
			// super.paint(g);
		}
	}
	
	public void showEndOfRoundScreen(Map<Long, Long> roundStatistics) {
		this.showStatisticScreen = true;
		OvalButton nextRound = new OvalButton("Next Round");
		nextRound.setSize(100, 25);
		nextRound.setVisible(true);
		nextRound.setLocation(250, 300);
		this.add(nextRound);
		nextRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	    	    //UI.this.statisticScreen.setVisible(false);
	    	    //UI.this.statisticScreen.dispose();
	    	    UI.this.removeAll();
	    	    UI.this.showStatisticScreen = false;
	    	    UI.this.repaint();
	    	    UI.this.getUiController().signalReady();
			}
	    });
		this.repaintTimer.stop();
		this.repaint();
		
		// TODO: show statistics
		/*statisticScreen = new JDialog();
		statisticScreen.setLayout(new BoxLayout(statisticScreen.getContentPane(), BoxLayout.Y_AXIS));
		statisticScreen.setAlwaysOnTop(true);
		statisticScreen.setLocationRelativeTo(null);
		
		Iterator<Entry<Long, Long>> it = roundStatistics.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Long, Long> entry = it.next();
	        JLabel temp = new JLabel("Player " + entry.getKey() + ": \t" + entry.getValue() + " points");
	        temp.setAlignmentX(Component.CENTER_ALIGNMENT);
	        statisticScreen.add(temp);
	    }
	    
	    JButton readyBt = new JButton("Ready");
	    readyBt.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    	    UI.this.statisticScreen.setVisible(false);
	    	    UI.this.statisticScreen.dispose();
	    	    UI.this.setFocusable(true);
	    	    UI.this.getUiController().signalReady();
	    	}
	    });
	    statisticScreen.add(readyBt);
		
	    statisticScreen.setFocusable(true);
	    statisticScreen.pack();
	    this.setFocusable(false);
	    statisticScreen.setVisible(true);
	    */
	}
	
	public void showGameOverScreen() {
		this.showGameOverScreen = true;
		OvalButton nextRound = new OvalButton("New Game");
		nextRound.setSize(100, 25);
		nextRound.setVisible(true);
		nextRound.setLocation(250, 400);
		this.add(nextRound);
		nextRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	    	    //UI.this.statisticScreen.setVisible(false);
	    	    //UI.this.statisticScreen.dispose();
	    	    UI.this.removeAll();
	    	    UI.this.showGameOverScreen = false;
	    	    UI.this.repaint();
	    	    UI.this.getUiController().signalReady();
			}
	    });
		this.repaintTimer.stop();
		this.repaint();
	}

	
	/**
	 * This is the Keyboard Listener. It listens for keyboards presses by the user
	 * and forwards them to the Client Class.
	 */
	class DirectionListener extends KeyAdapter {
		private final Client client;
		public DirectionListener(Client newclient){
			client = newclient;
		}

		public void keyPressed(KeyEvent e) {
			// TODO: pause if 'P' pressed
			int keycode = e.getKeyCode();
			Direction direction;

			switch (keycode) {
				case KeyEvent.VK_UP:
				case 'w':
				case 'W':
					direction=Direction.UP;
					break;
				case KeyEvent.VK_RIGHT:
				case 'd':
				case 'D':
					direction=Direction.RIGHT;
					break;
				case KeyEvent.VK_DOWN:
				case 's':
				case 'S':
					direction=Direction.DOWN;
					break;
				case KeyEvent.VK_LEFT:
				case 'a':
				case 'A':
					direction=Direction.LEFT;
					break;
				default:
					return;
			}
			client.sendcmd(direction);
		}
	}
	
	public Client getUiController() {
		return uiController;
	}

	public void setUiController(Client uiController) {
		this.uiController = uiController;
	}
	
	public PacmanAnimationTimer getPacmanAnimation() {
		return pacmanAnimation;
	}
}