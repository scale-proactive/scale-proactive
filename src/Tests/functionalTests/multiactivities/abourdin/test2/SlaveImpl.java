package functionalTests.multiactivities.abourdin.test2;

import java.util.List;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.multiactivity.MultiActiveService;

@DefineGroups({
	@Group(name="parallel", selfCompatible=true),
	@Group(name="runtime", selfCompatible=true),
	@Group(name="mutex", selfCompatible=false)
})
@DefineRules({
	@Compatible(value={"mutex", "parallel"})
})

public class SlaveImpl implements Itf1, RunActive {
	private Integer value;
	private Boolean multiActive = true;
	
	public SlaveImpl(){
		// for PA
	}
	
	@MemberOf("parallel")
	public void call(List<String> arg) {
		String str = "\n" + PAActiveObject.getBodyOnThis().getNodeURL() + " Slave: " + this + "\n";
        str += "arg: ";
        for (int i = 0; i < arg.size(); i++) {
            str += arg.get(i);
            if (i < arg.size() - 1) {
                str += " - ";
            }
        }
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(str);
	}
	
	@MemberOf("parallel")
	public void printValue() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String str = "\n" + PAActiveObject.getBodyOnThis().getNodeURL() + " Slave: " + this + "\n";
		System.out.println(str + " value: " + value);
	}
	
	@MemberOf("parallel")
	public void addValue(Integer v) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		value += v;
		String str = "\n" + PAActiveObject.getBodyOnThis().getNodeURL() + " Slave: " + this + "\n";
		System.out.println(str + " increased " + v);
		printValue();
	}
	
	@Override
	public void runActivity(Body body) {
    	if (this.multiActive) {
    		(new MultiActiveService(body)).multiActiveServing(3, true, true);
    	} else {
    		(new Service(body)).fifoServing();
    	}
	}
}
