package functionalTests.multiactivities.abourdin.simpleMAComponent;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.core.component.body.ComponentRunActive;
import org.objectweb.proactive.multiactivity.MultiActiveService;


@DefineGroups( { @Group(name = "parallel", selfCompatible = true),
        @Group(name = "runtime", selfCompatible = true), @Group(name = "mutex", selfCompatible = false) })
@DefineRules( { @Compatible(value = { "mutex", "parallel" }) })
public class SlaveImpl implements ComponentRunActive, SlaveItf {

    public SlaveImpl() {
        //
    }

    @MemberOf("parallel")
    public void print(String arg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(arg + " " + System.currentTimeMillis() + "\n");
    }

    @Override
    public void runComponentActivity(Body body) {
        System.out.println("MAObject.runComponentActivity()");

        (new MultiActiveService(body)).multiActiveServing(3, true, true);
    }
}
