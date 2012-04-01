package uitestcode;

import java.awt.Graphics;

import java.awt.Graphics2D;

import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.BasicStroke;

import javax.swing.Timer;
import java.awt.event.*;

public class Pacman extends JPanel{

	
	int[][] lab={
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,1,2,2,2,2,2,1,2,2,2,2,2,1},
			{1,0,0,0,1,0,1,0,0,1,0,0,1,0,1},
			{1,0,1,0,0,0,1,1,0,0,0,0,0,0,1},
			{1,0,0,0,1,0,0,0,0,1,0,1,0,1,1},
			{1,1,0,1,1,0,1,0,1,0,0,1,1,0,1},
			{1,0,0,0,1,0,1,0,1,1,0,0,0,0,1},
			{1,1,0,0,0,0,1,0,0,0,0,1,0,1,1},
			{1,0,0,1,0,1,0,1,0,1,0,1,0,0,1},
			{1,0,1,1,0,0,0,0,0,1,0,0,0,1,1},
			{1,0,0,0,1,0,1,0,1,0,0,1,0,0,1},
			{1,0,1,0,1,0,0,0,0,0,0,0,0,1,1},
			{1,0,0,0,0,0,1,0,1,0,1,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
	};
	int width;
	int height;
	int m;
	int stepm;
	int startm;
	int n;
	int stepn;
	int startn;
	
	Timer timer;
	
	//pacmanpos
	int posx;
	int posy;
	
	double dphi = 0.0;
	
	Color pacmancolor;
	
	public Pacman(){

//        timer = new Timer(20, this);
//        timer.setInitialDelay(190);
//        timer.start();
		
	}
	
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        height=getHeight();
        width=getWidth();
        m=lab.length;
        n=lab[0].length;
        stepm=(int)((height-55)/m);
        stepn=(int)((width-10)/n);
        startm=(height-55)-stepm*m+50;
        startn=(width-10)-stepn*n+5;
        
        posx=1;
        posy=1;

        //Renderingoptions
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
               RenderingHints.VALUE_RENDER_QUALITY);
        
        //Background black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,width,height);
        

        //string pacman
        String strpacman="PACMAN";
//        g2d.setColor(Color.ORANGE);
        GradientPaint gp2 = new GradientPaint(0, 0,Color.yellow, 5, 20, Color.orange, true);
        g2d.setPaint(gp2);
        Font font = new Font("Arial", Font.BOLD, 25);
        g2d.setFont(font);
        int strLength=(int)(g2d.getFontMetrics().getStringBounds(strpacman,g2d).getWidth());
        int start=(int)(width/2-strLength/2);
        g2d.drawString(strpacman, start, 30);
        
        //underline
        GradientPaint gp1 = new GradientPaint(0, 0,Color.orange, 0, 20, Color.yellow, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 35, width, 10);
        
        //paintlab

		//background wall

        for (int i=0;i<m;i++)
        {
        	for (int j=0;j<n;j++)
        	{
        		if (this.lab[i][j]==1) {
        			//wall 
        			//fillrect(x,y,stepx,stepy)
        	        GradientPaint gp3 = new GradientPaint(0, 0,new Color(120,0,200), width/100, height/20, new Color(128,0,255), true);
        	        g2d.setPaint(gp3);//        	        g2d.setColor(new Color(128,0,255));
        			g2d.fillRect(j*stepn+startn, i*stepm+startm, stepn, stepm);
            		BasicStroke bs1 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
            		g2d.setColor (new Color(100,0,125));
            		g2d.setStroke(bs1);
            		g2d.drawRect(j*stepn+startn, i*stepm+startm, stepn, stepm);
        			
        		}
        	}
        }
        
        //drawpacman
    }

    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 505);
       
        frame.add(new Pacman());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}