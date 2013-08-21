package org.objectweb.proactive.multiactivity.limits;

public class ThreadPair {

	private final int minThreads;
	private final int maxThreads;
	
	public ThreadPair(int minThreads, int maxThreads) {		
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}
	
	public int getMinThreads() {
		return minThreads;
	}
	
	public int getMaxThreads() { 
		return maxThreads;
	}
	
}
