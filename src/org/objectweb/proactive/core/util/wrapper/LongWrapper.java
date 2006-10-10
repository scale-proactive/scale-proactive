/* 
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, 
 *            Concurrent computing with Security and Mobility
 * 
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *  
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s): 
 * 
 * ################################################################
 */ 
package org.objectweb.proactive.core.util.wrapper;

import java.io.Serializable;


/**
 * <p>An reifiable object for wrapping the primitive Java type <code>long</code>.</p>
 * <p>Use this class as result for ProActive asynchronous method calls.</p>
 *
 * @author Alexandre di Costanzo
 *
 * Created on Jul 28, 2005
 */
public class LongWrapper implements Serializable {

    /**
     * The primitive value.
     */
    private long value;

    /**
     * The no arguments constructor for ProActive.
     */
    public LongWrapper() {
        // nothing to do
    }

    /**
     * Construct an reifiable object for a <code>long</code>.
     * @param value the primitive <code>long</code> value.
     */
    public LongWrapper(long value) {
        this.value = value;
    }

    /**
     * Return the value of the <code>long</code>.
     * @return the primitive value.
     */
    public long longValue() {
        return this.value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.value + "";
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof LongWrapper) {
            return ((LongWrapper) arg0).longValue() == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new Long(this.value).hashCode();
    }

}
