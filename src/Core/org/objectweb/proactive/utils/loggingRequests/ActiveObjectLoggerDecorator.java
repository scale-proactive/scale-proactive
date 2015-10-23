package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.execution.RequestExecutor;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;
import org.objectweb.proactive.multiactivity.limits.ThreadManager;
import org.objectweb.proactive.multiactivity.priority.PriorityManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by pkhvoros on 3/12/15.
 */
public class ActiveObjectLoggerDecorator extends RequestExecutor {
	private String folderPath;
	private String identifier;
	private boolean activated;
	public ActiveObjectLoggerDecorator(Body body, CompatibilityManager compatibilityManager, PriorityManager priorityManager, ThreadManager threadManager, String folderPath) {
		super(body, compatibilityManager, priorityManager, threadManager);
		this.folderPath = folderPath;
		this.activated = true;
	}

	public void serveStarted(RunnableRequest r){
		super.serveStarted(r);
		if (activated) {
			if (identifier == null){
				identifier = generateIdentifier();
			}
			StringBuilder builder = new StringBuilder();
			builder.append("ServeStarted\n");
			builder.append(getBodyId() + "\n");
			builder.append(Thread.currentThread().getId() + "\n");
			builder.append(r + "\n");
			builder.append(System.currentTimeMillis() + "\n");
			writeToFile(builder);
		}
	}

	public void serveStopped(RunnableRequest r){
		super.serveStopped(r);
		if (activated) {
			StringBuilder builder = new StringBuilder();
			builder.append("ServeStopped\n");
			builder.append(getBodyId() + "\n");
			builder.append(Thread.currentThread().getId() + "\n");
			builder.append(r + "\n");
			builder.append(System.currentTimeMillis() + "\n");
			writeToFile(builder);
		}
	}

	private String generateIdentifier(){
		return "ActiveObject_" + getBodyId();
	}
	private void writeToFile(StringBuilder log){
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folderPath + identifier + ".txt", true)));
			out.print(log);
			out.close();
		} catch (IOException e) {
			System.err.println("Exception while writting: multiactivity logging (objects) is now disabled.");
			this.activated = false;
		}
	}
}