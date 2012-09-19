package functionalTests.multiactivities.abourdin.test1;

import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
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


//@DefineGroups( {
//	@Group(name="parallel", selfCompatible=true),
//	@Group(name="runtime", selfCompatible=true),
//	@Group(name="mutex", selfCompatible=false)
//})
//@DefineRules({
//	@Compatible(value={"mutex", "parallel"})
//})
@DefineGroups( { @Group(name = "subscriptionsRead", selfCompatible = true),
        @Group(name = "subscriptionsWrite", selfCompatible = false),
        @Group(name = "listenersRead", selfCompatible = true),
        @Group(name = "listenersWrite", selfCompatible = false),
        @Group(name = "solutionsRead", selfCompatible = true),
        @Group(name = "solutionsWrite", selfCompatible = false),
        @Group(name = "eventIdsReceivedRead", selfCompatible = true),
        @Group(name = "eventIdsReceivedWrite", selfCompatible = false),
        @Group(name = "runtime", selfCompatible = true) })
@DefineRules( {
        @Compatible(value = { "runtime", "subscriptionsRead", "listenersRead", "solutionsRead",
                "eventIdsReceivedRead" }),
        @Compatible(value = { "runtime", "subscriptionsWrite", "listenersWrite", "solutionsWrite",
                "eventIdsReceivedWrite" }),
        @Compatible(value = { "subscriptionsRead", "listenersWrite", "solutionsWrite",
                "eventIdsReceivedWrite" }),
        @Compatible(value = { "subscriptionsWrite", "listenersRead", "solutionsWrite",
                "eventIdsReceivedWrite" }),
        @Compatible(value = { "subscriptionsWrite", "listenersWrite", "solutionsRead",
                "eventIdsReceivedWrite" }),
        @Compatible(value = { "subscriptionsWrite", "listenersWrite", "solutionsWrite",
                "eventIdsReceivedRead" }), })
public class MasterImpl implements RunActive, Runner, BindingController {
    public static String ITF_CLIENT = "i1";
    private Itf1 i1;
    private Boolean multiActive = true;

    public MasterImpl() {
        // for PA
    }

    //    @MemberOf("parallel")
    @MemberOf("runtime")
    public void run(List<String> arg) {
        //    	try {
        //			Thread.sleep(2000);
        //		} catch (InterruptedException e) {
        //			e.printStackTrace();
        //		}
        //		String str = "\n" + PAActiveObject.getBodyOnThis().getNodeURL() + " Master: " + this + "\n";
        //    	System.out.println(str + " call delegated to slave");
        //        i1.call(arg);
        run2(arg);
    }

    //  @MemberOf("parallel")
    @MemberOf("listenersWrite, subscriptionsWrite")
    public void run2(List<String> arg) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String str = "\n" + PAActiveObject.getBodyOnThis().getNodeURL() + " Master: " + this + "\n";
        System.out.println(str + " call delegated to slave");
        i1.call(arg);
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (ITF_CLIENT.equals(clientItfName)) {
            i1 = (Itf1) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { ITF_CLIENT };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (ITF_CLIENT.equals(clientItfName)) {
            return i1;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (ITF_CLIENT.equals(clientItfName)) {
            i1 = null;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
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