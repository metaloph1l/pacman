package at.ac.foop.pacman.ui;


import javax.swing.*;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.ui.drawings.Drawing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

//TODO: relative movement of pacmans

/**
 * UI including keyevents
 * 
 * @author Stefan
 */

public class UI extends JPanel {
	
	public Client parent;
	
	private Timer repaintTimer; // repaints Labyrinth every time is called
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
    
	

    //constructor
	public UI(Client newparent){
		//initialize
		System.out.println("[INIT]");
		this.initialize(newparent);
		System.out.println("[After INIT]");
		JFrame frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 505);
        
        frame.add(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        setFocusable(true);
        
        this.repaintTimer=new Timer(5,new ActionListener() {
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
        		if (coloredCookieTimer>60){
        			coloredCookieTimer=(int)(Math.random()*40);
        			coloredCookieColor=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        		}

        		for (int i=0;i<parent.controller.getPlayers().size();i++){
        			pacmanShape[i]=animatedPacman(i);
        		}
        		
        		//mouth open/closing
        		
    			pacmandphi+=pacmanAnimation;
				if (pacmandphi>360){
					pacmandphi=360;
					pacmanAnimation=-pacmanAnimation;
				}
				if (pacmandphi<300)
				{
					pacmandphi=300;
					pacmanAnimation=-pacmanAnimation;
				}

        		repaint();
        		
//test:
        		repaintTimer.stop();
        	}
        });//end declaration repainttimer

        this.colorChangeTimer=new Timer((int)(this.parent.colorChangeSpeed/100),new ActionListener() {
        	public void actionPerformed(ActionEvent e){
        		if (colorChangeAnimation<100){
        			colorChangeAnimation++;
        		} else {
        			colorChangeTimer.stop();
        			colorChangeAnimation=100;
        		}
        	}
        });
        this.colorChangeTimer.start();
        this.repaintTimer.start();

        addKeyListener(new TAdapter(parent));
	} //end constructor
    
    public void paint(Graphics g) {
    	System.out.println("[Paint]");

        //Renderingoptions
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
               RenderingHints.VALUE_RENDER_QUALITY);

//TODO: implement Scoreboard        
        
        Drawing drawing=new Drawing(g,this);
        drawing.drawBackground();
        drawing.drawHead();
        drawing.drawLabyrinth();
        drawing.drawPacmans();
        drawing.drawCookies();
    }

    private Shape animatedPacman(int player){
    	double arcphi=(this.pacmandphi-360.0f)*(-1)/2+this.pacmanphi;
    	
    	if (this.parent.controller.getPlayers().get(player).getPacman()!=null)
    	{
    		switch (this.parent.controller.getPlayers().get(player).getPacman().getDirection())
    		{
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
		
		int x=this.parent.controller.getPlayers().get(player).getPacman().getLocation().getCoordinate().getX();
		int y=this.parent.controller.getPlayers().get(player).getPacman().getLocation().getCoordinate().getY();
		
		return new Arc2D.Double(x*this.labStepX+this.labStartX,y*this.labStepY+this.labStartY, labStepX, labStepY, arcphi, this.pacmandphi, Arc2D.PIE);
    	}
    	else
    	{
//    		System.out.println("no Pacman");
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
		
		System.out.println("before shape");
		
		this.pacmanShape=new Shape[this.parent.controller.getPlayers().size()];
		for (int i=0;i<this.parent.controller.getPlayers().size();i++)
		{
			System.out.println("in shape");
			this.pacmanShape[i]=this.animatedPacman(i);
		} 
		this.pacmanAnimation=-4;
		this.pacmandphi=360.0f;
		this.pacmanphi=0.0f;
	}
	
    class TAdapter extends KeyAdapter {

    	private Client client;
    	
    	public TAdapter(Client newclient){
    		client=newclient;
    	}

    	public void keyPressed(KeyEvent e) {

// TODO: pause if 'P' pressed    		
    		
        	int keycode = e.getKeyCode();
        	Direction newdirection;

            switch (keycode) {
            case KeyEvent.VK_UP:
            case 'w':
            case 'W':
            	newdirection=Direction.UP;
                break;
            case KeyEvent.VK_RIGHT:
            case 'd':
            case 'D':
            	newdirection=Direction.RIGHT;
                break;
            case KeyEvent.VK_DOWN:
            case 's':
            case 'S':
            	newdirection=Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
            case 'a':
            case 'A':
            	newdirection=Direction.LEFT;
            	break;
            default:
            	return;
            }
            	
            client.sendcmd(newdirection);

        }
    }
}