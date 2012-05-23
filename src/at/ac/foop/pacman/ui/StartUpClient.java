package at.ac.foop.pacman.ui;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.client.GameController;
import at.ac.foop.pacman.application.client.RmiConnector;

public class StartUpClient {
	public static void main(String[] args) {
		//Initialize log4j
		org.apache.log4j.BasicConfigurator.configure();
		try {
			//Initialisation code for the RMI logic
			RmiConnector rmiConnector = new RmiConnector();
			final GameController controller = rmiConnector.init();
			
			Runtime.getRuntime().addShutdownHook( new Thread() { 
				@Override 
				public void run() { 
					try {
						controller.disconnect();
					} catch (RemoteException e) {
						Logger.getLogger(StartUpClient.class).error("ERROR", e);
					}
				  } 
				} ); 

			//Initialisation of the user interface
			//TODO: ID of this player
			Client client = new Client(controller, 10000, controller.getPlayers().get(0).getId());
			
			controller.connect("PLAYER " + controller.getPlayers().get(0).getId());
		} catch(Exception e) {
			Logger.getLogger(StartUpClient.class).error("ERROR", e);
		}
	}
}
