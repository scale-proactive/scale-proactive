/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.ft;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.process.JVMProcessImpl;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.GCMFunctionalTest;


/**
 * Common methods for FT non functional tests
 */
public class AbstractFTTezt extends GCMFunctionalTest {

	public static int AWAITED_RESULT = 1771014405;
	
    protected JVMProcessImpl server;
    

    public AbstractFTTezt(URL gcma, int hostCapacity, int vmCapacity) {
        super(gcma);
        super.setHostCapacity(hostCapacity);
        super.setVmCapacity(vmCapacity);
    }

    /**
     * Create a FT server on localhost:1100
     */
    protected void startFTServer(String protocol) throws IOException {
        this.server = new JVMProcessImpl(
            new org.objectweb.proactive.core.process.AbstractExternalProcess.StandardOutputMessageLogger());
        this.server.setClassname("org.objectweb.proactive.core.body.ft.servers.StartFTServer");
        this.server.setJvmOptions(super.getJvmParameters());
        this.server.setParameters(Arrays.asList("-proto", protocol));
        this.server.startProcess();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kill the FT Server
     */
    protected void stopFTServer() {
        this.server.stopProcess();
    }

    /**
     * Deploy two agents and start a dummy computation
     * @param gcmApplicationFile the deployment file  
     * @return the result of the computation
     */
    protected int deployAndStartAgents() throws ProActiveException {
        GCMVirtualNode vnode;

        super.startDeployment();
        vnode = super.gcmad.getVirtualNode("Workers");
        Node[] nodes = new Node[2];
        nodes[0] = vnode.getANode();
        nodes[1] = vnode.getANode();

        Agent a = this.createAgent(nodes[0]);
        Agent b = this.createAgent(nodes[1]);

        // not ft !
        Collector c = PAActiveObject.newActive(Collector.class, new Object[0]);

        a.initCounter(1);
        b.initCounter(1);
        a.setNeighbour(b);
        b.setNeighbour(a);
        a.setLauncher(c);

        c.go(a, 1000);

        //failure in 11 sec...
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            nodes[1].getProActiveRuntime().killRT(false);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        ReInt r = c.getResult();
        return r.getValue();
    }
    
    protected Agent createAgent(Node node) {
    	Agent returnAgent = null;
    	try {
    		returnAgent = PAActiveObject.newActive(Agent.class, new Object[0], node);
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
