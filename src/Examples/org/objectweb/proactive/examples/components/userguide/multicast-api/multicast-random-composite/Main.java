/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.control.PAContentController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author The ProActive Team
 */

/**
 * GCM example with "RANDOM" distribution of list parameters.
 * Distributes each element of the list parameter in a random manner to the connected slaves.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory gcmTypeFactory = GCM.getGCMTypeFactory(boot);
        GenericFactory genericFactory = GCM.getGenericFactory(boot);
        final String MULTICAST_INTERFACE_NAME = "multicast_interface";
        //define component types
        ComponentType componentTypeComposit = gcmTypeFactory.createFcType(new InterfaceType[]
                                                {
                                                        gcmTypeFactory.createFcItfType
                                                                ("runner",
                                                                    Runner.class.getName(),
                                                                    TypeFactory.SERVER,
                                                                    TypeFactory.MANDATORY,
                                                                    TypeFactory.SINGLE
                                                                )
                                                });

        ComponentType componentTypeMaster = gcmTypeFactory.createFcType(new InterfaceType[]
                {
                        gcmTypeFactory.createFcItfType
                                ("runner",
                                    Runner.class.getName(),
                                    TypeFactory.SERVER,
                                    TypeFactory.MANDATORY,
                                    TypeFactory.SINGLE
                                ),
                        gcmTypeFactory.createGCMItfType
                                (
                                    MULTICAST_INTERFACE_NAME,
                                    MasterMulticastItf.class.getName(),
                                    TypeFactory.CLIENT,
                                    TypeFactory.MANDATORY,
                                    GCMTypeFactory.MULTICAST_CARDINALITY
                                )
                });

        // you can define one component type and use it in all slaves bindings with different names. For the sake of clarity we will make 3 interface type for the 3 slaves
        ComponentType componentTypeSlave1 = gcmTypeFactory.createFcType(new InterfaceType[]{gcmTypeFactory.createFcItfType("i1", Itf1.class
                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE)});

        ComponentType ComponentTypeSlave2 = gcmTypeFactory.createFcType(new InterfaceType[]{gcmTypeFactory.createFcItfType("i2", Itf1.class
                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE)});

        ComponentType ComponentTypeSlave3 = gcmTypeFactory.createFcType(new InterfaceType[]{gcmTypeFactory.createFcItfType("i3", Itf1.class
                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE)});

        Component composite = genericFactory.newFcInstance(componentTypeComposit, new ControllerDescription("composite",
                Constants.COMPOSITE), null);

        Component master = genericFactory.newFcInstance(componentTypeMaster,
                new ControllerDescription("master", Constants.PRIMITIVE),
                MasterImpl.class.getName()
        );

        //ContentDescription is used to initialize the "name" member variable in slave. This is how you initialize the member variables in slave if you want.
        Component slave1 = genericFactory.newFcInstance(componentTypeSlave1, new ControllerDescription("slave1", Constants.PRIMITIVE),
                new ContentDescription(SlaveImpl.class.getName(), new Object[]{"Slave-1"}));
        Component slave2 = genericFactory.newFcInstance(ComponentTypeSlave2, new ControllerDescription("slave2", Constants.PRIMITIVE),
                new ContentDescription(SlaveImpl.class.getName(), new String[]{"Slave-2"}));
        Component slave3 = genericFactory.newFcInstance(ComponentTypeSlave3, new ControllerDescription("slave3", Constants.PRIMITIVE),
                new ContentDescription(SlaveImpl.class.getName(), new String[]{"Slave-3"}));

        /**
         * To add components to the Composite, we need to get the reference of ContentController object of "composite" component.
         */
        List<Component> componentList = new ArrayList<>();
        componentList.add(slave1);
        componentList.add(slave2);
        componentList.add(slave3);
        componentList.add(master);
        ((PAContentController) GCM.getContentController(composite)).addFcSubComponent(componentList);

        //bindings master ---> 3 slaves
        GCM.getBindingController(master).bindFc(MULTICAST_INTERFACE_NAME, slave1.getFcInterface("i1"));
        GCM.getBindingController(master).bindFc(MULTICAST_INTERFACE_NAME, slave2.getFcInterface("i2"));
        GCM.getBindingController(master).bindFc(MULTICAST_INTERFACE_NAME, slave3.getFcInterface("i3"));
        //bindings composite ---> master
        GCM.getBindingController(composite).bindFc("runner", master.getFcInterface("runner"));
        //start the components via composite
        GCM.getGCMLifeCycleController(composite).startFc();

        Runner runner = (Runner) composite.getFcInterface("runner");
        List<String> arg = new ArrayList<String>();
        arg.add("GCM IS FUN");
        arg.add("And it is cool");
        arg.add("When you run examples");
        arg.add("And play with it");
        runner.run(arg);

        Thread.sleep(4000); //this line is to avoid race condition when slaves are killed by main thread before they can perform their assigned tasks. Of course there can be better solution to fix this.
        GCM.getGCMLifeCycleController(composite).stopFc();
        System.exit(0);
    }
}
