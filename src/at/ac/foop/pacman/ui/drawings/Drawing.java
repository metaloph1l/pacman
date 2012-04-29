package at.ac.foop.pacman.ui.drawings;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import at.ac.foop.pacman.ui.UI;
import at.ac.foop.pacman.domain.PacmanColor;
import at.ac.foop.pacman.domain.SquareType;

/**
 * 
 * @author Stefan
 *
 */

public class Drawing extends JPanel{
	
	Graphics2D g2d;
	int width, height;
	
    private UI parent;
	
	public Drawing (Graphics g, UI newparent){
		super.paint(g);
		this.g2d = (Graphics2D) g;
		this.parent=newparent;
		
		this.width=this.parent.getWidth();
		this.height=this.parent.getHeight();

		//height und width von hier nehmen
		
		if (this.parent.parent.controller.getMap()==null)
		{
			System.out.println("No Map");
		}
		
		this.parent.labHeight=this.parent.parent.controller.getMap().getHeight();
		this.parent.labWidth=this.parent.parent.controller.getMap().getWidth();
        this.parent.labStepY=(int)((this.height-55)/this.parent.labHeight);
        this.parent.labStepX=(int)((this.width-10)/this.parent.labWidth);
        this.parent.labStartY=(this.height-55)-this.parent.labStepY*this.parent.labHeight+50;
        this.parent.labStartX=(this.width-10)-this.parent.labStepX*this.parent.labWidth+5;
	}
	
	public void drawBackground(){
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0,0,this.width,this.height);		
	}
	
	/**
	 * draws head
	 */
	public void drawHead()
	{
        //string pacman
        String strpacman="PACMAN";
        GradientPaint gp2 = new GradientPaint(0, 0,Color.yellow, 5, 20, Color.orange, true);
        g2d.setPaint(gp2);
        Font font = new Font("Arial", Font.BOLD, 25);
        g2d.setFont(font);
        int strLength=(int)(g2d.getFontMetrics().getStringBounds(strpacman,g2d).getWidth());
        int start=(int)(this.width/2-strLength/2);
  
        g2d.drawString(strpacman, start, 30);
        
        //underline
        GradientPaint gp1 = new GradientPaint(0, 0,new Color(125,125,125), this.width, 110, Color.yellow, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 35, this.width, 10);
	}
	
	/**
	 * draws Labyrinth
	 */
	public void drawLabyrinth()
	{
	    for (int i=0;i<parent.labHeight;i++) //y
        {
        	for (int j=0;j<parent.labWidth;j++) //x
        	{
        		//getSquare(x,y)
        		if (this.parent.parent.controller.getMap().getSquare(j, i).getType()==SquareType.WALL)
        		{
        			//wall 
        			//fillrect(x,y,stepx,stepy)
        	        GradientPaint gp3 = new GradientPaint(0, 0,new Color(120,0,200), width/100, height/20, new Color(128,0,255), true);
        	        g2d.setPaint(gp3);
        			g2d.fillRect(j*parent.labStepX+parent.labStartX, i*parent.labStepY+parent.labStartY, parent.labStepX, parent.labStepY);
            		BasicStroke bs1 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
            		g2d.setColor (new Color(100,0,125));
            		g2d.setStroke(bs1);
            		g2d.drawRect(j*parent.labStepX+parent.labStartX, i*parent.labStepY+parent.labStartY, parent.labStepX, parent.labStepY);
        		}
        		else
        		{
        			//Field
            		//getSquare(x,y)
        			if (this.parent.parent.controller.getMap().getSquare(j, i).getPoints()==2)
        			{
        				//normal cookie
            			//fillrect(x,y,stepx,stepy)
                		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,this.parent.alphaCookieAnimation));
            			g2d.setColor(new Color (255,255,100));
            			g2d.fillRect(j*parent.labStepX+parent.labStartX+parent.labStepX*3/8, i*parent.labStepY+parent.labStartY+parent.labStepY*3/8, parent.labStepX/4, parent.labStepY/4);
                		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
        			}
        			else if (this.parent.parent.controller.getMap().getSquare(j, i).getPoints()>2)
        			{
            			//colored cookie 
            			//fillrect(x,y,stepx,stepy)

            	        GradientPaint gp4 = new GradientPaint(0, 0,this.parent.coloredCookieColor, this.parent.labStepY/5, this.parent.labStepX/5, Color.white, true);
            	        g2d.setPaint(gp4);
            			g2d.fillRect(j*parent.labStepX+parent.labStartX+parent.labStepX*1/3, i*parent.labStepY+parent.labStartY+parent.labStepY*1/3, parent.labStepX/3, parent.labStepY/3);
                	}
        			else
        			{
        				//empty fiel
        				//background already black
        			}
        		}
        	}
        }//endfor paintlab
	}

	private boolean goodToEat(PacmanColor opponentColor, PacmanColor myColor)
	{
		//Blue eats green
		//green eats red
		//red eats blue
		if ((myColor==PacmanColor.BLUE && opponentColor==PacmanColor.GREEN) || (myColor==PacmanColor.GREEN && opponentColor==PacmanColor.RED) || (myColor==PacmanColor.RED && opponentColor==PacmanColor.BLUE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * draws Pacmans
	 */
	public void drawPacmans()
	{
		
		int j=0;
//TODO: break if j>Playercount + exception
//		while(this.parent.parent.controller.getPlayers().get(j).getId()!=this.parent.parent.myID)
//		{
//			j++;
//		}
		for (int i=0;i<this.parent.parent.controller.getPlayers().size();i++){
			if (j==i)
			{
				//Players pacman
				g2d.setColor(Color.BLUE);
			}
			else
			{
				//opponents Pacman
				if (this.goodToEat(this.parent.parent.controller.getPlayers().get(i).getPacman().getColor(),this.parent.parent.controller.getPlayers().get(j).getPacman().getColor()))
				{
					g2d.setColor(Color.GREEN);
				}
				else
				{
					g2d.setColor(Color.RED);
				}
			}
			g2d.fill(parent.pacmanShape[i]);
			g2d.setColor(Color.BLACK); //background pacman
			g2d.draw(this.parent.pacmanShape[i]);
		}
	}
	
	/**
	 * draws bar indicating when colors of Pacmans change
	 */
	public void drawCookies()
	{
		//colorchangeanimation
		Color helpcolor;
		if (this.parent.colorChangeAnimation<60){
			helpcolor=new Color(0,125,0);
		} else {
			if (this.parent.colorChangeAnimation<90){
				helpcolor=new Color(200,100,0);
			} else {
				helpcolor=new Color(125,0,0);
			}			
		}

        GradientPaint gp5 = new GradientPaint(0, 0,helpcolor, 0, 20, Color.yellow, true);
        g2d.setPaint(gp5);
        g2d.fillRect((int)(width*this.parent.colorChangeAnimation/100), 37,(int)( width*(100-this.parent.colorChangeAnimation)/100), 6);
	}
	
}
