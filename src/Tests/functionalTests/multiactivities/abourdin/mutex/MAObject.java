package functionalTests.multiactivities.abourdin.mutex;

import java.util.List;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.multiactivity.MultiActiveService;

@DefineGroups({
	@Group(name = "parallel", selfCompatible = true),
	@Group(name = "mutex", selfCompatible = false)
})
public class MAObject implements RunActive {
	
	public MAObject() {
		//
	}
	
	@MemberOf("parallel")
	public void print(String arg) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(arg + " @" + (System.currentTimeMillis() % 10000) + "ms");
	}
	
	@MemberOf("parallel")
	public void mutexCaller() {
		System.out.println("mutexCaller @" + (System.currentTimeMillis() % 10000) + "ms");
		for(int i=0; i<5; i++){
			MAObject.this.mutex();
		}
	}
	
	@MemberOf("mutex")
	public void mutex() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("mutex @" + (System.currentTimeMillis() % 10000) + "ms");
	}

	@Override
	public void runActivity(Body body) {
		System.out.println("MAObject.runActivity()");
		
		(new MultiActiveService(body)).multiActiveServing(2, true, true);
		
	}
}
