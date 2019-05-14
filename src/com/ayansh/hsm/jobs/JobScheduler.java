package com.ayansh.hsm.jobs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ayansh.hsm.common.Application;
import com.ayansh.hsm.common.Society;

public class JobScheduler extends Application{
	
private static JobScheduler app;
		
	public static JobScheduler getInstance(){
		
		if(app == null){
		
			app = new JobScheduler();
			
		}
		
		return app;
		
	}
	
	private List<Society> societyList;
	
	private JobScheduler(){
		
	}
	
	public void initializeApplication() throws IOException, SQLException{
		
		super.initializeApplication();
		
		initializeExecutorService();
		
	}
	
	private void loadSocietyList() {
		
		societyList = new ArrayList<Society>();

		try {
			
			JSONArray file_contents = readJSONFile("society_list.json");
			
			for(int i=0; i<file_contents.length(); i++) {
				
				JSONObject society_data = file_contents.getJSONObject(i);
				Society society = new Society(society_data);
				societyList.add(society);
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private JSONArray readJSONFile(String fileName) throws IOException {
		
		JSONArray file_contents = new JSONArray();
		
		BufferedReader br = new BufferedReader(new FileReader(filePath + fileName));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    
		    file_contents = new JSONArray(sb.toString());
		    
		} finally {
		    br.close();
		}
		
		return file_contents;
		
	}

	public void triggerJobs(String jobName) throws IOException {
		
		Society society;
		
		loadSocietyList();
		
		Iterator<Society> iterator = societyList.iterator();
		
		while(iterator.hasNext()) {
			
			society = iterator.next();
			
			if(jobName == null) {
				// Trigger all jobs
				society.triggerJobs();
			}
			else {
				// Trigger a specific Job
				society.triggerJob(jobName);
			}
			
		}
	}
}