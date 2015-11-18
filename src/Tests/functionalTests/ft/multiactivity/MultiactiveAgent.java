package functionalTests.ft.multiactivity;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import functionalTests.ft.Agent;
import functionalTests.ft.ReInt;

/*@DefineGroups({
		@Group(name="counting", selfCompatible=true)})*/
public class MultiactiveAgent extends Agent implements RunActive {

	private static final long serialVersionUID = 1L;

	@Override
	public void runActivity(Body body) {
		MultiActiveService service = new MultiActiveService(body);
		while (body.isActive()) {
			service.multiActiveServing();
		}
	}
	
	@Override
	//@MemberOf("counting")
	public ReInt doStuff(ReInt param) {
        return super.doStuff(param);
    }

}
