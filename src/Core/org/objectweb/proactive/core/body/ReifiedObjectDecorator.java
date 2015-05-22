package org.objectweb.proactive.core.body;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;

public abstract class ReifiedObjectDecorator implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final ReifiedObjectDecorator emptyDecorator = new EmptyDecorator(null);

	protected Body body;
	
	private Object reifiedObject;
	
	protected ReifiedObjectDecorator(Body body) {
		this.body = body;
		if (body != null) {
			this.reifiedObject = this.body.getReifiedObject();
		}
	}
	
	public Object getReifiedObject() {
		return this.reifiedObject;
	}
	
	/**
     * This method is called when a reply is received.
     * @param reply the received reply
     */
    public abstract int onReceiveReply(Reply reply);

    /**
     * This method is called when a request is received.
     * @param request the received request
     */
    public abstract int onReceiveRequest(Request request);

    /**
     * This method is called after the future is updated by the reply.
     * @param reply the reply that updates a future
     */
    public abstract int onDeliverReply(Reply reply);

    /**
     * This method is called when a request is stored in the requestqueue
     * @param request the stored request
     */
    public abstract int onDeliverRequest(Request request);

    /**
     * This method is called before the sending of a reply
     * @param reply the reply that will be sent
     */
    public abstract int onSendReplyBefore(Reply reply);

    /**
     * This method is called after the sending of a reply
     * @param reply the sent reply
     * @param rdvValue the value returned by the sending
     * @param destination the destination body of reply
     * @return depends on fault-tolerance protocol
     */
    public abstract int onSendReplyAfter(Reply reply);

    /**
     * This method is called before the sending of a request
     * @param request the request that will be sent
     * @return depends on fault-tolerance protocol
     */
    public abstract int onSendRequestBefore(Request request);

    /**
     * This method is called after the sending of a request
     * @param request the sent request
     * @param rdvValue the value returned by the sending
     * @param destination the destination body of request
     * @return depends on fault-tolerance protocol
     * @throws RenegotiateSessionException
     * @throws CommunicationForbiddenException
     */
    public abstract int onSendRequestAfter(Request request)
            throws RenegotiateSessionException, CommunicationForbiddenException;

    /**
     * This method is called before the service of a request
     * @param request the request that is served
     * @return depends on fault-tolerance protocol
     */
    public abstract int onServeRequestBefore(Request request);

    /**
     * This method is called after the service of a request
     * @param request the request that has been served
     * @return depends on fault-tolerance protocol
     */
    public abstract int onServeRequestAfter(Request request);
    
    private static class EmptyDecorator extends ReifiedObjectDecorator {

		private static final long serialVersionUID = 1L;

		protected EmptyDecorator(Body body) {
			super(body);
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
			return 0;
		}

		@Override
		public int onSendRequestAfter(Request request) throws RenegotiateSessionException,
				CommunicationForbiddenException {
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
    	
    }
    
}
