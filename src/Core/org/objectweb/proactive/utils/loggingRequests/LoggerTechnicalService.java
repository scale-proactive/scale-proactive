package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.node.Node;

import java.util.Map;

/**
 * Created by pkhvoros on 4/15/15.
 */
public class LoggerTechnicalService implements TechnicalService {

	private static final long serialVersionUID = 1L;
	
	public static final String IS_ENABLED = "is_enabled";
    public static final String URL_TO_LOG_FOLDER = "url_to_log_file";
    private boolean isEnabled;
    private String logUrl;
    @Override
    public void init(Map<String, String> argValues) {
        this.isEnabled = Boolean.parseBoolean(argValues.get(IS_ENABLED));
        this.logUrl = argValues.get(URL_TO_LOG_FOLDER);
    }

    @Override
    public void apply(Node node) {
        try {
            if (isEnabled) {
                node.setProperty(IS_ENABLED, Boolean.toString(isEnabled));
                node.setProperty(URL_TO_LOG_FOLDER, logUrl);
            }
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }
}