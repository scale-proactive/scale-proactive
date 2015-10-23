package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.AttachedCallback;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by pkhvoros on 3/27/15.
 */
public class RequestLoggerDecorator extends AttachedCallback {
	private String folderPath;
	private String identifier;
	private boolean activated;
	//    private ReifiedObjectDecorator decoratedObject;
	//    public RequestLoggerDecorator(ReifiedObjectDecorator decorator, Body body1) {
	//        super(body1);
	//        this.decoratedObject = decorator;
	//    }


	public RequestLoggerDecorator(Body body, String folderPath) {
		super(body);
		this.folderPath = folderPath;
		this.activated = true;
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
		if (activated) {
			if (identifier == null){
				identifier = generateIdentifier();
			}
			String log = "deliverrequest " + body.getID() + " "
					+ Thread.currentThread().getId() + " " + request.getMethodName()+ " "
					+ request.getSequenceNumber() + " " + System.currentTimeMillis() + " "
					+ request.getSourceBodyID() + "\n";
			writeToFile(log);
		}
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
		if (activated) {
			if (identifier == null){
				identifier = generateIdentifier();
			}
			String log = "beforerequestsent " + body.getID() + " " + Thread.currentThread().getId() + " " + request.getMethodName()+ " " + request.getSequenceNumber() + " " + System.currentTimeMillis() + " " + request.getSourceBodyID() + "\n";
			writeToFile(log);
		}
		return 0;
	}

	@Override
	public int onSendRequestAfter(Request request) {
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
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(folderPath + identifier + ".txt", true)));
			out.print(log);
			out.close();
		} catch (IOException e) {
			System.err.println("Exception while writting: multiactivity logging (requests) is now disabled.");
			this.activated = false;
		}
	}
	private String generateIdentifier(){
		return "Request_" + body.getID().toString();
	}
}