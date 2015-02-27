package org.objectweb.proactive.core.body;

import org.objectweb.proactive.Body;

public abstract class ReifiedObjectDecorator implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	protected Body body;
	
	private Object reifiedObject;
	
	protected ReifiedObjectDecorator(Body body) {
		this.body = body;
		this.reifiedObject = this.body.getReifiedObject();
	}
	
	public Object getReifiedObject() {
		return this.reifiedObject;
	}
	
	public abstract void decorate();
}
