package org.objectweb.proactive.utils.loggingRequests;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityMap;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by pkhvoros on 7/9/15.
 */
public class CompatibilityLogger {

    public static void logCompatibility(CompatibilityMap compatibilityMap, String folderPath, String activeObjectId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(activeObjectId + "\n");
        stringBuilder.append("compatibility\n");
        for (Map.Entry<String, MethodGroup> entry : compatibilityMap.getGroups().entrySet()){
//            stringBuilder.append(entry.getValue().name + " " + ((entry.getValue().isSelfCompatible()) ? 1 : 0));
            stringBuilder.append(entry.getValue().name);
            for (MethodGroup methodGroup: entry.getValue().getCompatibleWith()){
                stringBuilder.append(" " + methodGroup.name);
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("methods");
        for (Map.Entry<String, MethodGroup> entry : compatibilityMap.getMembership().entrySet()){
            if (entry != null && entry.getKey() != null && entry.getValue() != null)
                stringBuilder.append("\n" + entry.getKey() + " " + entry.getValue().name);
        }
        if (stringBuilder.length() != 0){
            writeToFile(stringBuilder.toString() + " ", folderPath + "Compatibility_" + activeObjectId + ".txt");
        }
    }
    private static void writeToFile(String log, String path){
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
            out.print(log);
            out.close();
        } catch (IOException e) {
        	System.err.println("Could not write compatibility information.");
        }
    }
}