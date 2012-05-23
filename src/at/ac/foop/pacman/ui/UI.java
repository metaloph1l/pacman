package at.ac.foop.pacman.ui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.domain.Coordinate;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.ui.drawings.Drawing;

//TODO: relative movement of pacmans

/**
 * UI including keyevents
 *
 * @author Stefan
 */
public class UI extends JPanel {

	public Client parent;
	private final Logger logger = Logger.getLogger(UI.class);

	private final Timer repaintTimer; // repaints Labyrinth every time is called
	public float alphaCookieAnimation; //alpha of cookies (small Value)
	private float plusAlphaCookie; //alpha changes

	private int coloredCookieTimer; // color of cookies change after certain value is reached
	public Color coloredCookieColor; // colored cookies current color

	public int colorChangeAnimation; // Percent of bar painted
	public Timer colorChangeTimer; 	// called every 1/100 of colorChangeSpeed
									// stops after bar is 100%
									// restarts after Client.colorChanges is called

	//TODO: move shape into Drawing
	//Pacman
	public Shape[] pacmanShape; //current shape of pacman
	private int pacmanAnimation; //difference in opening or closing of mouth [degrees]
	private double pacmandphi ; //for animation of pacmanmouth
	private double pacmanphi;	//for animation of pacmanmouth

	//also used for shape
	//TODO: move m,n, ... into Drawing after pacmanshape
	public int labHeight;
	public int labStepY;
	public int labStartY;
	public int labWidth;
	public int labStepX;
	public int labStartX;

	//Constructors
	public UI(Client newparent){
		//initialize
		logger.info("Initializing UI");
		this.initialize(newparent);
		logger.info("Finished initializing UI");
		JFrame frame = new JFrame("Pacman");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(450, 505);
		this.add(new Label("LOADING..."));
		frame.add(this);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setFocusable(true);

		this.repaintTimer= new Timer(5, new AnimationTimer());

		this.colorChangeTimer=new Timer((this.parent.colorChangeSpeed/100),new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (colorChangeAnimation<100){
					colorChangeAnimation++;
				} else {
					colorChangeTimer.stop();
					colorChangeAnimation=100;
				}
			}
		});

		addKeyListener(new DirectionListener(parent));
	} //end constructor
	
	public void initGameBoard() {
		this.colorChangeTimer.start();
		this.repaintTimer.start();
		this.repaint();
	}

	public void paint(Graphics g) {
		logger.debug("Painting Start");

		//Renderingoptions
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		//TODO: implement Scoreboard
		if(this.repaintTimer != null && this.repaintTimer.isRunning()) {
			try {
				Drawing drawing=new Drawing(g,this);
				drawing.drawBackground();
				drawing.drawHead();
				drawing.drawLabyrinth();
				drawing.drawPacmans();
				drawing.drawCookies();
			} catch(Exception e) {
				logger.error("ERROR", e);
			}
		}
		else {
			super.paint(g);
		}

	}

	private Shape animatedPacman(int player) {
		double arcphi=(this.pacmandphi-360.0f)*(-1)/2+this.pacmanphi;
		Pacman pacman = this.parent.controller.getPlayers().get(player).getPacman();
		if (pacman != null) {
			switch (pacman.getDirection()) {
				case UP:{
					arcphi+=90;
					break;
				}
				case LEFT:{
					arcphi+=180;
					break;
				}
				case DOWN:{
					arcphi+=270;
					break;
				}
			}
			if (arcphi>360.0) {
				arcphi-=360;
			}
			Coordinate coordinate = pacman.getLocation().getCoordinate();
			int x=coordinate.getX();
			int y=coordinate.getY();
			return new Arc2D.Double(x*this.labStepX+this.labStartX,
					y*this.labStepY+this.labStartY,
					labStepX, labStepY, arcphi,
					this.pacmandphi, Arc2D.PIE
					);
		} else {
			//logger.debug("no Pacman");
			//return some random Pacman for testing purposes
			//TODO: exception handling
			return new Arc2D.Double(this.labStepX,this.labStepY,labStepX,labStepY,4.0f,4.0f,Arc2D.PIE);
		}
	}

	private void initialize(Client newparent){
		this.colorChangeAnimation=0;
		this.parent=newparent;
		this.alphaCookieAnimation=1.0f;
		this.plusAlphaCookie=-0.01f;
		this.coloredCookieTimer=(int)(Math.random()*40);
		this.coloredCookieColor=Color.RED;

		logger.debug("before shape");

		this.pacmanShape=new Shape[this.parent.controller.getPlayers().size()];
		for (int i=0;i<this.parent.controller.getPlayers().size();i++) {
			logger.debug("in shape");
			this.pacmanShape[i]=this.animatedPacman(i);
		}
		this.pacmanAnimation=-4;
		this.pacmandphi=360.0f;
		this.pacmanphi=0.0f;
	}

	class AnimationTimer implements ActionListener {
		public void actionPerformed(ActionEvent e){

			//change alpha
			alphaCookieAnimation+=plusAlphaCookie;
			if (alphaCookieAnimation>=1.0f)
			{
				plusAlphaCookie=-plusAlphaCookie;
				alphaCookieAnimation=1.0f;
			} else {
				if (alphaCookieAnimation<=0.6f)
				{
					plusAlphaCookie=-plusAlphaCookie;
					alphaCookieAnimation=0.6f;
				}
			}

			//change color of colored cookie
			coloredCookieTimer++;
			if (coloredCookieTimer>60) {
				coloredCookieTimer=(int)(Math.random()*40);
				coloredCookieColor=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			}

			for (int i=0;i<parent.controller.getPlayers().size();i++) {
				pacmanShape[i]=animatedPacman(i);
			}

			//mouth open/closing

			pacmandphi+=pacmanAnimation;
			if (pacmandphi>360) {
				pacmandphi=360;
				pacmanAnimation=-pacmanAnimation;
			}
			if (pacmandphi<300) {
				pacmandphi=300;
				pacmanAnimation=-pacmanAnimation;
			}

			repaint();

			//test:
			repaintTimer.stop();
		}
	} //end of AnimationTimer (inner class)

	class DirectionListener extends KeyAdapter {
		private final Client client;
		public DirectionListener(Client newclient){
			client=newclient;
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
}