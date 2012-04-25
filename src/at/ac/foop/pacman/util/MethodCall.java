package at.ac.foop.pacman.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodCall {
	
	private List<Object> args;
	private Method method;
	
	public MethodCall() {
	}
	
	public MethodCall(Method m) {
		this.method = m;
	}
	
	public MethodCall(Method m, List<Object> args) {
		this.method = m;
		this.args = args;
	}
	
	public void addArg(Object arg) {
		if(this.args == null) {
			this.args = new ArrayList<Object>();
		}
		this.args.add(arg);
	}
	
	public List<Object> getArgs() {
		return args;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
