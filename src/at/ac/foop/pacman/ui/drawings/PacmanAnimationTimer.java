package at.ac.foop.pacman.ui.drawings;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

import javax.swing.Timer;

import at.ac.foop.pacman.domain.Coordinate;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.ui.UI;

public class PacmanAnimationTimer implements ActionListener {
	
	private UI parent;
	private Shape[] pacmanShape; //current shape of pacman

	private int pacmanAnimation; //difference in opening or closing of mouth [degrees]
	private double pacmandphi ; //for animation of pacmanmouth
	private double pacmanphi;	//for animation of pacmanmouth
	
	private float alphaCookieAnimation; //alpha of cookies (small Value)
	private float plusAlphaCookie; //alpha changes

	private int coloredCookieTimer; // color of cookies change after certain value is reached
	private Color coloredCookieColor; // colored cookies current color

	private int colorChangeAnimation; // Percent of bar painted
	private Timer colorChangeTimer; 	// called every 1/100 of colorChangeSpeed
									// stops after bar is 100%
									// restarts after Client.colorChanges is called
	
	public PacmanAnimationTimer(UI parent) {
		this.parent = parent;
		this.initialize();
	}
	
	private void initialize() {
		this.colorChangeAnimation=0;
		this.alphaCookieAnimation=1.0f;
		this.plusAlphaCookie=-0.01f;
		this.coloredCookieTimer=(int)(Math.random()*40);
		this.coloredCookieColor=Color.RED;

		this.pacmanAnimation=-4;
		this.pacmandphi=360.0f;
		this.pacmanphi=0.0f;
	}
	
	public void initializePacmans() {
		this.pacmanShape = new Shape[this.parent.getUiController().getPlayers().size()];
		for (int i=0;i < this.parent.getUiController().getPlayers().size();i++) {
			this.pacmanShape[i]=this.animatedPacman(i);
		}
	}
	
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

		for (int i = 0;i < this.parent.getUiController().getPlayers().size(); i++) {
			pacmanShape[i] = animatedPacman(i);
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

		this.parent.repaint();

		//test:
		//repaintTimer.stop();
	}
	
	private Shape animatedPacman(int player) {
		double arcphi = (this.pacmandphi-360.0f)*(-1)/2+this.pacmanphi;
		Pacman pacman = this.parent.getUiController().getPlayers().get(player).getPacman();
		if (pacman != null && pacman.isAlive()) {
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
			return new Arc2D.Double(x*this.parent.labStepX+this.parent.labStartX,
					y*this.parent.labStepY+this.parent.labStartY,
					this.parent.labStepX, this.parent.labStepY, arcphi,
					this.pacmandphi, Arc2D.PIE
					);
		} else {
			//logger.debug("no Pacman");
			//return some random Pacman for testing purposes
			//TODO: exception handling
			return new Arc2D.Double(this.parent.labStepX,this.parent.labStepY, this.parent.labStepX, this.parent.labStepY,4.0f,4.0f,Arc2D.PIE);
		}
	}
	
	/**
	 *
	 * call when colors change
	 * restarts Timer
	 *
	 */
	public void colorChanges(){
		//TODO: call this when color changes
		//TODO: optional: PID-Element for actual time

		this.colorChangeAnimation = 0;
		this.colorChangeTimer.restart();
	}
	
	public Shape[] getPacmanShape() {
		return pacmanShape;
	}

	public void setPacmanShape(Shape[] pacmanShape) {
		this.pacmanShape = pacmanShape;
	}

	public float getAlphaCookieAnimation() {
		return alphaCookieAnimation;
	}

	public void setAlphaCookieAnimation(float alphaCookieAnimation) {
		this.alphaCookieAnimation = alphaCookieAnimation;
	}

	public int getColorChangeAnimation() {
		return colorChangeAnimation;
	}

	public void setColorChangeAnimation(int colorChangeAnimation) {
		this.colorChangeAnimation = colorChangeAnimation;
	}
	
	public Color getColoredCookieColor() {
		return coloredCookieColor;
	}

	public void setColoredCookieColor(Color coloredCookieColor) {
		this.coloredCookieColor = coloredCookieColor;
	}
} 
