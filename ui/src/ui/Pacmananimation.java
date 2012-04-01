package uitestcode;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class Pacmananimation extends JPanel{
	  double dphi ;
	  double phi;
	  Shape arcs;
	  Color[] colors = {Color.orange,Color.BLUE};
	  int timerint;
	  int colorint;
	  int groesser;
	 
	  public Pacmananimation() {
		colorint=0;
		timerint=0;
		phi=0.0;
		dphi=360.0;
		groesser=-1;

		setPreferredSize(new Dimension(400, 400));			
		  
		//perform action every 2 miliseconds
		new Timer(5, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animatedPacman();
				timerint++;
				if (timerint>1000){
					phi+=90.0;
					if (phi>360.0) {
						phi-=360;
					}
					colorint++;
					if (colorint>1){
						colorint=0;
					}
					timerint=0;
				}
				dphi+=groesser;
				if (dphi>359){
					groesser=-1;
				}
				if (dphi<300)
				{
					groesser=1;
				}
			}
		}).start();
	  }
	  
	  public void animatedPacman() {

		  double arcphi=(dphi-360)*(-1)/2+phi;
			if (arcphi>360.0) {
				arcphi-=360;
			}
		  arcs = new Arc2D.Double(10, 10, 380, 380, arcphi, dphi, Arc2D.PIE);
		  
		  repaint();
	  }

	  public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		setBackground(Color.black);
			
		  g2d.setColor(colors[colorint]);
		  g2d.fill(arcs);
		  g2d.setColor(Color.black);
		  g2d.draw(arcs);
	  }

	  public static void main(String[] args) {
		JFrame frame = new JFrame("ShapeExample1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Pacmananimation pacanime = new Pacmananimation();
		frame.getContentPane().add(pacanime, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	  }
	}