package at.ac.foop.pacman.ui;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.ui.drawings.Drawing;
import at.ac.foop.pacman.ui.drawings.PacmanAnimationTimer;

//TODO: relative movement of pacmans

/**
 * UI including keyevents
 *
 * @author Stefan
 */
public class UI extends JPanel {

	private static final long serialVersionUID = -5053684701514536944L;
	private Client uiController;
	private final Logger logger = Logger.getLogger(UI.class);
	private final Timer repaintTimer; // repaints Labyrinth every time is called
	private PacmanAnimationTimer pacmanAnimation;
	private JDialog statisticScreen;


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
		frame.setSize(600, 505);
		loadingLabel = new Label("LOADING...");
		this.add(loadingLabel);
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

		//TODO: implement Scoreboard
		if(this.repaintTimer != null && this.repaintTimer.isRunning()) {
			this.loadingLabel.setVisible(false);
			try {
				Drawing drawing = new Drawing(g, this);
				drawing.drawBackground();
				drawing.drawHead();
				drawing.drawLabyrinth();
				drawing.drawPacmans();
				drawing.drawCookies();
				drawing.drawStatistics();
			} catch(Exception e) {
				logger.error("ERROR", e);
			}
		}
		else {
			super.paint(g);
		}
	}
	
	public void showEndOfRoundScreen(Map<Long, Long> roundStatistics) {
		// TODO: show statistics
		statisticScreen = new JDialog();
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
	}

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