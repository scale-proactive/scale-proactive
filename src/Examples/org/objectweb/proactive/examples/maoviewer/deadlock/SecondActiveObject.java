package org.objectweb.proactive.examples.maoviewer.deadlock;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


@DefineGroups({
        @Group(name = "second_run", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"second_run"})
        })
})
public class SecondActiveObject implements RunActive,Serializable { 

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
    @MemberOf("second_run")
    public String run(StubObject first){
        return ((FirstActiveObject) first).run(PAActiveObject.getStubOnThis());
    }
    public void someCall(){}
}
