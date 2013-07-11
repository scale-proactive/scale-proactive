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
package org.objectweb.proactive.multiactivity.component;

import java.util.List;

import org.etsi.uri.gcm.api.control.PriorityController;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.body.ComponentBody;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.control.PAMembraneController;
import org.objectweb.proactive.core.component.identity.PAComponentImpl;
import org.objectweb.proactive.multiactivity.MultiActiveService;
import org.objectweb.proactive.multiactivity.component.policy.ComponentMembranePriorityServingPolicy;
import org.objectweb.proactive.multiactivity.component.policy.ComponentMembraneServingPolicy;
import org.objectweb.proactive.multiactivity.component.policy.ComponentPriorityServingPolicy;
import org.objectweb.proactive.multiactivity.component.policy.ComponentServingPolicy;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;
import org.objectweb.proactive.multiactivity.priority.PriorityConstraint;


/**
 * This class extends the {@link MultiActiveService} class for GCM components by
 * using a specific {@link ServingPolicy} according to the controllers of the
 * GCM components (ie. have a {@link PAMembraneController membrane controller}
 * and/or a {@link PriorityController priority controller} or neither of the
 * two). This extension allows to properly manage the life cycle of the GCM
 * components.
 * <p>
 * By default, all serving policy used with this {@link MultiActiveService} are
 * wrapped with a {@link ComponentServingPolicy} in order to handle
 * non-functional method calls properly. This behavior can be redefined by
 * overriding
 * {@link ComponentMultiActiveService#wrapServingPolicy(ServingPolicy)}.
 * 
 * @author The ProActive Team
 */
public class ComponentMultiActiveService extends MultiActiveService {

    /**
     * Creates a ComponentMultiActiveService.
     * 
     * @param body
     *            The body of the GCM component.
     */
    public ComponentMultiActiveService(Body body) throws IllegalArgumentException {
        super(body);

        if (!((ComponentBody) body).isComponent()) {
            throw new IllegalStateException(ComponentMultiActiveService.class.getName() +
                " can only be used with GCM components");
        }
    }

    protected ServingPolicy wrapServingPolicy(ServingPolicy delegate) {
        PAComponentImpl componentImpl = ((ComponentBody) body).getPAComponentImpl();
        PAGCMLifeCycleController lifeCycleController;
        PriorityController priorityController;
        PAMembraneController membraneController;

        try {
            lifeCycleController = Utils.getPAGCMLifeCycleController(componentImpl);
        } catch (NoSuchInterfaceException nsie) {
            throw new IllegalStateException("No life cycle controller interface, unable to"
                + "create a serving policy for this GCM component", nsie);
        }

        try {
            priorityController = GCM.getPriorityController(componentImpl);
            membraneController = Utils.getPAMembraneController(componentImpl);

            return new ComponentMembranePriorityServingPolicy(delegate, lifeCycleController,
                priorityController, membraneController);
        } catch (NoSuchInterfaceException nsie1) {
            try {
                priorityController = GCM.getPriorityController(componentImpl);

                return new ComponentPriorityServingPolicy(delegate, lifeCycleController, priorityController);
            } catch (NoSuchInterfaceException nsie2) {
                try {
                    membraneController = Utils.getPAMembraneController(componentImpl);

                    return new ComponentMembraneServingPolicy(delegate, lifeCycleController,
                        membraneController);
                } catch (NoSuchInterfaceException nsie3) {
                    return new ComponentServingPolicy(delegate, lifeCycleController);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServingPolicy createServingPolicy() {
        return this.wrapServingPolicy(super.createServingPolicy());
    }

    /**
     * The specified policy is automatically wrapped by a
     * {@link ComponentServingPolicy} to handle non-functional method calls
     * properly. This wrapping behavior could be redefined (and thus removed) by
     * overriding the method
     * {@link ComponentMultiActiveService#wrapServingPolicy(ServingPolicy)}.
     */
    @Override
    public void policyServing(ServingPolicy policy) {
        policy = this.wrapServingPolicy(policy);
        super.policyServing(policy);
    }

    /**
     * The specified policy is automatically wrapped by a
     * {@link ComponentServingPolicy} to handle non-functional method calls
     * properly. This wrapping behavior could be redefined (and thus removed) by
     * overriding the method
     * {@link ComponentMultiActiveService#wrapServingPolicy(ServingPolicy)}.
     */
    @Override
    public void policyServing(ServingPolicy policy, int maxActiveThreads) {
        policy = this.wrapServingPolicy(policy);
        super.policyServing(policy, maxActiveThreads);
    }

    /**
     * The specified policy is automatically wrapped by a
     * {@link ComponentServingPolicy} to handle non-functional method calls
     * properly. This wrapping behavior could be redefined (and thus removed) by
     * overriding the method
     * {@link ComponentMultiActiveService#wrapServingPolicy(ServingPolicy)}.
     */
    @Override
    public void policyServing(ServingPolicy policy, List<PriorityConstraint> priorityConstraints,
            int maxActiveThreads, boolean hardLimit, boolean hostReentrant) {
        policy = this.wrapServingPolicy(policy);
        super.policyServing(policy, priorityConstraints, maxActiveThreads, hardLimit, hostReentrant);
    }

}
