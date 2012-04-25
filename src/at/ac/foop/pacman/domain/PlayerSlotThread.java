package at.ac.foop.pacman.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.SynchronousQueue;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.util.MethodCall;

public class PlayerSlotThread implements Runnable {
	
	private SynchronousQueue<MethodCall> methodCalls;
	private IGame callback;
	private boolean run = true;

	public PlayerSlotThread() {
		methodCalls = new SynchronousQueue<MethodCall>();
	}
	
	@Override
	public void run() {
		while(run) {
			MethodCall currCall = this.methodCalls.poll();
			if(currCall != null) {
				this.notifyCallback(currCall);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void notifyCallback(MethodCall currCall) {
		//MethodQueue mq = ... // get the queue from where you want
	    //mq.add(new MethodInvocation(obj, m, args)
		
		//System.out.println("------------Begin notfiy Callback--------------");
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("------------End notfiy Callback--------------");
	}
	
	public void addMethodCall(MethodCall mCall) {
		//System.out.println(mCall);
		if(this.methodCalls == null) {
			methodCalls = new SynchronousQueue<MethodCall>();
		}
		try {
			this.methodCalls.put(mCall);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
