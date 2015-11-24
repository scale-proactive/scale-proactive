package org.objectweb.proactive.examples.maoviewer;

import java.net.URL;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.maoviewer.datarace.DataRace;
import org.objectweb.proactive.examples.maoviewer.deadlock.ActiveObject1;
import org.objectweb.proactive.examples.maoviewer.masterslave.Master;
import org.objectweb.proactive.examples.maoviewer.threadlimit.LimitExample;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class MAOViewerExamples {
	
    private static GCMApplication gcmApplication = null;
    
    public static void main(String[] args) {
    	//runMasterSlaveExample();
    	//runLimitExample();
    	//runDeadlockExample();
        runConcurrentReadWriteExample();
	}
    
	private static void runLimitExample(){
    	URL descUrl = MAOViewerExamples.class.getResource("GCMALimitExample.xml");
        initTechnicalService(descUrl);
        limitExample();
    }
    
	private static void runMasterSlaveExample(){
    	URL descUrl = MAOViewerExamples.class.getResource("GCMASlave.xml");
        initTechnicalService(descUrl);
        masterSlaveExample();
    }
    
    private static void runDeadlockExample(){
    	URL descUrl = MAOViewerExamples.class.getResource("GCMADeadlock.xml");
        initTechnicalService(descUrl);
        deadlockExample();
    }
    
	private static void runConcurrentReadWriteExample(){
    	URL descUrl = MAOViewerExamples.class.getResource("GCMAConcurrency.xml");
        initTechnicalService(descUrl);
        concurrencyExample();
    }
    
    private static void initTechnicalService(URL resourceURL){
        try {
            gcmApplication = PAGCMDeployment.loadApplicationDescriptor(
                    resourceURL);
        }
        catch (ProActiveException e1) {
            e1.printStackTrace();
        }
        gcmApplication.startDeployment();
        gcmApplication.waitReady();
    }
    
    private static void masterSlaveExample(){
        Master master = null;
        try {
            GCMVirtualNode vn = gcmApplication.getVirtualNode("Master");
            master = PAActiveObject.newActive(Master.class, null, vn.getANode());
            master.prepareAction(gcmApplication);
            master.startAction();
            master.collectStatistics();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    
    private static void deadlockExample(){
        ActiveObject1 firstExample;
        try {
            GCMVirtualNode vn = gcmApplication.getVirtualNode("FirstActiveObject");
            firstExample = PAActiveObject.newActive(ActiveObject1.class, null, vn.getANode());
            firstExample.start(gcmApplication);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    
    private static void limitExample(){
        LimitExample firstExample;
        try {
            GCMVirtualNode vn = gcmApplication.getVirtualNode("LimitExample");
            firstExample = PAActiveObject.newActive(LimitExample.class, null, vn.getANode());
            firstExample.run(20);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    
    private static void concurrencyExample(){
        DataRace firstExample;
        try {
            GCMVirtualNode vn = gcmApplication.getVirtualNode("MainDataRace");
            firstExample = PAActiveObject.newActive(DataRace.class, null, vn.getANode());
            System.out.println("here = " +firstExample.run(gcmApplication));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
    
}