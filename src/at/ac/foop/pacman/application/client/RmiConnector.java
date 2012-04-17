package at.ac.foop.pacman.application.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.ui.GameUI;
import at.ac.foop.pacman.util.ClientSettings;
import at.ac.foop.pacman.util.PropertyLoader;

/**
 *
 * @author Phil
 */
public class RmiConnector {

	private IGame controller;
	private IGameServer server;

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
			System.out.println("server reference received");
			System.out.println(server.downloadMap());

			controller = new GameController(server);
			UnicastRemoteObject.exportObject(controller, 0);

			((GameController)controller).init("Test");
			System.out.println("connected to server");
			return (GameController)controller;
		} catch (MalformedURLException e) {
			// TODO: exception handling
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO: exception handling
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO: exception handling
			e.printStackTrace();
		}
		return null;
	}
}
