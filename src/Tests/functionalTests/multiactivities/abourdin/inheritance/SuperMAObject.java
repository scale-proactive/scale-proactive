package functionalTests.multiactivities.abourdin.inheritance;

import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;


@DefineGroups( { @Group(name = "parallel", selfCompatible = true),
        @Group(name = "mutex", selfCompatible = false) })
public class SuperMAObject {

    public SuperMAObject() {
        //
    }

    @MemberOf("parallel")
    public void test() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("SMAO test @" + (System.currentTimeMillis() % 10000) + "ms");
    }

    @MemberOf("mutex")
    public void mutex() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("SMAO mutex @" + (System.currentTimeMillis() % 10000) + "ms");
    }

}
