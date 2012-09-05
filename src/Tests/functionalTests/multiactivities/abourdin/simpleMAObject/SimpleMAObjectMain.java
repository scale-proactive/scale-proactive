package functionalTests.multiactivities.abourdin.simpleMAObject;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

public class SimpleMAObjectMain {
	
	public static void main(String[] args) {
		MAObject mao;
		try {
			mao = PAActiveObject.newActive(MAObject.class, new Object[]{});
			mao.print("test");
			mao.print("test");
			mao.print("test");
			mao.print("test");
			mao.print("test");
			String s1 = mao.toString();
			String s2 = mao.toString();
			String s3 = mao.toString();
			System.out.println(s1);
			System.out.println(s2);
			System.out.println(s3);
			
			Thread.sleep(2000);
			System.out.println("====================================");
			
			System.out.println("result : " + mao.getResult().getValue());
			System.out.println("result : " + mao.getResult().getValue());
			System.out.println("result : " + mao.getResult().getValue());

			Thread.sleep(2000);
			System.out.println("====================================");
			
			Result r1 = mao.getResult();
			Result r2 = mao.getResult();
			Result r3 = mao.getResult();
			System.out.println("result : " + r1.getValue());
			System.out.println("result : " + r2.getValue());
			System.out.println("result : " + r3.getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
