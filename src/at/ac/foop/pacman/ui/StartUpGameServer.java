package at.ac.foop.pacman.ui;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.application.gameserver.Gameserver;

public class StartUpGameServer {
	public static void main(String[] args) {
		try {
			Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			IGameServer server = new Gameserver();
		    reg.rebind("Server", server);
		    try {
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
