package org.objectweb.proactive.core.jmx.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.util.UrlBuilder;


/**
 * Names used in the creation of ProActive ObjectNames.
 * @author ProActiveRuntime
 */
public class FactoryName {
    public static final String OS = "java.lang:type=OperatingSystem";
    public static final String NODE_TYPE = "Node";
    public static final String NODE = "org.objectweb.proactive.core.node:type=" +
        NODE_TYPE;
    public static final String HOST_TYPE = "Host";
    public static final String HOST = "org.objectweb.proactive.core.host:type=" +
        HOST_TYPE;
    public static final String RUNTIME_TYPE = "Runtime";
    public static final String RUNTIME = "org.objectweb.proactive.core.runtimes:type=" +
        RUNTIME_TYPE;
    public static final String AO_TYPE = "AO";
    public static final String AO = "org.objectweb.proactive.core.body:type=" +
        AO_TYPE;

    /**
     * Creates a ObjectName corresponding to an active object.
     * @param id The unique id of the active object.
     * @return The ObjectName corresponding to the given id.
     */
    public static ObjectName createActiveObjectName(UniqueID id) {
        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.AO + ", id=" +
                    id.toString().replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return oname;
    }

    /**
     * Creates a ObjectName corresponding to a node.
     * @param runtimeUrl The url of the ProActive Runtime.
     * @param nodeName The name of the node
     * @return The ObjectName corresponding to the given id.
     */
    public static ObjectName createNodeObjectName(String runtimeUrl,
        String nodeName) {
        String host = UrlBuilder.getHostNameFromUrl(runtimeUrl);
        String name = UrlBuilder.getNameFromUrl(runtimeUrl);
        String protocol = UrlBuilder.getProtocol(runtimeUrl);
        int port = UrlBuilder.getPortFromUrl(runtimeUrl);

        runtimeUrl = UrlBuilder.buildUrl(host, name, protocol, port);

        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.NODE + ",runtimeUrl=" +
                    runtimeUrl.replace(':', '-') + ", nodeName=" +
                    nodeName.replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return oname;
    }

    /**
     * Creates a ObjectName corresponding to a ProActiveRuntime.
     * @param url The url of the ProActiveRuntime.
     * @return The ObjectName corresponding to the given url.
     */
    public static ObjectName createRuntimeObjectName(String url) {
        String host = UrlBuilder.getHostNameFromUrl(url);
        String name = UrlBuilder.getNameFromUrl(url);
        String protocol = UrlBuilder.getProtocol(url);
        int port = UrlBuilder.getPortFromUrl(url);

        url = UrlBuilder.buildUrl(host, name, protocol, port);

        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.RUNTIME + ",url=" +
                    url.replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return oname;
    }
}
