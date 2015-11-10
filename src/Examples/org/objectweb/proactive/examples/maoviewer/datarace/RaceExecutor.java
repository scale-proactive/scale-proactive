package org.objectweb.proactive.examples.maoviewer.datarace;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.examples.maoviewer.masterslave.SpeedGenerator;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


@DefineGroups({
        @Group(name = "start", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"start"})
        })
})
@DefineThreadConfig(threadPoolSize = 5, hardLimit = true)
public class RaceExecutor implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
    @MemberOf("start")
    public void start(DataHolder dataHolder, int value){
        int time = SpeedGenerator.generateRandomSpeed();
        try {
            Thread.sleep(time);
            ((DataHolder) dataHolder).write(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void someCall(){}
}
