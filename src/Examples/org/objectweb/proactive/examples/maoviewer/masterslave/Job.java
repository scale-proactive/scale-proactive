package org.objectweb.proactive.examples.maoviewer.masterslave;

import java.io.Serializable;


public class Job implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int amountOfWork;
    private boolean hasStarted = false;

    public Job(int amountOfWork) {
        this.amountOfWork = amountOfWork;
    }

    public int getAmountOfWork() {
        return amountOfWork;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setAmountOfWork(int amountOfWork) {
        this.amountOfWork = amountOfWork;
    }
}
