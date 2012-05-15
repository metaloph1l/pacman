package at.ac.foop.pacman.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import at.ac.foop.pacman.application.IGame;

public class MethodCallBuilder {

	public static MethodCall getMethodCall(String method, Object...args) {
		//System.out.println("----------------Begin getMethodCall-------------------");
		Class<?>[] argClasses = null;
		if(args != null) {
			argClasses = new Class<?>[args.length];
			for(int i = 0; i < args.length; i++) {
				argClasses[i] = args[i].getClass();
				// Should check all implemented Interfaces
				// System.out.println("Class of argument: " + args[i].getClass());
				for(int j = 0; j < args[i].getClass().getInterfaces().length; j++) {
					if(args[i].getClass().getInterfaces()[j].toString().contains("java.util.List")) {
						argClasses[i] = List.class;
					}
					else if(args[i].getClass().getInterfaces()[j].toString().contains("java.util.Map")) {
						argClasses[i] = Map.class;
					}
					else {
						// DO NOTHING
					}
				}
				//System.out.println("SuperClass of argument: " + args[i].getClass().getSuperclass());
			}
		}
		Method m = null;
		try {
			//System.out.println("Getting Method: " + method);
			//System.out.println("Args: " + argClasses);
			m = IGame.class.getMethod(method, argClasses);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
		MethodCall retCall = new MethodCall(m, Arrays.asList(args));
		//System.out.println("Args from MethodCall: " + retCall.getArgs());
		//System.out.println("----------------End getMethodCall-------------------");
		
		return retCall;
	}
	
	public static MethodCall getMethodCallWithInterfaces(String method, List<Object> objs, List<Class<?>> classes) {
		System.out.println("----------------Begin getMethodCall-------------------");
		Class<?>[] argClasses = null;
		if(classes != null) {
			argClasses = (Class<?>[])classes.toArray();
		}
		
		Method m = null;
		try {
			m = IGame.class.getMethod(method, argClasses);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
		MethodCall retCall = new MethodCall(m, objs);
		System.out.println("----------------End getMethodCall-------------------");
		
		return retCall;
		
	}
}
