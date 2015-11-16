package functionalTests.loggingRequests.deadlock;

import java.io.Serializable;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineThreadConfig;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import functionalTests.loggingRequests.LoggerDeadlockSolutionTest;


/**
 * Created by pkhvoros on 6/5/15.
 */
@DefineGroups({
        @Group(name = "first_run", selfCompatible = true)
})
@DefineThreadConfig(threadPoolSize=1, hardLimit=false)
public class FirstActiveObject implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
	
    @MemberOf("first_run")
    public String start(){
        try {
            SecondActiveObject secondActiveObject = PAActiveObject.newActive(
            		SecondActiveObject.class, null);
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
        return new StringWrapper(LoggerDeadlockSolutionTest.SEARCHED_STRING);
    }
    
}
