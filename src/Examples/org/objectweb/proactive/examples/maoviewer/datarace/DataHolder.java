package org.objectweb.proactive.examples.maoviewer.datarace;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.multiactivity.MultiActiveService;

import java.io.Serializable;


public class DataHolder implements RunActive,Serializable {

	private static final long serialVersionUID = 1L;
	
	private int value;
	
    @Override
    public void runActivity(Body body) {
        MultiActiveService service = new MultiActiveService(body);
        while (body.isActive()) {
            service.multiActiveServing();
        }
    }
    
    public void write(int value){
        this.value = value;
    }
    
    public int read(){
    	return value;
    }
}
