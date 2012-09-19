package functionalTests.multiactivities.abourdin.inheritance;

import org.objectweb.proactive.api.PAActiveObject;


public class InheritanceMain {

    public static void main(String[] args) {
        MAObject mao;
        Itf impl;
        try {
            mao = PAActiveObject.newActive(MAObject.class, new Object[] {});
            mao.print("test");
            mao.print("test");
            mao.print("test");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mao.testItf();
            mao.testItf();
            mao.testItf();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            impl = PAActiveObject.newActive(MAObject.class, new Object[] {});
            impl.testItf();
            impl.testItf();
            impl.testItf();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
