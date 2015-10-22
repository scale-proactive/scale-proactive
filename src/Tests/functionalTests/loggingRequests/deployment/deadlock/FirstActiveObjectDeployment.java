package functionalTests.loggingRequests.deployment.deadlock;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import functionalTests.loggingRequests.deployment.LoggerDeadlockSolutionDeploymentTest;

import java.io.Serializable;

/**
 * Created by pkhvoros on 6/5/15.
 */
@DefineGroups({
        @Group(name = "first_run", selfCompatible = true)
})
@DefineThreadConfig(threadPoolSize=1, hardLimit=false)
public class FirstActiveObjectDeployment implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
	
    @MemberOf("first_run")
    public String start(GCMApplication gcmApplication){
        GCMVirtualNode vn = gcmApplication.getVirtualNode(LoggerDeadlockSolutionDeploymentTest.DEADLOCK_VN2_NAME);
        try {
            SecondActiveObjectDeployment secondActiveObject = PAActiveObject.newActive(
            		SecondActiveObjectDeployment.class, null, vn.getANode());
            StringWrapper str = secondActiveObject.run(PAActiveObject.getStubOnThis());
            return str.getStringValue();
        } 
        catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } 
        catch (NodeException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @MemberOf("first_run")
    public StringWrapper run(StubObject second){
        return new StringWrapper(LoggerDeadlockSolutionDeploymentTest.SEARCHED_STRING);
    }
    
}
