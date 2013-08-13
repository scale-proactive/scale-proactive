package org.objectweb.proactive.multiactivity.priority;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class PriorityUtils {

	private static final Logger LOGGER;
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String FILE_SEP = System.getProperty("file.separator");	
	private static final String LOGGER_NAME = "PriorityLogger";	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH);
	
	public static final String LOG_PATH = "/local/home/jrochas/tmp/priority_logs";
	public static final String LOG_FILE = "priority.log";	

	static {
		// Logger initialization
		Layout layout = new PatternLayout("%m");
		LOGGER = Logger.getLogger(LOGGER_NAME);
		LOGGER.setLevel(Level.INFO);
		try {
			File file = new File(LOG_PATH);
			file.mkdirs();
			file = new File(LOG_PATH + FILE_SEP + LOG_FILE);
			FileAppender appender = new FileAppender(
					layout, file.getAbsolutePath(), false);
			appender.setImmediateFlush(true);
			appender.activateOptions();
			LOGGER.removeAllAppenders();
			LOGGER.addAppender(appender);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// The parsed date must have the exact same format, do not apply 
		// heuristics
		DATE_FORMAT.setLenient(false);
	}
	
	public static void logMessage(String message) {
		LOGGER.info(message + PriorityUtils.LINE_SEP);
	}
}
