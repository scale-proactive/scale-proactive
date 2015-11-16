package org.objectweb.proactive.examples.maoviewer.threadlimit;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


@DefineGroups({
        @Group(name = "run", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"run"})
        })
})
@DefineThreadConfig(threadPoolSize = 10, hardLimit = false)
public class LimitExample implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
    @MemberOf("run")
    public int run(int n){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  n != 0 ? ((LimitExample)PAActiveObject.getStubOnThis()).run(n - 1) : 0;
    }
}