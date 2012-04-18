package at.ac.foop.pacman.ui;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.application.gameserver.GameController;

public class StartUpGameServer {
	public static void main(String[] args) {
		System.out.println("[Gameserver] Registering RMI class");
		try {
			Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			IGameServer server = GameController.getCurrentInstance();
		    reg.rebind("Server", server);
		    try {
				System.out.println("[Gameserver] Started!");
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
