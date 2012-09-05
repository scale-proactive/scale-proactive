package functionalTests.multiactivities.abourdin.inheritance;

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

public class MAObject extends SuperMAObject implements RunActive, Itf {
	
	public MAObject() {
		//
	}
	
	@MemberOf("parallel")
	public void print(String arg) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(arg + " @" + (System.currentTimeMillis() % 10000) + "ms");
	}
	
	@MemberOf("parallel")
	public void testItf(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("testItf @" + (System.currentTimeMillis() % 10000) + "ms");
	}

	@Override
	public void runActivity(Body body) {
		System.out.println("MAObject.runActivity()");
		
		(new MultiActiveService(body)).multiActiveServing(2, true, true);
		
	}
}
