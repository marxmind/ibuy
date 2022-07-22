package com.italia.buynsell.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.utils.DateUtils;

@Named("chatBean")
@ViewScoped
public class ChatBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 455457687968751L;
	
	private String chatMe;
	private String chatMsg;
	
	@PostConstruct
	public void init(){
		loadHistory();
	}
	
	private void loadHistory(){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		try{
		String FILE_LOG_NAME = "chatlog";
		String FILE_LOG_TMP_NAME = "tmpchatlog";
		String EXT = ".log";
		
		ReadApplicationDetails r = new ReadApplicationDetails();
        String logpath = r.getChatLog();
        
        String finalFile = logpath + FILE_LOG_NAME + EXT;// + "-" + DateUtils.getCurrentYYYYMMDD() + EXT;
        String tmpFileName = logpath + FILE_LOG_TMP_NAME + EXT;// + "-" + DateUtils.getCurrentYYYYMMDD() + EXT;
        
        File originalFile = new File(finalFile);
        
        if(!originalFile.exists()){
        	originalFile.createNewFile();
        }
        
        BufferedReader br = new BufferedReader(new FileReader(originalFile));

       
        String line = null;
        // Read from the original file
        String str = "";
        while ((line = br.readLine()) != null) {
            //pw.println(line);
        	str +=line + "\n";
        }
       
        br.close();
        setChatMsg(str);
        		
		}catch(Exception e){e.getMessage();}
		
	}
	
	public void saveMsg(String msg){
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		try{
		String FILE_LOG_NAME = "chatlog";
		String FILE_LOG_TMP_NAME = "tmpchatlog";
		String EXT = ".log";
		
		ReadApplicationDetails r = new ReadApplicationDetails();
		String logpath = r.getChatLog();
        
        File dir = new File(logpath);
        dir.mkdir();
        
        String finalFile = logpath + FILE_LOG_NAME + EXT; //+ "-" + DateUtils.getCurrentYYYYMMDD() + EXT;
        String tmpFileName = logpath + FILE_LOG_TMP_NAME + EXT;// + "-" + DateUtils.getCurrentYYYYMMDD() + EXT;
        
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
        //int i=1;
       // for(String action : msg){
        	pw.println(DateUtils.getCurrentDateMMDDYYYYTIME() + " " + onlineUser()  + ": " + msg);
        //}
        
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
	
	private String onlineUser(){
		try{
			HttpSession session = SessionBean.getSession();
			String proc_by = session.getAttribute("username").toString();
			return proc_by;
			}catch(Exception e){
				return "error";
			}
	}
	
	public void sendMsg(){
		String text = getChatMe();
		System.out.println("send text " + text);
		saveMsg(text);
		setChatMe(null);
		loadHistory();
	}
	
	public void clear(){
		ReadApplicationDetails r = new ReadApplicationDetails();
		String logpath = r.getChatLog();
		String FILE_LOG_NAME = logpath +  "chatlog.log";
		
		 File originalFile = new File(FILE_LOG_NAME);
		 originalFile.delete();
		 setChatMe(null);
		 setChatMsg(null);
	}
	
	public String getChatMsg() {
		return chatMsg;
	}

	public void setChatMsg(String chatMsg) {
		this.chatMsg = chatMsg;
	}

	public String getChatMe() {
		return chatMe;
	}

	public void setChatMe(String chatMe) {
		this.chatMe = chatMe;
	}

}
