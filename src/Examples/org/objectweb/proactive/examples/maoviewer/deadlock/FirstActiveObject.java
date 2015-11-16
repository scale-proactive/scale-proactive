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
        @Group(name = "first_run", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"first_run"})
        })
})

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
    public void start(GCMApplication gcmApplication){
        GCMVirtualNode vn = gcmApplication.getVirtualNode("SecondActiveObject");
//        System.out.println("run start" + vn);
        try {
            SecondActiveObject secondActiveObject = PAActiveObject.newActive(SecondActiveObject.class, null, vn.getANode());
            System.out.println(secondActiveObject.run(PAActiveObject.getStubOnThis()));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    @MemberOf("first_run")
    public String run(StubObject second){
        System.out.println("run first object");
        return "needed result";
    }
}
