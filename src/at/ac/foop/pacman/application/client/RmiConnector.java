package at.ac.foop.pacman.application.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.util.ClientSettings;
import at.ac.foop.pacman.util.PropertyLoader;

/**
 *
 * @author Phil
 */
public class RmiConnector {

	private IGame controller;
	private IGameServer server;
	private final Logger logger = Logger.getLogger(RmiConnector.class);

	public GameController init() {
		String url = PropertyLoader.getInstance().getProperty(
				ClientSettings.CLIENT_CONFIG,
				ClientSettings.SERVER_URL,
				ClientSettings.DEFAULT_SERVER_URL
			);

		Integer port = Integer.parseInt(PropertyLoader.getInstance().getProperty(
				ClientSettings.CLIENT_CONFIG,
				ClientSettings.SERVER_PORT,
				ClientSettings.DEFAULT_SERVER_PORT)
			);

		try {
			server = (IGameServer)Naming.lookup("rmi://" + url + "/Server");
			logger.debug("[RMI] Server lookup complete");

			controller = new GameController(server);
			UnicastRemoteObject.exportObject(controller, 0);

			((GameController)controller).init();
			return (GameController)controller;
		} catch (MalformedURLException e) {
			// TODO: exception handling
			logger.info("Error", e);
		} catch (NotBoundException e) {
			// TODO: exception handling
			logger.info("Error", e);
		} catch (RemoteException e) {
			// TODO: exception handling
			logger.info("Error", e);
		}
		return null;
	}
}
