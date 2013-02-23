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
package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;


/**
 * Maintain {@link PriorityConstraint}s.
 * 
 * @author The ProActive Team
 */
public class PriorityConstraints {

    private final Map<String, List<PriorityConstraint>> priorityConstraints;

    private final TreeMap<Integer, PriorityGroup> priorityGroups;

    public PriorityConstraints(PriorityConstraint... priorityConstraints) {
        this(Arrays.asList(priorityConstraints));
    }

    public PriorityConstraints(List<PriorityConstraint> priorityConstraints) {
        this.priorityConstraints = new HashMap<String, List<PriorityConstraint>>();

        this.priorityGroups = new TreeMap<Integer, PriorityGroup>();
        // priority group for methods without priority
        this.priorityGroups.put(0, new PriorityGroup(0));

        for (PriorityConstraint constraint : priorityConstraints) {
            List<PriorityConstraint> existingEntries = this.priorityConstraints.get(constraint
                    .getMethodName());

            if (existingEntries == null) {
                existingEntries = new ArrayList<PriorityConstraint>(1);
                existingEntries.add(constraint);
                this.priorityConstraints.put(constraint.getMethodName(), existingEntries);
            } else {
                existingEntries.add(constraint);
            }

            if (!this.priorityGroups.containsKey(constraint.getPriorityLevel())) {
                this.priorityGroups.put(constraint.getPriorityLevel(), new PriorityGroup(constraint
                        .getPriorityLevel()));
            }
        }
    }

    public void addToDefaultPriorityGroup(RunnableRequest request) {
        this.priorityGroups.get(0).add(request);
    }

    public void addToPriorityGroup(int groupLevel, RunnableRequest request) {
        this.priorityGroups.get(groupLevel).add(request);
    }

    public void clearPriorityGroups() {
        for (PriorityGroup priorityGroup : this.priorityGroups.values()) {
            priorityGroup.clear();
        }
    }

    public List<PriorityConstraint> getConstraints(String methodCallName) {
        return this.priorityConstraints.get(methodCallName);
    }

    public TreeMap<Integer, PriorityGroup> getPriorityGroups() {
        return this.priorityGroups;
    }

    public static boolean satisfies(Request request, PriorityConstraint priorityConstraint) {
        boolean sameNames = request.getMethodCall().getName().equals(priorityConstraint.getMethodName());
        boolean sameParameters = true;

        if (priorityConstraint.getParameterTypes() != null) {
            for (int i = 0; i < priorityConstraint.getParameterTypes().size(); i++) {
                Class<?> parameterClazz = priorityConstraint.getParameterTypes().get(i);

                if (i >= request.getMethodCall().getNumberOfParameter()) {
                    sameParameters = false;
                    break;
                } else {
                    sameParameters &= request.getMethodCall().getParameter(i).getClass().equals(
                            parameterClazz);
                }
            }
        }

        return sameNames && sameParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("PriorityConstraints:\n");

        for (List<PriorityConstraint> priorityConstraints : this.priorityConstraints.values()) {
            for (PriorityConstraint priorityConstraint : priorityConstraints) {
                buf.append("  ");
                buf.append(priorityConstraint.toString());
                buf.append("\n");
            }
        }

        return buf.toString();
    }

}
