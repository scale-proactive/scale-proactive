package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.ReifiedObjectDecorator;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by pkhvoros on 3/27/15.
 */
public class RequestLoggerDecorator extends ReifiedObjectDecorator{
    private String folderPath;
    private String identifier;
//    private ReifiedObjectDecorator decoratedObject;
//    public RequestLoggerDecorator(ReifiedObjectDecorator decorator, Body body1) {
//        super(body1);
//        this.decoratedObject = decorator;
//    }


    public RequestLoggerDecorator(Body body, String folderPath) {
        super(body);
        this.folderPath = folderPath;
    }

    @Override
    public int onReceiveReply(Reply reply) {
        return 0;
    }

    @Override
    public int onReceiveRequest(Request request) {
        return 0;
    }

    @Override
    public int onDeliverReply(Reply reply) {
        return 0;
    }

    @Override
    public int onDeliverRequest(Request request) {
        if (identifier == null){
            identifier = generateIdentifier();
        }
        String log = "deliverrequest " + body.getID() + " " + Thread.currentThread().getId() + " " + request.getMethodName()+ " " + request.getSequenceNumber() + " " + System.currentTimeMillis() + " " + request.getSourceBodyID() + "\n";
        writeToFile(log);
        return 0;
    }

    @Override
    public int onSendReplyBefore(Reply reply) {
        return 0;
    }

    @Override
    public int onSendReplyAfter(Reply reply) {
        return 0;
    }

    @Override
    public int onSendRequestBefore(Request request) {
        if (identifier == null){
            identifier = generateIdentifier();
        }
        String log = "beforerequestsent " + body.getID() + " " + Thread.currentThread().getId() + " " + request.getMethodName()+ " " + request.getSequenceNumber() + " " + System.currentTimeMillis() + " " + request.getSourceBodyID() + "\n";
        writeToFile(log);
        return 0;
    }

    @Override
    public int onSendRequestAfter(Request request) throws RenegotiateSessionException, CommunicationForbiddenException {
        return 0;
    }

    @Override
    public int onServeRequestBefore(Request request) {
        return 0;
    }

    @Override
    public int onServeRequestAfter(Request request) {
        return 0;
    }

    private void writeToFile(String log){
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folderPath + identifier + ".txt", true)));
            out.print(log);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
    private String generateIdentifier(){
        return "Request_" + body.getID().toString();
    }
}
