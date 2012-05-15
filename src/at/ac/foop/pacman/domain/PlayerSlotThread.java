package at.ac.foop.pacman.domain;

import java.lang.reflect.InvocationTargetException;
import java.rmi.ConnectException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.gameserver.GameController;
import at.ac.foop.pacman.util.MethodCall;

public class PlayerSlotThread implements Runnable {

	private LinkedBlockingQueue<MethodCall> methodCalls;
	private IGame callback;
	private Long playerId;
	private boolean run = true;
	private final Logger logger;

	public PlayerSlotThread(Long playerId) {
		this.playerId = playerId;
		methodCalls = new LinkedBlockingQueue<MethodCall>();
		logger = Logger.getLogger(PlayerSlotThread.class);
	}

	@Override
	public void run() {
		logger.info("Player " + playerId + " in this thread");
		while(run) {
			MethodCall currCall = null;
			try {
				currCall = this.methodCalls.take();
			} catch (InterruptedException e) {
				logger.error("ERROR", e);
			}
			if(currCall != null) {
				this.notifyCallback(currCall);
			}
		}
	}

	public void notifyCallback(MethodCall currCall) {
		try {
			if(callback != null) {
				currCall.getMethod().invoke(callback, currCall.getArgs().toArray());
			}
		} catch (IllegalArgumentException e) {
			logger.error("ERROR", e);
		} catch (IllegalAccessException e) {
			logger.error("ERROR", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if(cause instanceof ConnectException) {
				GameController.getCurrentInstance().disconnect(playerId);
				logger.warn("Player " + playerId + " went offline");
			} else {
				logger.error("ERROR", e);
			}
		} catch (Exception ex) {
			logger.error("ERROR", ex);
		}
	}

	public void addMethodCall(MethodCall mCall) {
		if(this.methodCalls == null) {
			methodCalls = new LinkedBlockingQueue<MethodCall>();
		}
		// System.out.println("MethodCall: " + mCall);
		this.methodCalls.add(mCall);
	}

	public void stop() {
		this.run = false;
		this.methodCalls.clear();
		this.callback = null;
	}

	public IGame getCallback() {
		return callback;
	}

	public void setCallback(IGame callback) {
		// if we reset the callback then we also reset the methodCalls
		if(callback == null && this.methodCalls != null) {
			this.methodCalls.clear();
		}
		this.callback = callback;
	}
}
