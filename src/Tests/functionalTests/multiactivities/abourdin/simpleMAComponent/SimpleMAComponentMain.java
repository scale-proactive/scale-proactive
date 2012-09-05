package functionalTests.multiactivities.abourdin.simpleMAComponent;

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

public class SimpleMAComponentMain {

	public static void main(String[] args) throws Exception {
		Component boot = Utils.getBootstrapComponent();
		GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
		GenericFactory gf = GCM.getGenericFactory(boot);
		InterfaceType itf1Slave = tf.createFcItfType("i1", SlaveItf.class.getName(), TypeFactory.SERVER,
				TypeFactory.MANDATORY, TypeFactory.SINGLE);
		ComponentType tSlave = tf.createFcType(new InterfaceType[] { itf1Slave });
		Component slave = gf.newFcInstance(tSlave, new ControllerDescription("slave", Constants.PRIMITIVE),
				SlaveImpl.class.getName());

		GCM.getGCMLifeCycleController(slave).startFc();

		SlaveItf mao = (SlaveItf) slave.getFcInterface("i1");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");
		mao.print("test");

	}

}
