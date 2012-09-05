package functionalTests.multiactivities.abourdin.test2;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;

public class Main {
	public static void main(String[] args) throws Exception {

		// #### TEST MULTIACTIVE MASTER/SLAVE COMPONENTS ####
		Component boot = Utils.getBootstrapComponent();
		GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
		GenericFactory gf = GCM.getGenericFactory(boot);
		ComponentType tComposite = tf.createFcType(new InterfaceType[] { tf.createFcItfType("runner",
				Runner.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) });

		// Create the Master Component type
		ComponentType tMaster = tf.createFcType(new InterfaceType[] {
				tf.createFcItfType("runner", Runner.class.getName(), TypeFactory.SERVER,
						TypeFactory.MANDATORY, TypeFactory.SINGLE),
				tf.createFcItfType("i1", Itf1.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY,
						TypeFactory.SINGLE) });

		ComponentType tSlave = tf.createFcType(new InterfaceType[] { tf.createFcItfType("i1",
				Itf1.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) });

		Component slave = gf.newFcInstance(tSlave, new ControllerDescription("slave", Constants.PRIMITIVE),
				SlaveImpl.class.getName());

		// Create the Master Component
		Component master = gf.newFcInstance(tMaster,
				new ControllerDescription("master", Constants.PRIMITIVE), MasterImpl.class.getName());

		Component composite = gf.newFcInstance(tComposite, new ControllerDescription("composite",
				Constants.COMPOSITE), null);

		// Add slave and master components to the composite component
		ContentController cc = GCM.getContentController(composite);
		cc.addFcSubComponent(slave);
		cc.addFcSubComponent(master);

		// Do the bindings
		BindingController bcMaster = GCM.getBindingController(master);
		bcMaster.bindFc("i1", slave.getFcInterface("i1"));
		BindingController bcComposite = GCM.getBindingController(composite);
		bcComposite.bindFc("runner", master.getFcInterface("runner"));

		GCM.getGCMLifeCycleController(composite).startFc();

		Runner runner = (Runner) composite.getFcInterface("runner");

		System.out.println("#Start#");
		List<String> arg = new ArrayList<String>();
		arg.add("hello");
		arg.add("world");
		System.out.println("Call runner.run(arg) 5 times ...");
		runner.run(arg);
		runner.run(arg);
		runner.run(arg);
		runner.run(arg);
		runner.run(arg);
		System.out.println("runner.run(arg) called 5 times");

		// GCM.getGCMLifeCycleController(composite).stopFc();

		// System.exit(0);
	}
}
