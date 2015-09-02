package org.objectweb.proactive.core.body;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;

public abstract class AttachedCallback {
	
	protected Body body;
	
	public AttachedCallback(Body body) {
        this.body = body;
    }

    public abstract int onReceiveReply(Reply reply);

    public abstract int onReceiveRequest(Request request);

    public abstract int onDeliverReply(Reply reply);

    public abstract int onDeliverRequest(Request request);

    public abstract int onSendReplyBefore(Reply reply);

    public abstract int onSendReplyAfter(Reply reply);

    public abstract int onSendRequestBefore(Request request);

    public abstract int onSendRequestAfter(Request request);

    public abstract int onServeRequestBefore(Request request);

    public abstract int onServeRequestAfter(Request request);
    
}
