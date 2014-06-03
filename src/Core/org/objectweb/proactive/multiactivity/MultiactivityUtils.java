package org.objectweb.proactive.multiactivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * This class is meant to monitor the priority features in order to be able to 
 * run experiments. It provides constants to use in such experiments, as well 
 * as a devoted logger.
 * 
 * @author The ProActive Team
 */
public class MultiactivityUtils {

	/** Enable/disable multiactivity logs */
	public static final boolean LOG_ENABLED = false;
	
	// Constants used to log messages
	private static final Logger LOGGER;
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String FILE_SEP = System.getProperty("file.separator");
	private static final String LOGGER_NAME = "MultiactivityLogger";	
	
	// Location and format of logs
	public static final String LOG_SEPARATOR = "\t";
	public static final String LOG_PATH = "/tmp/multiactivity_logs";
	public static final String LOG_FILE = "multiactivity.log";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH);
	
	// Particular strings used in logs about priorities. Useful to parse the 
	// corresponding information
	public static final String INSERTION_TIME = "INSERTION-TIME";
	public static final String SERVICE_TIME = "SERVICE-TIME";
	public static final String METHOD_TIME = "METHOD-TIME";
	public static final String VERY_HIGH_PRIORITY_METHOD_NAME = "veryHighPriority";
	public static final String VERY_LOW_PRIORITY_METHOD_NAME = "veryLowPriority";

	static {
		// Logger initialization
		Layout layout = new PatternLayout("%m");
		LOGGER = Logger.getLogger(LOGGER_NAME);
		LOGGER.setLevel(Level.TRACE);
		
		try {
			File file = new File(LOG_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(LOG_PATH + FILE_SEP + LOG_FILE);
			FileAppender appender = new FileAppender(
					layout, file.getAbsolutePath(), false);
			appender.setImmediateFlush(true);
			appender.activateOptions();
			LOGGER.removeAllAppenders();
			LOGGER.addAppender(appender);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// The parsed date must have the exact same format as defined, do not 
		// apply heuristics
		DATE_FORMAT.setLenient(false);
	}
	
	/**
	 * This method should be used to log messages related to priorities. 
	 * Output location is written in LOG_PATH and LOG_FILE variables.
	 * @param message
	 */
	public static void logMessage(String message) {
		LOGGER.info(message);
	}
	
}
