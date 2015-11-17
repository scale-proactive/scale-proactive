package functionalTests.ft.multiactivity;

import java.net.URL;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.ft.AbstractFTTezt;
import functionalTests.ft.Agent;

public class MultiactiveAbstractFTTezt extends AbstractFTTezt {

	public MultiactiveAbstractFTTezt(URL gcma, int hostCapacity, int vmCapacity) {
		super(gcma, hostCapacity, vmCapacity);
	}

	@Override
	protected Agent createAgent(Node node) {
    	Agent returnAgent = null;
    	try {
    		returnAgent = PAActiveObject.newActive(MultiactiveAgent.class, new Object[0], node);
		} 
    	catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		}
    	catch (NodeException e) {
    		e.printStackTrace();
    	}
    	return returnAgent;
    }
}
