package at.ac.foop.pacman.ui.drawings;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * A simple oval Button that is shown on before the game starts.
 * @author Ralph Hoch
 */
public class OvalButton extends Button {  
	private static final long serialVersionUID = 6675466180492977128L;

	public OvalButton(String title) {
		super(title);
	}

	public void paintComponent(Graphics g) {
		System.out.println("OvalButton.width: " + this.getWidth());
		g.setColor(Color.WHITE);  
		g.fillRect(0, 0, 200, 200);  
		Graphics2D g2D = (Graphics2D)g;  
		g2D.setColor(Color.yellow);    
		g2D.fillOval(0,0, this.getWidth()-2, this.getHeight()-2);  
		g2D.setColor(Color.red);  
		g2D.drawOval(0, 0, this.getWidth()-2, this.getHeight()-2);  
		g2D.drawString("Ready", 10, this.getHeight()/2);  
		//this.getUI().paint(g, this);
	}
}
