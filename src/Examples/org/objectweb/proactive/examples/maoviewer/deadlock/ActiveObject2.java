package org.objectweb.proactive.examples.maoviewer.deadlock;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


public class ActiveObject2 implements RunActive,Serializable { 

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
	
    public String run(StubObject first){
        return ((ActiveObject1) first).run(PAActiveObject.getStubOnThis());
    }
    
}
