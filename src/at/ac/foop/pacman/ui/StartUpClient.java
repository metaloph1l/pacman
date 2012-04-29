package at.ac.foop.pacman.ui;

import at.ac.foop.pacman.application.client.RmiConnector;
import at.ac.foop.pacman.application.client.GameController;

public class StartUpClient {
	public static void main(String[] args) {
		//Initialize log4j
		org.apache.log4j.BasicConfigurator.configure();
		//Initialisation code for the RMI logic
		RmiConnector rmiConnector = new RmiConnector();
		GameController controller = rmiConnector.init();
		//Initialisation of the user interface
		
//TODO: ID of this player
		new Client(controller, 10000,controller.getPlayers().get(0).getId());

	}
}
