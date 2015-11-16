package functionalTests.loggingRequests;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.loggingRequests.deadlock.FirstActiveObject;

public class LoggerDeadlockSolutionTest {

	public static final String SEARCHED_STRING = "needed result";

	@Test
	public void runDeadlockExample(){
		cleanLogFolder();
		deadlockExample();
	}

	private void cleanLogFolder() {
		File logFolder;
		logFolder = new File(ProActiveConfiguration.getInstance().getProperty(
				CentralPAPropertyRepository.PA_MULTIACTIVITY_DEFAULT_LOGGING.getName()));
		if (logFolder.exists()) {
			File[] logFiles = logFolder.listFiles();
			for (File logFile: logFiles) {
				logFile.delete();
			}
			logFolder.delete();
		}
	}

	private void deadlockExample(){
		FirstActiveObject firstExample;
		try {
			firstExample = PAActiveObject.newActive(FirstActiveObject.class, null);
			String str = firstExample.start();
			// Check app logic
			Assert.assertTrue(str.equals(SEARCHED_STRING));
			// Check log location 
			File logFolder = new File(ProActiveConfiguration.getInstance().getProperty(
						CentralPAPropertyRepository.PA_MULTIACTIVITY_DEFAULT_LOGGING.getName()));
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
		}
	}
}
