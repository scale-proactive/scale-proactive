package functionalTests.multiactivities.abourdin.simpleMAObject;

import java.io.Serializable;

public class Result implements Serializable {
	private String value;
	
	public Result(){
		//
	}

	public Result(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
