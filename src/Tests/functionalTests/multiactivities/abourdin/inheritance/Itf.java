package functionalTests.multiactivities.abourdin.inheritance;

import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;


@DefineGroups( { @Group(name = "pitf", selfCompatible = true) })
public interface Itf {

    public void testItf();

}
