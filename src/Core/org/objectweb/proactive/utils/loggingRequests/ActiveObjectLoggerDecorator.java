package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityTracker;
import org.objectweb.proactive.multiactivity.execution.RequestExecutor;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;
import org.objectweb.proactive.multiactivity.limits.ThreadManager;
import org.objectweb.proactive.multiactivity.priority.PriorityManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by pkhvoros on 3/12/15.
 */
public class ActiveObjectLoggerDecorator extends RequestExecutor {
    private final String folderPath = "/user/pkhvoros/home/Documents/Projects/ViewerToolForActiveObject/logs/";
    private String identifier;
    public ActiveObjectLoggerDecorator(Body body, CompatibilityManager compatibilityManager, PriorityManager priorityManager, ThreadManager threadManager) {
        super(body, compatibilityManager, priorityManager, threadManager);
    }

    public ActiveObjectLoggerDecorator(Body body, CompatibilityTracker compatibility, PriorityManager priority, ThreadManager threadManager, int activeLimit, boolean hardLimit, boolean hostReentrant) {
        super(body, compatibility, priority, threadManager, activeLimit, hardLimit, hostReentrant);
    }

    public void serveStarted(RunnableRequest r){
        super.serveStarted(r);
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
//        System.out.println(builder);
    }

    public void serveStopped(RunnableRequest r){
        super.serveStopped(r);
        StringBuilder builder = new StringBuilder();
        builder.append("ServeStopped\n");
        builder.append(getBodyId() + "\n");
        builder.append(Thread.currentThread().getId() + "\n");
        builder.append(r + "\n");
        builder.append(System.currentTimeMillis() + "\n");
        writeToFile(builder);
//        System.out.println(builder);
    }
    private String generateIdentifier(){
        return "ActiveObject_" + getBodyId();
    }
    private void writeToFile(StringBuilder log){
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folderPath + identifier + ".txt", true)));
            out.print(log);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
}
