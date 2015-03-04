package org.objectweb.proactive.core.body.ft.extension;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.ReifiedObjectDecorator;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.message.MessageInfo;
import org.objectweb.proactive.core.body.ft.message.ReplyLog;
import org.objectweb.proactive.core.body.ft.message.RequestLog;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.MessageInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.cic.managers.FTManagerCIC;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.message.Message;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.reply.ReplyImpl;
import org.objectweb.proactive.core.body.request.AwaitedRequest;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Utils;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.MutableLong;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class FTDecorator extends ReifiedObjectDecorator {

	private static final long serialVersionUID = 1L;

	public static final String keyMethod = "_checkpoint_request" ;
	public static final String FT_MAO_GROUP = "_ft_mao_group";


	protected static Logger logger = ProActiveLogger.getLogger(
			Loggers.FAULT_TOLERANCE_EXTENSION);

	private FTManagerCIC manager;

	private int rdvValueReply;
	private UniversalBody destinationReply;
	private int rdvValueRequest;
	private UniversalBody destinationRequest;

	public FTDecorator(Body body, FTManagerCIC manager) {
		super(body);
		this.manager = manager;
	}

	public void _checkpoint_request() {
		if (logger.isDebugEnabled()) {
			logger.debug("About to checkpoint (through " + keyMethod + 
					" method) from reified object decorator.");
		}
		((AbstractBody) this.body).getFTManager().checkpoint(null);
	}

	public boolean matchesKeyMethod(String name) {
		return keyMethod.equals(name);
	}

	@Override
	public int onReceiveReply(Reply reply) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onReceiveReply (for body:" +
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + reply.getMethodName());
		}
		reply.setFTManager(this.manager);
		return this.incarnationTest(reply);
	}

	@Override
	public int onReceiveRequest(Request request) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onReceiveRequest (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}
		request.setFTManager(this.manager);
		return this.incarnationTest(request);
	}

	/*
	 * This method test if the message m has to be take into account by the receiver;
	 * If not, set the ignore tag in m.
	 */
	private int incarnationTest(Message m) {
		if (this.isSignificant(m)) {
			MessageInfoCIC mi = (MessageInfoCIC) m.getMessageInfo();
			int localInt = this.manager.getIncarnation();
			int inc = mi.incarnation;
			if (inc > localInt) {
				// this body will recover
				m.setIgnoreIt(true);
				return FTManagerCIC.RESEND_MESSAGE;
			} else if (inc < localInt) {
				// force the sender to recover and ignore this message
				m.setIgnoreIt(true);
				return FTManagerCIC.RECOVER;
			}
		}
		return 0; //This value is not returned to the sender
	}

	@Override
	public synchronized int onDeliverReply(Reply reply) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onDeliverReply (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + reply.getMethodName());
		}
		int currentCheckpointIndex = this.manager.getCheckpointIndex();
		if (this.isSignificant(reply)) {
			MessageInfoCIC mi = (MessageInfoCIC) reply.getMessageInfo();

			// history closure
			this.updateHistory(mi.historyIndex);
			// udpate checkpoint index
			if (mi.checkpointIndex > currentCheckpointIndex) {
				generateCheckpointRequest();
			}
		}
		return currentCheckpointIndex;
	}

	@Override
	public synchronized int onDeliverRequest(Request request) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onDeliverRequest (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}
		int currentCheckpointIndex = this.manager.getCheckpointIndex();

		//System.out.println(""+ this.bodyID + " receive " + request);
		if (this.isSignificant(request)) {
			//System.out.println(""+ this.bodyID + " receive significant " + request);
			MessageInfoCIC mi = (MessageInfoCIC) request.getMessageInfo();

			// history closure
			this.updateHistory(mi.historyIndex);
			//is there any corresponding awaited request ?
			if (!(this.updateAwaitedRequests(request))) {
				if (FTManagerCIC.isOCEnable || this.manager.getCompletingCheckpoint()) {
					synchronized (this.manager.getHistoryLock()) {
						// if no, an awaited request is added to history
						this.manager.getHistory().add(request.getSourceBodyID());
					}

					// inc the historized index
					this.manager.setDeliveredRequestsCounter(this.manager.getDeliveredRequestsCounter()+1);
					// manage local vector clock only if needed
					if (FTManagerCIC.isOCEnable) {
						// set the position in history of the request
						mi.positionInHistory = this.manager.getDeliveredRequestsCounter();
						// update local vector clock
						this.updateLocalVectorClock(mi.vectorClock);
					}
				}
			} else {
				//this request must be then ignored...
				request.setIgnoreIt(true);
			}
			// udpate checkpoint index
			int ckptIndex = mi.checkpointIndex;
			if (ckptIndex > currentCheckpointIndex) {
				generateCheckpointRequest();
				// mark the request that is orphan; it will be changed in awaited req in next ckpt
				// oprhan du indexCkpt+1 a mi.ckptIndex compris
				mi.isOrphanFor = (char) ckptIndex;
				//System.out.println("" + this.bodyID + " will have orphans in checkpoint " + ckptIndex);
			}
		}
		return currentCheckpointIndex;
	}

	/*
	 * Creates a request for checkpointing and adds it in the body queue.
	 */
	private void generateCheckpointRequest() {
		logger.debug("Creating a checkpoint request for object: " +
				this.body.getReifiedObject().getClass().getSimpleName());
		try {
			this.body.receiveRequest(new RequestImpl(
					new MethodCall(this.body.getDecorator().
							getClass().getDeclaredMethod(FTDecorator.keyMethod, 
									new Class<?>[]{}), null, null), true));
		} 
		catch (NoSuchMethodException | SecurityException | IOException | RenegotiateSessionException e) {
			e.printStackTrace();
		}
		StringBuilder b = new StringBuilder("Request queue of object: " +
				this.body.getReifiedObject() + ": ");
		Iterator<Request> i = this.body.getRequestQueue().iterator();
		while (i.hasNext()) {
			b.append(i.next().getMethodName() + " ");
		}
		logger.debug(b.toString());
	}

	/*
	 * Close and commit the current history if needed.
	 */
	private void updateHistory(int index) {
		if (index > this.manager.getHistoryIndex()) {
			// commit minimal history to the server
			this.manager.commitHistories(this.manager.getCheckpointIndex(), this.manager.getDeliveredRequestsCounter(), true, true);
			if (this.manager.getCompletingCheckpoint()) {
				this.manager.setCompletingCheckpoint(false);
			}
		}
	}

	/*
	 * Return true if the message m is significant for the protocol, i.e
	 * if it's not sent by a non_ft body nor a half body
	 */
	private boolean isSignificant(Message m) {
		return ((m.getMessageInfo() != null) && (!m.getMessageInfo().isFromHalfBody()));
	}

	/*
	 * Update the local vector clock regarding the paramater.
	 * if local[i]<param[i] or local[i] doesn't exist, then local[i]=param[i];
	 */
	private void updateLocalVectorClock(Hashtable<UniqueID, MutableLong> vectorClock) {
		Enumeration<UniqueID> ids = vectorClock.keys();
		MutableLong localClock;
		MutableLong senderClock = null;
		UniqueID id = null;
		while (ids.hasMoreElements()) {
			id = ids.nextElement();
			localClock = (this.manager.getLocalVectorClock().get(id));
			senderClock = (vectorClock.get(id));
			if (localClock == null) {
				// there is no clock for the AO id
				this.manager.getLocalVectorClock().put(id, new MutableLong(senderClock.getValue()));
			} else if (localClock.isLessThan(senderClock)) {
				// local clock is not uptodate
				localClock.setValue(senderClock.getValue());
			}
		}
	}

	@Override
	public synchronized int onSendReplyBefore(Reply reply) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onSendReplyBefore (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + reply.getMethodName());
		}
		// set message info values
		this.manager.getForSentReply().checkpointIndex = (char) this.manager.getCheckpointIndex();
		this.manager.getForSentReply().historyIndex = (char) this.manager.getHistoryIndex();
		this.manager.getForSentReply().incarnation = (char) this.manager.getIncarnation();
		this.manager.getForSentReply().lastRecovery = (char) this.manager.getLastRecovery();
		this.manager.getForSentReply().isOrphanFor = Character.MAX_VALUE;
		this.manager.getForSentReply().fromHalfBody = false;
		this.manager.getForSentReply().vectorClock = null;
		reply.setMessageInfo(this.manager.getForSentReply());

		// output commit
		if (FTManagerCIC.isOCEnable && this.isOutputCommit(reply)) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug(this.body.getID() + " is output commiting for reply " + reply);
				}
				this.manager.getStorage().outputCommit(this.manager.getForSentReply());
			} catch (RemoteException e) {
				logger.error("**ERROR** Cannot perform output commit");
				e.printStackTrace();
			}
		}

		return 0;
	}

	@Override
	public synchronized int onSendRequestBefore(Request request) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onSendRequestBefore (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}
		// set message info values
		this.manager.getForSentRequest().checkpointIndex = (char) this.manager.getCheckpointIndex();
		this.manager.getForSentRequest().historyIndex = (char) this.manager.getHistoryIndex();
		this.manager.getForSentRequest().incarnation = (char) this.manager.getIncarnation();
		this.manager.getForSentRequest().lastRecovery = (char) this.manager.getLastRecovery();
		this.manager.getForSentRequest().isOrphanFor = Character.MAX_VALUE;
		this.manager.getForSentRequest().fromHalfBody = false;
		if (FTManagerCIC.isOCEnable) {
			this.manager.getForSentRequest().vectorClock = this.manager.getLocalVectorClock();
		}
		request.setMessageInfo(this.manager.getForSentRequest());

		// output commit
		if (FTManagerCIC.isOCEnable && this.isOutputCommit(request)) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug(this.body.getID() + " is output commiting for request " + request);
				}
				this.manager.getStorage().outputCommit(this.manager.getForSentRequest());
			} catch (RemoteException e) {
				logger.error("**ERROR** Cannot perform output commit");
				e.printStackTrace();
			}
		}
		return 0;
	}

	/*
	 * Return true if the sending of the paramter message is an output commit
	 * ** TEST IMPLEMENTATION **
	 */
	private boolean isOutputCommit(Message m) {
		if (FTManagerCIC.isOCEnable) {
			Request r = (Request) m;
			if (r.getMethodName().equals("logEvent")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public synchronized int onSendReplyAfter(Reply reply) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onSendReplyAfter (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + reply.getMethodName());
		}
		// if return value is RESEND, receiver have to recover --> resend the message
		if (this.rdvValueReply == FTManagerCIC.RESEND_MESSAGE) {
			try {
				reply.setIgnoreIt(false);
				Thread.sleep(FTManager.TIME_TO_RESEND);
				int rdvValueBis = this.manager.sendReply(reply, this.destinationReply);
				this.setOnSendReplyAfterParameters(rdvValueBis, this.destinationReply);
				return this.onSendReplyAfter(reply);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int currentCheckpointIndex = this.manager.getCheckpointIndex();

		// update checkpoint index
		if (this.rdvValueReply > currentCheckpointIndex) {
			generateCheckpointRequest();
			// log this in-transit message
			this.manager.extendReplyLog(this.rdvValueReply);
			// must make a deep copy of result !
			try {
				Reply toLog = null;

				//try {
				//    toLog = new ReplyImpl(reply.getSourceBodyID(),
				//            reply.getSequenceNumber(), reply.getMethodName(),
				//            (MethodCallResult) Utils.makeDeepCopy(reply.getResult()),
				//            owner.getProActiveSecurityManager());
				//} catch (SecurityNotAvailableException e1) {
				toLog = new ReplyImpl(reply.getSourceBodyID(), reply.getSequenceNumber(), reply
						.getMethodName(), (MethodCallResult) Utils.makeDeepCopy(reply.getResult()), null);
				//}
				ReplyLog log = new ReplyLog(toLog, this.destinationReply.getRemoteAdapter());
				for (int i = currentCheckpointIndex + 1; i <= this.rdvValueReply; i++) {
					(this.manager.getReplyToResend().get(new Integer(i))).add(log);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	@Override
	public synchronized int onSendRequestAfter(Request request)
			throws RenegotiateSessionException, CommunicationForbiddenException {
		if (logger.isDebugEnabled()) {
			logger.debug("#onSendRequestAfter (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}
		//	if return value is RESEDN, receiver have to recover --> resend the message
		if (this.rdvValueRequest == FTManagerCIC.RESEND_MESSAGE) {
			try {
				request.resetSendCounter();
				request.setIgnoreIt(false);
				Thread.sleep(FTManager.TIME_TO_RESEND);
				int rdvValueBis = this.manager.sendRequest(request, this.destinationRequest);
				this.setOnSendRequestAfterParameters(rdvValueBis, this.destinationRequest);
				return this.onSendRequestAfter(request);
			} catch (RenegotiateSessionException e1) {
				throw e1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int currentCheckpointIndex = this.manager.getCheckpointIndex();

		// update checkpoint index
		if (this.rdvValueRequest > currentCheckpointIndex) {
			generateCheckpointRequest();
			// log this in-transit message in the rdvValue-currentIndex next checkpoints
			this.manager.extendRequestLog(this.rdvValueRequest);
			try {
				//must make deep copy of paramteres
				request.getMethodCall().makeDeepCopyOfArguments();
				//must reset the send counter (this request has not been forwarded)
				request.resetSendCounter();
				RequestLog log = new RequestLog(request, this.destinationRequest.getRemoteAdapter());
				for (int i = currentCheckpointIndex + 1; i <= this.rdvValueRequest; i++) {
					//System.out.println(""+this.bodyID + " logs a request for " + destination.getID());
					this.manager.getRequestToResend().get(new Integer(i)).add(log);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	@Override
	public int onServeRequestBefore(Request request) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onServeRequestBefore (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}

		// checkpoint if needed
		if (!request.getMethodName().equals(FTDecorator.keyMethod) && 
				this.haveToCheckpoint()) {
			this.manager.checkpoint(request);
		}

		// update the last served request index only if needed
		if (FTManagerCIC.isOCEnable) {
			MessageInfo mi = request.getMessageInfo();
			if (mi != null) {
				long requestIndex = ((MessageInfoCIC) (mi)).positionInHistory;
				if (this.manager.getLastServedRequestIndex().getValue() < requestIndex) {
					this.manager.getLastServedRequestIndex().setValue(requestIndex);
				}
			}
		}
		return 0;
	}

	@Override
	public int onServeRequestAfter(Request request) {
		if (logger.isDebugEnabled()) {
			logger.debug("#onServeRequestAfter (for body:" + 
					this.body.getReifiedObject().getClass().getSimpleName() 
					+ "): " + request.getMethodName());
		}
		return 0;
	}

	/*
	 * search for an awaited request from r.source.
	 * if any, unfreeze ar and remove it from awaitedRequests list.
	 * WARNING : this.awaitedRequests must be ordered. Do not use a map !
	 */
	private boolean updateAwaitedRequests(Request r) {
		AwaitedRequest ar = null;
		Iterator<AwaitedRequest> it = this.manager.getAwaitedRequests().iterator();
		while (it.hasNext()) {
			AwaitedRequest arq = (it.next());
			if ((arq.getAwaitedSender()).equals(r.getSourceBodyID())) {
				ar = arq;
				break;
			}
		}
		if (ar != null) {
			//System.err.println(""+ this.bodyID + " Request is updated by " + r.getSourceBodyID());
			ar.setAwaitedRequest(r);
			this.manager.getAwaitedRequests().remove(ar);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * return true if this ao have to checkpoint
	 * should checkpoint if TTC is elapsed
	 */
	private boolean haveToCheckpoint() {
		if ((this.manager.getCheckpointTimer() + this.manager.getTTC()) < System.currentTimeMillis()) {
			return true;
		} else {
			return false;
		}
	}

	public void setOnSendReplyAfterParameters(int res, UniversalBody destination) {
		this.rdvValueReply = res;
		this.destinationReply = destination;
	}
	
	public void setOnSendRequestAfterParameters(int res, UniversalBody destination) {
		this.rdvValueRequest = res;
		this.destinationRequest = destination;
	}

	/*
	 * Return the memory actually used
	 * For debugging stuff.
	 */

	//private long getUsedMem() {
	//    return (FTManagerCIC.runtime.totalMemory() -
	//    FTManagerCIC.runtime.freeMemory()) / 1024;
	//}
	//////////////////////////////////////////////////////////////////////////////////
	///////// HANDLING EVENTS ////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////


}
