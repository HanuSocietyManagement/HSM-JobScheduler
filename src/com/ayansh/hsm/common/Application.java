/**
 * 
 */
package com.ayansh.hsm.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Ayansh TechnoSoft
 *
 */
public abstract class Application {

	protected Properties properties;
	
	protected ExecutorService executor;
	protected int thread_limit;
	protected int await_termination_time;
	
	protected String filePath;
	protected String appURL;
	
	protected Application(){
		
		properties = new Properties();
		
	}
	
	public void initializeApplication() throws IOException, SQLException{
		
		properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
				
		// Thread Limit
		thread_limit = Integer.valueOf(properties.getProperty("thread_limit"));
		
		await_termination_time = Integer.valueOf(properties.getProperty("await_termination_time"));
		
		// File Path
		filePath = properties.getProperty("file_location");
		
		// App Url
		appURL = properties.getProperty("app_url");
						
	}
	
	public void initializeExecutorService(){
		
		// Initialize Executor Service
		executor = Executors.newFixedThreadPool(thread_limit);

	}
	
	protected void closeApplication(){
	}
	
	public void finish(){
		
		closeApplication();

		System.out.println("I'm Done!");
		System.exit(0);
		
	}
	
	public ExecutorService getExecutor(){
		return executor;
	}
	
	public void shutdownExecutor() throws InterruptedException{
		
		// After all tasks are executed, shutdown the executer.
		executor.shutdown();
				
		// But wait. Wait until 3 minutes
		executor.awaitTermination(await_termination_time, TimeUnit.SECONDS);
		
	}
	
	public String getApplicationURL() {
		
		return appURL;
	}
}