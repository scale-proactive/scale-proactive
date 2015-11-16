package functionalTests.loggingRequests.deployment;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.utils.loggingRequests.LoggerTechnicalService;

import functionalTests.GCMFunctionalTest;
import functionalTests.loggingRequests.deployment.deadlock.FirstActiveObjectDeployment;

/**
 * Checks that log folder is created and contains what it should,
 * and that logger test produces the right result.
 */
public class LoggerDeadlockSolutionDeploymentTest extends GCMFunctionalTest {

	private static final String DEADLOCK_APP_DESCRIPTOR_PATH = 
			"/functionalTests/loggingRequests/deployment/deadlock/GCMADeadlock.xml";
	private static final String DEADLOCK_VN1_NAME = "FirstActiveObject";
	public static final String DEADLOCK_VN2_NAME = "SecondActiveObject";
	public static final String SEARCHED_STRING = "needed result";

	private GCMApplication gcmApplication;
	private Node mainNode;

	public LoggerDeadlockSolutionDeploymentTest() {
		super(LoggerDeadlockSolutionDeploymentTest.class.getResource(DEADLOCK_APP_DESCRIPTOR_PATH));
	}

	@Test
	public void runDeadlockExample(){
		initTechnicalService();
		cleanLogFolder();
		deadlockExample();
	}

	private void initTechnicalService(){
		try {
			this.gcmApplication = PAGCMDeployment.loadApplicationDescriptor(
					this.applicationDescriptor);
		}
		catch (ProActiveException e1) {
			e1.printStackTrace();
		}
		this.gcmApplication.startDeployment();
		this.gcmApplication.waitReady();
	}

	private void cleanLogFolder() {
		GCMVirtualNode vn = this.gcmApplication.getVirtualNode(DEADLOCK_VN1_NAME);
		this.mainNode = vn.getANode();
		File logFolder;
		try {
			logFolder = new File(
					this.mainNode.getProperty(LoggerTechnicalService.URL_TO_LOG_FOLDER));
			if (logFolder.exists()) {
				File[] logFiles = logFolder.listFiles();
				for (File logFile: logFiles) {
					logFile.delete();
				}
				logFolder.delete();
			}
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
	}

	private void deadlockExample(){
		FirstActiveObjectDeployment firstExample;
		try {
			firstExample = PAActiveObject.newActive(FirstActiveObjectDeployment.class, null, this.mainNode);
			String str = firstExample.start(this.gcmApplication);
			// Check app logic
			Assert.assertTrue(str.equals(SEARCHED_STRING));
			// Check logs activation
			Assert.assertTrue("true".equals(
					this.mainNode.getProperty(LoggerTechnicalService.IS_ENABLED)));
			// Check log location 
			File logFolder = new File(
					this.mainNode.getProperty(LoggerTechnicalService.URL_TO_LOG_FOLDER));
			Assert.assertTrue(logFolder.exists());
			// Check number of log files
			String[] logFiles = logFolder.list();
			Assert.assertEquals(logFiles.length, 4);
			// Check log files names
			int nbAOFiles = 0;
			int nbRequestFiles = 0;
			int nbFirstAOFiles = 0;
			int nbSecondAOFiles = 0;
			for (String logFile: logFiles) {
				if (logFile.contains("ActiveObject_ActiveObject")) nbAOFiles++;
				if (logFile.contains("Request_ActiveObject")) nbRequestFiles++;
				if (logFile.contains("FirstActiveObject")) nbFirstAOFiles++;
				if (logFile.contains("SecondActiveObject")) nbSecondAOFiles++;
			}
			Assert.assertEquals(nbAOFiles, 2);
			Assert.assertEquals(nbRequestFiles, 2);
			Assert.assertEquals(nbFirstAOFiles, 2);
			Assert.assertEquals(nbSecondAOFiles, 2);
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
	}

}