package functionalTests.multiactivities.abourdin.simpleMAObject;

import java.util.UUID;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.multiactivity.MultiActiveService;


@DefineGroups( { @Group(name = "parallel", selfCompatible = true),
        @Group(name = "runtime", selfCompatible = true), @Group(name = "mutex", selfCompatible = false) })
@DefineRules( { @Compatible(value = { "mutex", "parallel" }) })
public class MAObject implements RunActive {

    public MAObject() {
        //
    }

    @Override
    public void runActivity(Body body) {
        System.out.println("MAObject.runActivity()");

        (new MultiActiveService(body)).multiActiveServing(3, true, true);
        //		(new MultiActiveService(body)).fifoServing();

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
    public Result getResult() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MAObject.getResult()");
        return new Result(UUID.randomUUID().toString() + " @" + (System.currentTimeMillis() % 10000) + "ms");
    }

    @MemberOf("parallel")
    public String toString() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "toString @" + (System.currentTimeMillis() % 10000) + "ms";
    }
}
