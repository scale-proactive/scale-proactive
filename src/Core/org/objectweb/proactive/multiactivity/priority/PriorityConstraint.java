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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Defines the priority level to apply to the method call with the specified
 * method name and optionally, the given parameter types.
 * 
 * @author The ProActive Team
 */
public class PriorityConstraint implements Comparator<PriorityConstraint> {

    private final String methodName;

    private final List<Class<?>> parameterTypes;

    // priority level for methods without priority is 0
    private final int priorityLevel;

    public PriorityConstraint(int priorityLevel, String methodName) {
        this(priorityLevel, methodName, (List<Class<?>>) null);
    }

    public PriorityConstraint(int priorityLevel, String methodName,
            Class<?>... parameterTypes) {
        this(
                priorityLevel,
                methodName,
                (parameterTypes == null || parameterTypes.length == 0)
                        ? null
                        : Collections.unmodifiableList(Arrays.asList(parameterTypes)));
    }

    public PriorityConstraint(int priorityLevel, String methodName,
            List<Class<?>> parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes =
                (parameterTypes == null || parameterTypes.isEmpty())
                        ? null : parameterTypes;
        this.priorityLevel = priorityLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(PriorityConstraint rpc1, PriorityConstraint rpc2) {
        return rpc1.priorityLevel - rpc2.priorityLevel;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        PriorityConstraint other = (PriorityConstraint) obj;
        if (this.methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!this.methodName.equals(other.methodName)) {
            return false;
        }

        if (this.parameterTypes == null) {
            if (other.parameterTypes != null) {
                return false;
            }
        } else if (!this.parameterTypes.equals(other.parameterTypes)) {
            return false;
        }

        if (this.priorityLevel != other.priorityLevel) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.methodName == null)
                ? 0 : this.methodName.hashCode());
        result = prime * result + ((this.parameterTypes == null)
                ? 0 : this.parameterTypes.hashCode());
        result = prime * result + this.priorityLevel;
        return result;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<Class<?>> getParameterTypes() {
        return this.parameterTypes;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("priority=");
        buf.append(this.priorityLevel);
        buf.append(", method=");
        buf.append(this.methodName);
        buf.append('(');

        if (this.parameterTypes != null) {
            for (int i = 0; i < this.parameterTypes.size(); i++) {
                Class<?> clazz = this.parameterTypes.get(i);

                buf.append(clazz.getName());
                if (i < this.parameterTypes.size() - 1) {
                    buf.append(", ");
                }

            }
        }

        buf.append(')');

        return buf.toString();
    }

}
