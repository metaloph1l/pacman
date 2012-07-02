package at.ac.foop.pacman.ui.drawings;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.domain.PacmanColor;
import at.ac.foop.pacman.domain.PlayerOutcome;
import at.ac.foop.pacman.domain.SquareType;
import at.ac.foop.pacman.ui.UI;

/**
 * 
 * @author Stefan
 * 
 */
public class Drawing extends JPanel {

	private static final long serialVersionUID = 89327207047038380L;
	private final Graphics2D g2d;
	private final int width, height;
	private final UI parent;
	private final Logger logger;

	public Drawing(Graphics g, UI newparent) {
		super.paint(g);
		this.logger = Logger.getLogger(Drawing.class);
		this.g2d = (Graphics2D) g;
		this.parent = newparent;

		this.width = this.parent.getWidth();
		this.height = this.parent.getHeight();

		// height und width von hier nehmen

	}
	
	public void initializeMapValues() {
		if (this.parent.getUiController().getMap() == null) {
			logger.info("No Map");
		}

		this.parent.labHeight = this.parent.getUiController().getMap().getHeight();
		this.parent.labWidth = this.parent.getUiController().getMap().getWidth();
		this.parent.labStepY = ((this.height - 55) / this.parent.labHeight);
		this.parent.labStepX = ((this.width - 155) / this.parent.labWidth);
		this.parent.labStartY = (this.height - 55) - this.parent.labStepY * this.parent.labHeight + 50;
		this.parent.labStartX = (this.width - 155) - this.parent.labStepX * this.parent.labWidth + 5;
	
	}
	
	public void drawGameStatistics() {
		String strGameStatistics = "Game Statistics...";
		GradientPaint gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
		g2d.setPaint(gp2);
		Font font = new Font("Arial", Font.BOLD, 25);
		g2d.setFont(font);
		int strLength = (int) (g2d.getFontMetrics().getStringBounds(strGameStatistics, g2d).getWidth());
		int startX = (this.width / 2 - strLength / 2);
		int startY = 80;
		
		g2d.drawString(strGameStatistics, startX, startY);
		
		String strGameOutcome = "Game Outcome: " + this.parent.getUiController().controller.getGameOutcome();
		strLength = (int) (g2d.getFontMetrics().getStringBounds(strGameOutcome, g2d).getWidth());
		startX = (this.width / 2 - strLength / 2);
		g2d.drawString(strGameOutcome, startX, 120);
		
		Map<Long, PlayerOutcome> playerOutcomes = this.parent.getUiController().controller.getPlayerOutcome();
		Iterator<Entry<Long, PlayerOutcome>> it = playerOutcomes.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Long, PlayerOutcome> pairs = it.next();
	        if(pairs.getKey().equals(this.parent.getUiController().getPlayerId())) {
				gp2 = new GradientPaint(0, 0, Color.GREEN, 5, 20, Color.BLUE, true);
				g2d.setPaint(gp2);
			}
			else {
				gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
				g2d.setPaint(gp2);
			}
	        String playerOutcome = "Player " + pairs.getKey() + ": " + pairs.getValue() + "" +
	        		" (" + this.parent.getUiController().getGamePointsForPlayer(pairs.getKey()) + " Points)";
	        strLength = (int) (g2d.getFontMetrics().getStringBounds(playerOutcome, g2d).getWidth());
	        startX = (this.width / 2 - strLength / 2);
			g2d.drawString(playerOutcome, startX, 160 + (pairs.getKey()*40));
	    }
	}
	
	public void drawRoundStatistics() {
		GradientPaint gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
		g2d.setPaint(gp2);
		Font font = new Font("Arial", Font.BOLD, 25);
		g2d.setFont(font);
		
		for (int i = 0; i < this.parent.getUiController().getPlayers().size(); i++) {
			if(this.parent.getUiController().getPlayers().get(i).getId().equals(this.parent.getUiController().getPlayerId())) {
				gp2 = new GradientPaint(0, 0, Color.GREEN, 5, 20, Color.BLUE, true);
				g2d.setPaint(gp2);
			}
			else {
				gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
				g2d.setPaint(gp2);
			}
			String strPlayerPoints = "Player "+ this.parent.getUiController().getPlayers().get(i).getId() + ":";
			strPlayerPoints = strPlayerPoints + " " + this.parent.getUiController().getPlayers().get(i).getRoundPoints() + " Points";
			int strLength = (int) (g2d.getFontMetrics().getStringBounds(strPlayerPoints, g2d).getWidth());
			g2d.drawString(strPlayerPoints, this.width / 2 - strLength / 2, 120 + (i*60));
		}
		
		/*OvalButton bonjourButton = new OvalButton("Bonjour");
		bonjourButton.setSize(200,200);
		bonjourButton.setVisible(true);
		bonjourButton.setLocation(300, 300);
		bonjourButton.repaint();
	    this.add(bonjourButton); 
	    this.repaint();*/
	    //this.parent.repaint();
	}
	
	

	public void drawBackground() {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, this.width, this.height);
	}
	
	public void clearScreen() {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, UI.SCREEN_SIZE_WIDTH, UI.SCREEN_SIZE_WIDTH);
	}
	
	public void clearPlayFieldSection() {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 55, UI.SCREEN_SIZE_WIDTH, UI.SCREEN_SIZE_WIDTH);
	}
	
	/**
	 * draws loading screen
	 */
	public void drawLoadingScreen() {
		String strpacman = "Waiting for other players...";
		GradientPaint gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
		g2d.setPaint(gp2);
		Font font = new Font("Arial", Font.BOLD, 25);
		g2d.setFont(font);
		int strLength = (int) (g2d.getFontMetrics().getStringBounds(strpacman, g2d).getWidth());
		int startX = (this.width / 2 - strLength / 2);
		int startY = (this.height / 2);
		
		g2d.drawString(strpacman, startX, startY);
	}

	/**
	 * draws head
	 */
	public void drawHead() {
		// string pacman
		String strpacman = "PACMAN";
		GradientPaint gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
		g2d.setPaint(gp2);
		Font font = new Font("Arial", Font.BOLD, 25);
		g2d.setFont(font);
		int strLength = (int) (g2d.getFontMetrics().getStringBounds(strpacman, g2d).getWidth());
		int start = (this.width / 2 - strLength / 2);
		
		g2d.drawString(strpacman, start, 30);

		// underline
		GradientPaint gp1 = new GradientPaint(0, 0, new Color(125, 125, 125),
				this.width, 110, Color.yellow, true);
		g2d.setPaint(gp1);
		g2d.fillRect(0, 35, this.width, 10);
	}
	
	/**
	 * draws head
	 */
	public void drawStatistics() {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(this.width - 155, 35, 155, this.height);
		
		String strStatistic = "Statistic";
		GradientPaint gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
		g2d.setPaint(gp2);
		Font font = new Font("Arial", Font.BOLD, 25);
		g2d.setFont(font);
		int strLength = (int) (g2d.getFontMetrics().getStringBounds(strStatistic, g2d).getWidth());
		//int start = (this.width / 2 - strLength / 2);
		int start = (this.width - 155/2 - strLength / 2);

		g2d.drawString(strStatistic, start, 30);
		
		for (int i = 0; i < this.parent.getUiController().getPlayers().size(); i++) {
			if(this.parent.getUiController().getPlayers().get(i).getId().equals(this.parent.getUiController().getPlayerId())) {
				gp2 = new GradientPaint(0, 0, Color.GREEN, 5, 20, Color.BLUE, true);
				g2d.setPaint(gp2);
			}
			else {
				gp2 = new GradientPaint(0, 0, Color.yellow, 5, 20, Color.orange, true);
				g2d.setPaint(gp2);
			}
			
			String strPlayer = "Player "+ this.parent.getUiController().getPlayers().get(i).getId() + ":";
			String strPoints = this.parent.getUiController().getPlayers().get(i).getRoundPoints() + " Points";
			g2d.drawString(strPlayer, this.width - 140, 80 + (i*60));
			g2d.drawString(strPoints, this.width - 140, 100 + (i*60));
		}
		
		

		/* // underline
		GradientPaint gp1 = new GradientPaint(0, 0, new Color(125, 125, 125),
				this.width, 110, Color.yellow, true);
		g2d.setPaint(gp1);
		g2d.fillRect(0, 35, this.width, 10); */
	}

	/**
	 * draws Labyrinth
	 */
	public void drawLabyrinth() {
		for (int i = 0; i < parent.labHeight; i++) { // y
			for (int j = 0; j < parent.labWidth; j++) { // x
				// getSquare(x,y)
				if (this.parent.getUiController().getMap().getSquare(j, i).getType() == SquareType.WALL) {
					// wall
					// fillrect(x,y,stepx,stepy)
					GradientPaint gp3 = new GradientPaint(0, 0, new Color(120,
							0, 200), width / 100, height / 20, new Color(128,
							0, 255), true);
					g2d.setPaint(gp3);
					g2d.fillRect(j * parent.labStepX + parent.labStartX, i
							* parent.labStepY + parent.labStartY,
							parent.labStepX, parent.labStepY);
					BasicStroke bs1 = new BasicStroke(2, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL);
					g2d.setColor(new Color(100, 0, 125));
					g2d.setStroke(bs1);
					g2d.drawRect(j * parent.labStepX + parent.labStartX, i
							* parent.labStepY + parent.labStartY,
							parent.labStepX, parent.labStepY);
				} else {
					// Field
					// getSquare(x,y)
					if (this.parent.getUiController().getMap().getSquare(j, i).getPoints() == 2) {
						// normal cookie
						// fillrect(x,y,stepx,stepy)
						g2d.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER,
								this.parent.getPacmanAnimation().getAlphaCookieAnimation()));
						g2d.setColor(new Color(255, 255, 100));
						g2d.fillRect(j * parent.labStepX + parent.labStartX
								+ parent.labStepX * 3 / 8, i * parent.labStepY
								+ parent.labStartY + parent.labStepY * 3 / 8,
								parent.labStepX / 4, parent.labStepY / 4);
						g2d.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1));
					} else if (this.parent.getUiController().getMap().getSquare(j, i)
							.getPoints() > 2) {
						// colored cookie
						// fillrect(x,y,stepx,stepy)

						GradientPaint gp4 = new GradientPaint(0, 0,
								this.parent.getPacmanAnimation().getColoredCookieColor(),
								this.parent.labStepY / 5,
								this.parent.labStepX / 5, Color.white, true);
						g2d.setPaint(gp4);
						g2d.fillRect(j * parent.labStepX + parent.labStartX
								+ parent.labStepX * 1 / 3, i * parent.labStepY
								+ parent.labStartY + parent.labStepY * 1 / 3,
								parent.labStepX / 3, parent.labStepY / 3);
					} else {
						// empty fiel
						// background already black
					}
				}
			}
		}// endfor paintlab
	}

	private boolean goodToEat(PacmanColor opponentColor, PacmanColor myColor) {
		// Blue eats green
		// green eats red
		// red eats blue
		if ((myColor == PacmanColor.BLUE && opponentColor == PacmanColor.GREEN)
				|| (myColor == PacmanColor.GREEN && opponentColor == PacmanColor.RED)
				|| (myColor == PacmanColor.RED && opponentColor == PacmanColor.BLUE)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * draws Pacmans
	 */
	public void drawPacmans() {
		for (int i = 0; i < this.parent.getUiController().getPlayers().size(); i++) {
			if(this.parent.getUiController().getPlayers().get(i).getPacman().getColor().equals(PacmanColor.BLUE)) {
				g2d.setColor(Color.BLUE);
			}
			else if (this.parent.getUiController().getPlayers().get(i).getPacman().getColor().equals(PacmanColor.GREEN)) {
				g2d.setColor(Color.GREEN);
			}
			else {
				g2d.setColor(Color.RED);
			}
			
			g2d.fill(this.parent.getPacmanAnimation().getPacmanShape()[i]);
			g2d.setColor(Color.BLACK); // background pacman
			g2d.draw(this.parent.getPacmanAnimation().getPacmanShape()[i]);
		}
	}

	/**
	 * draws Pacmans
	 */
	public void drawPacmansBackup() {
		int j = 0;
		// TODO: break if j>Playercount + exception
		// while(this.parent.getUiController().controller.getPlayers().get(j).getId()!=this.parent.getUiController().myID)
		// {
		// j++;
		// }
		for (int i = 0; i < this.parent.getUiController().getPlayers().size(); i++) {
			if (j == i) {
				// Players pacman
				g2d.setColor(Color.BLUE);
			} else {
				// opponents Pacman
				if (this.goodToEat(this.parent.getUiController().getPlayers().get(i)
						.getPacman().getColor(), this.parent.getUiController()
						.getPlayers().get(j).getPacman().getColor())) {
					g2d.setColor(Color.GREEN);
				} else {
					g2d.setColor(Color.RED);
				}
			}
			g2d.fill(this.parent.getPacmanAnimation().getPacmanShape()[i]);
			g2d.setColor(Color.BLACK); // background pacman
			g2d.draw(this.parent.getPacmanAnimation().getPacmanShape()[i]);
		}
	}

	/**
	 * draws bar indicating when colors of Pacmans change
	 */
	public void drawCookies() {
		// colorchangeanimation
		Color helpcolor;
		if (this.parent.getPacmanAnimation().getColorChangeAnimation() < 60) {
			helpcolor = new Color(0, 125, 0);
		} else {
			if (this.parent.getPacmanAnimation().getColorChangeAnimation() < 90) {
				helpcolor = new Color(200, 100, 0);
			} else {
				helpcolor = new Color(125, 0, 0);
			}
		}
		GradientPaint gp5 = new GradientPaint(0, 0, helpcolor, 0, 20,
				Color.yellow, true);
		g2d.setPaint(gp5);
		g2d.fillRect((width * this.parent.getPacmanAnimation().getColorChangeAnimation() / 100), 37,
				(width * (100 - this.parent.getPacmanAnimation().getColorChangeAnimation()) / 100), 6);
	}
}
