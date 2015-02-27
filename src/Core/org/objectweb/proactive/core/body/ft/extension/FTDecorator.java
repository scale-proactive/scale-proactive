package org.objectweb.proactive.core.body.ft.extension;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.ReifiedObjectDecorator;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class FTDecorator extends ReifiedObjectDecorator {

	private static final long serialVersionUID = 1L;
	
	public static final String keyMethod = "_checkpoint_request" ;
	
	protected static Logger logger = ProActiveLogger.getLogger(
    		Loggers.FAULT_TOLERANCE_EXTENSION);
	
	public FTDecorator(Body body) {
		super(body);
	}
	
	public void _checkpoint_request() {
		if (logger.isDebugEnabled()) {
			logger.debug("About to checkpoint (through " + keyMethod + 
				" method) from reified object decorator.");
		}
		((AbstractBody) this.body).getFTManager().checkpoint(null);
	}

	@Override
	public void decorate() {
		
	}

	public boolean matchesKeyMethod(String name) {
		return keyMethod.equals(name);
	}

}
