package functionalTests.loggingRequests.deployment.deadlock;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;

/**
 * Created by pkhvoros on 6/5/15.
 */
public class SecondActiveObjectDeployment implements RunActive,Serializable { 
	
	private static final long serialVersionUID = 1L;

	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }

    public StringWrapper run(StubObject first){
        return ((FirstActiveObjectDeployment) first).run(PAActiveObject.getStubOnThis());
    }

}
