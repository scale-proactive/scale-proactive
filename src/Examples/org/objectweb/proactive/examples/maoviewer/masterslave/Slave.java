package org.objectweb.proactive.examples.maoviewer.masterslave;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.*;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@DefineGroups({
     @Group(name = "perform_computation", selfCompatible = true),
     @Group(name = "assign_work", selfCompatible = false),
     @Group(name = "help_slave", selfCompatible = false),
     @Group(name = "getters", selfCompatible = true)
})
@DefinePriorities({
        @PriorityHierarchy({
                @PrioritySet({"getters"}),
                @PrioritySet({"assign_work"}),
                @PrioritySet({"help_slave"}),
                @PrioritySet({"perform_computation"})
        })
})
@DefineRules({
        @Compatible({"perform_computation", "getters"}),
        @Compatible({"perform_computation", "assign_work"}),
        @Compatible({"assign_work", "getters"}),
        @Compatible({"help_slave", "getters"})
})
@DefineThreadConfig(threadPoolSize = 10, hardLimit = true)

//interface CompletionCallback{
//    public void completionFinished(Slave slave);
//}
public class Slave implements RunActive,Serializable{

	private static final long serialVersionUID = 1L;
	
	private int speed = SpeedGenerator.generateRandomSpeed();
    private List<Job> jobs = new ArrayList<>();

    @Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }

    @MemberOf("perform_computation")
    public BooleanWrapper compute(IntWrapper jobIndex){
        Job job = jobs.get(jobIndex.getIntValue());
        int currentWork = job.getAmountOfWork();
        job.setHasStarted(true);
        try {
            while(currentWork > 0) {
                currentWork = currentWork - speed > 0 ? currentWork - speed : 0;
                job.setAmountOfWork(currentWork);
                Thread.sleep(10);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new BooleanWrapper(true);
    }

    @MemberOf("assign_work")
    public void addJobs(List<Job> jobs){
        this.jobs = jobs;
    }
    @MemberOf("assign_work")
    public void addJob(Job job){
        this.jobs.add(job);
    }
    @MemberOf("assign_work")
    public Job assignDelegatedWork(){
        for (Job job: jobs){
            if (!job.hasStarted()) {
                jobs.remove(job);
                return job;
            }
        }
        return null;
    }
    @MemberOf("help_slave")
    public BooleanWrapper helpAnotherSlave(Slave slave){
        Job job = slave.assignDelegatedWork();
        if (job == null)
            return new BooleanWrapper(true);
        jobs.add(job);
        return compute(new IntWrapper(jobs.indexOf(job)));
    }
    @MemberOf("getters")
    public BooleanWrapper needsHelp(){
        for (Job job: jobs){
            if (!job.hasStarted()){
                return new BooleanWrapper(true);
            }
        }
        return new BooleanWrapper(false);
    }
    @MemberOf("getters")
    public List<IntWrapper> amountOfWorkLeft(){
        List<IntWrapper> jobsLeft = new ArrayList<>();
        for (Job job:jobs)
            jobsLeft.add(new IntWrapper(job.getAmountOfWork()));
        return jobsLeft;
    }

}
