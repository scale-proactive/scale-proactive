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
package functionalTests.multiactivities.component.activity;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.FactoryFactory;

import functionalTests.ComponentTest;


/**
 * Test activity for multi-active GCM components.
 * 
 * @author The ProActive Team
 */
public class TestActivity extends ComponentTest {
    private Factory factory;

    private Component myComponent;

    private FService fService;

    @Before
    public void setUp() throws Exception {
        this.factory = FactoryFactory.getFactory();
    }

    @Test
    public void testActivity() throws Exception {
        this.myComponent = (Component) this.factory.newComponent(
                "functionalTests.multiactivities.component.activity.MyComponent",
                new HashMap<String, Object>());
        this.fService = (FService) myComponent.getFcInterface("f-service");

        this.launchCallerAndWait(false);

        GCM.getGCMLifeCycleController(myComponent).startFc();

        this.launchCallerAndWait(true);

        GCM.getGCMLifeCycleController(myComponent).stopFc();

        this.launchCallerAndWait(false);

        Utils.getPAGCMLifeCycleController(myComponent).terminateGCMComponent(true);
    }

    @Test
    public void testActivityWithoutPriorityController() throws Exception {
        this.myComponent = (Component) this.factory.newComponent(
                "functionalTests.multiactivities.component.activity.MyComponentWithoutPriorityController",
                new HashMap<String, Object>());
        this.fService = (FService) myComponent.getFcInterface("f-service");

        this.launchCallerAndWait(false);

        GCM.getGCMLifeCycleController(myComponent).startFc();

        this.launchCallerAndWait(true);

        GCM.getGCMLifeCycleController(myComponent).stopFc();

        this.launchCallerAndWait(false);

        Utils.getPAGCMLifeCycleController(myComponent).terminateGCMComponent(true);
    }

    @Test
    public void testMembraneActivity() throws Exception {
        this.myComponent = (Component) this.factory.newComponent(
                "functionalTests.multiactivities.component.activity.MyComponentWithMembrane",
                new HashMap<String, Object>());
        this.fService = (FService) myComponent.getFcInterface("f-service");

        Utils.getPAMembraneController(myComponent).stopMembrane();

        this.launchCallerAndWait(false);

        Utils.getPAMembraneController(myComponent).startMembrane();

        this.launchCallerAndWait(false);

        GCM.getGCMLifeCycleController(myComponent).startFc();

        this.launchCallerAndWait(true);

        GCM.getGCMLifeCycleController(myComponent).stopFc();

        this.launchCallerAndWait(false);

        Utils.getPAMembraneController(myComponent).stopMembrane();

        this.launchCallerAndWait(false);

        // Need to start the membrane to call the terminate method
        Utils.getPAMembraneController(myComponent).startMembrane();

        Utils.getPAGCMLifeCycleController(myComponent).terminateGCMComponent(true);
    }

    @Test
    public void testMembraneActivityWithoutPriorityController() throws Exception {
        this.myComponent = (Component) this.factory
                .newComponent(
                        "functionalTests.multiactivities.component.activity.MyComponentWithMembraneWithoutPriorityController",
                        new HashMap<String, Object>());
        this.fService = (FService) myComponent.getFcInterface("f-service");

        Utils.getPAMembraneController(myComponent).stopMembrane();

        this.launchCallerAndWait(false);

        Utils.getPAMembraneController(myComponent).startMembrane();

        this.launchCallerAndWait(false);

        GCM.getGCMLifeCycleController(myComponent).startFc();

        this.launchCallerAndWait(true);

        GCM.getGCMLifeCycleController(myComponent).stopFc();

        this.launchCallerAndWait(false);

        Utils.getPAMembraneController(myComponent).stopMembrane();

        this.launchCallerAndWait(false);

        // Need to start the membrane to call the terminate method
        Utils.getPAMembraneController(myComponent).startMembrane();

        Utils.getPAGCMLifeCycleController(myComponent).terminateGCMComponent(true);
    }

    private void launchCallerAndWait(boolean shouldSucceed) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new Caller(this.fService));
        boolean succeed = false;

        try {
            succeed = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // Ignore it
        }

        executor.shutdown();

        Assert.assertTrue(succeed == shouldSucceed);
    }

    private class Caller implements Callable<Boolean> {
        private FService fService;

        public Caller(FService fservice) {
            this.fService = fservice;
        }

        @Override
        public Boolean call() {
            return this.fService.ping();
        }
    }
}
