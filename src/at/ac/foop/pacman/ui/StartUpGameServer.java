package at.ac.foop.pacman.ui;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.application.gameserver.GameController;

public class StartUpGameServer {
	public static void main(String[] args) {
		//Initialize log4j
		org.apache.log4j.BasicConfigurator.configure();
		Logger logger = Logger.getLogger(StartUpClient.class);
		logger.info("[Gameserver] Registering RMI class");
		try {
			Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			IGameServer server = GameController.getCurrentInstance();
		    reg.rebind("Server", server);
		    try {
				logger.info("[Gameserver] Started!");
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("ERROR", e);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("ERROR", e);
		}
	}
}
