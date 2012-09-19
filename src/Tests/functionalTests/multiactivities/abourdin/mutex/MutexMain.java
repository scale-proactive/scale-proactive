package functionalTests.multiactivities.abourdin.mutex;

import org.objectweb.proactive.api.PAActiveObject;


public class MutexMain {

    public static void main(String[] args) {
        MAObject mao;
        try {
            mao = PAActiveObject.newActive(MAObject.class, new Object[] {});

            mao.mutexCaller();
            mao.mutexCaller();
            for (int i = 0; i < 5; i++) {
                mao.mutex();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
