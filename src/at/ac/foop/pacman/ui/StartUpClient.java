package at.ac.foop.pacman.ui;

import at.ac.foop.pacman.application.client.RmiConnector;
import at.ac.foop.pacman.application.client.GameController;

public class StartUpClient {
	public static void main(String[] args) {
		//Initialisation code for the RMI logic
		RmiConnector rmiConnector = new RmiConnector();
		GameController controller = rmiConnector.init();
		//Initialisation of the user interface
		new GameUI(40, 40, controller);
	}
}
