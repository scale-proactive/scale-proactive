package org.objectweb.proactive.examples.maoviewer.deadlock;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;

@DefineGroups({
	@Group(name="starter", selfCompatible=true),
	@Group(name="runner", selfCompatible=true)
})
@DefineRules({
	@Compatible({"starter", "runner"})
})
public class AO1 implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
	
	@MemberOf("starter")
    public void start(GCMApplication gcmApplication){
        GCMVirtualNode vn = gcmApplication.getVirtualNode("SecondActiveObject");
        try {
            AO2 secondActiveObject = PAActiveObject.newActive(AO2.class, null, vn.getANode());
            System.out.println(secondActiveObject.run(PAActiveObject.getStubOnThis()));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    
	@MemberOf("runner")
    public String run(StubObject second){
        System.out.println("run first object");
        return "needed result";
    }
}
