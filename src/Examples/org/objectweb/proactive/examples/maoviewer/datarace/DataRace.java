package org.objectweb.proactive.examples.maoviewer.datarace;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


public class DataRace implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
	
    public int run(GCMApplication gcmApplication){
        GCMVirtualNode vnRaceExecutor = gcmApplication.getVirtualNode("RaceExecutor");
        GCMVirtualNode vnDataHolder = gcmApplication.getVirtualNode("DataHolder");
        try {
            DataHolder dataHolder = PAActiveObject.newActive(DataHolder.class, null, vnDataHolder.getANode());
            for (int i = 0; i < 5; i++){
                RaceExec raceExecutor = PAActiveObject.newActive(RaceExec.class, null, vnRaceExecutor.getANode());
                raceExecutor.start(dataHolder, i);
            }
            return dataHolder.read();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
