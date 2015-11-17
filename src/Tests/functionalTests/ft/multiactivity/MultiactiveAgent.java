package functionalTests.ft.multiactivity;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import functionalTests.ft.Agent;

public class MultiactiveAgent extends Agent implements RunActive {

	private static final long serialVersionUID = 1L;

	@Override
	public void runActivity(Body body) {
		MultiActiveService service = new MultiActiveService(body);
		while (body.isActive()) {
			service.multiActiveServing();
		}
	}

}
