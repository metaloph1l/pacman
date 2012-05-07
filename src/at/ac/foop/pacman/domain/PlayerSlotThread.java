package at.ac.foop.pacman.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.util.MethodCall;

public class PlayerSlotThread implements Runnable {

	private LinkedBlockingQueue<MethodCall> methodCalls;
	private IGame callback;
	private boolean run = true;
	private final Logger logger;

	public PlayerSlotThread() {
		methodCalls = new LinkedBlockingQueue<MethodCall>();
		logger = Logger.getLogger(PlayerSlotThread.class);
	}

	@Override
	public void run() {
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
			// DEBUG:
			/* if (currCall.getArgs() != null) {
				for (int i = 0; i < currCall.getArgs().size(); i++) {
					System.out.println("ArgParams: " + currCall.getArgs().get(i));
					System.out.println("ArgParamsClass: " + currCall.getArgs().get(i).getClass());
				}
			}*/
			currCall.getMethod().invoke(callback, currCall.getArgs().toArray());
		} catch (IllegalArgumentException e) {
			logger.error("ERROR", e);
		} catch (IllegalAccessException e) {
			logger.error("ERROR", e);
		} catch (InvocationTargetException e) {
			logger.error("ERROR", e);
		}
	}

	public void addMethodCall(MethodCall mCall) {
		if(this.methodCalls == null) {
			methodCalls = new LinkedBlockingQueue<MethodCall>();
		}
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
		this.callback = callback;
	}
}
