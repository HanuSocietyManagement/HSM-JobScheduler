package com.ayansh.hsm.jobs;

public class Main {

	public static void main(String[] args) {
		
		// Initialize Job Scheduler
		JobScheduler app = JobScheduler.getInstance();
		
		try {
			
			app.initializeApplication();
			
			String jobName = null;

			if(args.length == 0) {
				// Trigger all jobs
			}
			else if(args.length == 1){
				jobName = args[0];
			}
			else {
				throw new Exception("Invalid arguments");
			}
			
			app.triggerJobs(jobName);

		}
		catch(Exception e) {
			System.out.println("Error in Main: " + e.getMessage());
		}
		
		try {
			
			// Trigger execution shut down and wait.
			app.shutdownExecutor();
			
		}
		catch(Exception e) {
			System.out.println("Error in Shutdown Executor" + e.getMessage());
		}
		
		// Finish App.
		app.finish();
	}
}