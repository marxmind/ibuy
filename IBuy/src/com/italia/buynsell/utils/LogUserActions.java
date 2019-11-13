package com.italia.buynsell.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.italia.buynsell.controller.ReadApplicationDetails;
/*
 * @uthor mark italia
 */
public class LogUserActions {
	
	public static void logUserActions(List<String> actions){
		ReadApplicationDetails rd = new ReadApplicationDetails();
		if("yes".equalsIgnoreCase(rd.getIncludeLog())){
		try{
		String FILE_LOG_NAME = "systemlog";
		String FILE_LOG_TMP_NAME = "tmpsystemlog";
		String EXT = ".log";
		
		ReadApplicationDetails r = new ReadApplicationDetails();
        String logpath = r.getLogPath();
        
        String finalFile = logpath + FILE_LOG_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
        String tmpFileName = logpath + FILE_LOG_TMP_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
        
        File originalFile = new File(finalFile);
        
        if(!originalFile.exists()){
        	originalFile.createNewFile();
        }
        
        BufferedReader br = new BufferedReader(new FileReader(originalFile));

        // Construct the new file that will later be renamed to the original
        // filename.
        File tempFile = new File(tmpFileName);
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

        String line = null;
        // Read from the original file and write to the new
        while ((line = br.readLine()) != null) {
            pw.println(line);
        }
        //added new data into file
        int i=1;
        for(String action : actions){
        	pw.println(DateUtils.getCurrentDateMMDDYYYYTIME() + "["+ i++ +"] " + action);
        }
        
        pw.flush();
        pw.close();
        br.close();

        // Delete the original file
        if (!originalFile.delete()) {
            System.out.println("Could not delete file");
            return;
        }

        // Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(originalFile))
            System.out.println("Could not rename file");
		
		}catch(Exception e){e.getMessage();}
		}
	}
	 
}
