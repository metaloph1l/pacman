package at.ac.foop.pacman.util;

import java.lang.reflect.Method;
import java.util.Arrays;

import at.ac.foop.pacman.application.IGame;

public class MethodCallBuilder {

	public static MethodCall getMethodCall(String method, Object...args) {
		//System.out.println("----------------Begin getMethodCall-------------------");
		Class<?>[] argClasses = null;
		if(args != null) {
			argClasses = new Class<?>[args.length];
			for(int i = 0; i < args.length; i++) {
				argClasses[i] = args[i].getClass();
				//System.out.println("Class of argument: " + args[i].getClass());
			}
		}
		Method m = null;
		try {
			//System.out.println("Getting Method: " + method);
			//System.out.println("Args: " + argClasses);
			m = IGame.class.getMethod(method, argClasses);
		} catch (Exception ex) {
			 return null;
		}
		
		MethodCall retCall = new MethodCall(m, Arrays.asList(args));
		//System.out.println("Args from MethodCall: " + retCall.getArgs());
		//System.out.println("----------------End getMethodCall-------------------");
		
		return retCall;
		
	}
}
