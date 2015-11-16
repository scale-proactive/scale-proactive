package org.objectweb.proactive.examples.maoviewer.masterslave;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DefineGroups({
        @Group(name = "assign_help", selfCompatible = false),
        @Group(name = "start_action", selfCompatible = false),
        @Group(name = "collect_statistics", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"collect_statistics"}),
                @PrioritySet({"assign_help"}),
                @PrioritySet({"start_action"})
        })
})
@DefineRules({
        @Compatible({"assign_help", "collect_statistics"})
})
@DefineThreadConfig(threadPoolSize = 1, hardLimit = true)


public class Master implements RunActive,Serializable{

	private static final long serialVersionUID = 1L;
	
	private final int numberOfSlaves = 10;
    private final int numberOfJobsForEachSlave = 1000;
    private List<Slave> slaves = new ArrayList<Slave>();
    List<List<BooleanWrapper>> futures;

    @MemberOf("start_action")
    public void prepareAction(GCMApplication gcmApplication) {
        //for each slave and for each job we have a future variable
        try {
//            System.out.println("gcm app =" + gcmApplication.getVirtualNode("Slave") + " " + gcmApplication.getVirtualNode("Slave").getANode());
            for (int i = 0; i < numberOfSlaves; i++) {
                GCMVirtualNode vn = gcmApplication.getVirtualNode("Slave");
                Slave slave = PAActiveObject.newActive(Slave.class, null, vn.getANode());
                slaves.add(slave);
                List<Job> jobs = createJobs();
                slave.addJobs(jobs);
            }
        } catch (ActiveObjectCreationException | NodeException e) {
            e.printStackTrace();
        }

//        for (Slave slave:slaves)
//            PAActiveObject.terminateActiveObject(slave, false);
    }
    private List<Job> createJobs(){
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < numberOfJobsForEachSlave; i++){
            jobs.add(new Job(1000));
        }
        return jobs;
    }
    private List<List<BooleanWrapper>> makeComputations(){
        List<List<BooleanWrapper>> futures = new ArrayList<>();
        for(int i = 0; i < slaves.size(); i++){
            List<BooleanWrapper> jobResults = new ArrayList<>();
            for (int j = 0; j < numberOfJobsForEachSlave; j++) {
                jobResults.add(slaves.get(i).compute(new IntWrapper(j)));
            }
            futures.add(jobResults);
        }
        return futures;
    }
    @MemberOf("assign_help")
    public void startAction(){
        futures = makeComputations();
        boolean isAnyoneNeedHelp = true;
        Slave freeSlave = null;
        Slave slaveWhichNeedsHelp = null;
        while (isAnyoneNeedHelp){
            try {
                isAnyoneNeedHelp = false;
                for (Slave slave : slaves) {
                    if (slave.needsHelp().getBooleanValue()) {
                        slaveWhichNeedsHelp = slave;
                        isAnyoneNeedHelp = true;
                    }
                    else{
                        freeSlave = slave;
                    }
                }
                if (freeSlave != null && slaveWhichNeedsHelp != null && freeSlave != slaveWhichNeedsHelp){
                    freeSlave.helpAnotherSlave(slaveWhichNeedsHelp);
                    System.out.print("\nSlave" + slaves.indexOf(freeSlave) + " helps slave" + slaves.indexOf(slaveWhichNeedsHelp));
                }
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

//    Returns true if there are still some work to do. I.e. if someone still working
    @MemberOf("collect_statistics")
    private boolean printAmountOfWorkLeft(){
        System.out.println("Amount of work left");
        boolean isStillWorking = false;
        for (int slaveIndex = 0; slaveIndex < slaves.size(); slaveIndex++){
            List<IntWrapper> worksLeft = slaves.get(slaveIndex).amountOfWorkLeft();
            System.out.print("work left for slave[" + slaveIndex + "]= ");
            for (IntWrapper workLeft:worksLeft){
                System.out.print(workLeft.getIntValue() + " ");
                if (workLeft.getIntValue() > 0)
                    isStillWorking = true;
            }
            System.out.println();

        }
        System.out.print("\n\n");
        return isStillWorking;
    }
    @MemberOf("collect_statistics")
    public void doSomething(){
        System.out.println("SMTH");
    }
    @MemberOf("collect_statistics")
    public void collectStatistics(){
        for(int i = 0; i < 5; i++){
            try {
                Thread.sleep(300);
                ((Master)PAActiveObject.getStubOnThis()).doSomething();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
}
