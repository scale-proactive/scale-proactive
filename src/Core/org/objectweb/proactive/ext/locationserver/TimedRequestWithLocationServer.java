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
package org.objectweb.proactive.ext.locationserver;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.StubObject;


public class TimedRequestWithLocationServer extends RequestImpl implements java.io.Serializable {
    private static final int MAX_TRIES = 30;

    /**
     * the number of time we try before reporting a failure
     */

    // private long startTimeServer;
    //private long startTimeAgent;
    protected long startTime;
    private int tries;
    private transient LocationServer server;

    public TimedRequestWithLocationServer(MethodCall methodCall, UniversalBody sender, boolean isOneWay,
            long nextSequenceID, LocationServer server) {
        super(methodCall, sender, isOneWay, nextSequenceID);
        this.server = server;
    }

    @Override
    public Reply serve(Body targetBody) {
        Reply r = super.serve(targetBody);
        return r;
    }

    @Override
    protected int sendRequest(UniversalBody destinationBody) throws java.io.IOException {
        System.out.println("TimedRequestWithLocationServer: sending to remote " + methodName);
        int ftres = FTManager.NON_FT;
        try {
            startTime = System.currentTimeMillis();
            ftres = destinationBody.receiveRequest(this);

            long endTime = System.currentTimeMillis();
            System.out.println("TimedRequestWithLocationServer:  .............. 1/gamma = " +
                (endTime - startTime) + " for method " + methodName);
            System.out.println("TimedRequestWithLocationServer:  .............. done  = " +
                (endTime - startTime) + " for method " + methodName);
        } catch (Exception e) {
            // endTime = System.currentTimeMillis();
            //There can only be a problem when trying to contact the Agent
            System.out.println("TimedRequestWithLocationServer:  .............. FAILED = " +
                (System.currentTimeMillis() - startTime) + " for method " + methodName);
            //e.printStackTrace();
            System.out.println(">>>>>>>>>>>> Exception " + e);
            this.backupSolution(destinationBody);
        }
        return ftres;
    }

    /**
     * Implements the backup solution
     */
    protected void backupSolution(UniversalBody destinationBody) throws java.io.IOException {
        //   long startTimeGamma1=0;
        //   long endTimeGamma1=0;
        boolean ok = false;
        tries = 0;

        //   System.out.println("TimedRequestWithLocationServer: backupSolution() contacting server  at time " + System.currentTimeMillis());
        //get the new location from the server
        UniqueID bodyID = destinationBody.getID();
        while (!ok && (tries < MAX_TRIES)) {
            UniversalBody remoteBody = null;
            System.out.println(" ==== Query server ==== time " + System.currentTimeMillis());

            UniversalBody mobile = queryServer(bodyID);
            System.out.println("=========================== time " + System.currentTimeMillis());
            //we want to bypass the stub/proxy
            remoteBody = (UniversalBody) ((FutureProxy) ((StubObject) mobile).getProxy()).getResult();

            long startTimeGamma = System.currentTimeMillis();
            try {
                remoteBody.receiveRequest(this);

                long endTime = System.currentTimeMillis();
                System.out.println("TimedRequestWithLocationServer:  .............. 1/gamma = " +
                    (endTime - startTimeGamma) + " for method " + methodName);
                System.out.println("TimedRequestWithLocationServer:  .............. done = " +
                    (endTime - startTime) + " for method " + methodName);
                //everything went fine, we have to update the current location of the object
                //so that next requests don't go through the server
                if (sender != null) {
                    sender.updateLocation(bodyID, remoteBody);
                } else {
                    LocalBodyStore.getInstance().getLocalBody(getSourceBodyID()).updateLocation(bodyID,
                            remoteBody);
                }
                ok = true;
            } catch (Exception e) {
                System.out.println("TimedRequestWithLocationServer:  .............. FAILED = " +
                    (System.currentTimeMillis() - startTimeGamma) + " for method " + methodName);
                tries++;
            }
        }
    }

    protected UniversalBody queryServer(UniqueID bodyID) {
        long startTimeBackupSolution = System.currentTimeMillis();
        if (server == null) {
            server = LocationServerFactory.getLocationServer();
        }

        UniversalBody mobile = server.searchObject(bodyID);
        long endTimeBackupSolution = System.currentTimeMillis();
        System.out
                .println("TimedRequestWithLocationServer: backupSolution() server has sent an answer after " +
                    (endTimeBackupSolution - startTimeBackupSolution));
        PAFuture.waitFor(mobile);
        return mobile;
    }
}
