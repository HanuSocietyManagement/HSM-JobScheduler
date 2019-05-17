package com.ayansh.hsm.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.ayansh.hsm.jobs.JobScheduler;

public class Society {
	
	private HttpClient httpClient;
	private String name, slug, username, password;
	private String appURL;
	private String refererKey;
	
	public Society(JSONObject society_data) {
		
		appURL = JobScheduler.getInstance().getApplicationURL();
		
		name = society_data.getString("name");
		slug = society_data.getString("slug");
		username = society_data.getString("username");
		password = society_data.getString("password");
		
		refererKey = "HSM-JobScheduler";
		
	}
	
	public String getName() {
		return name;
	}

	public String getSlug() {
		return slug;
	}

	private void performLogin() throws IOException {
			
		String login_url = appURL + "/sync/api/login";
		
		httpClient = new DefaultHttpClient();  
        HttpPost httpPost = new HttpPost(login_url);
        httpPost.setHeader("Referer", refererKey);
        
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("user_name", username));
        params.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		      
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        //response.getEntity().getContent();
        
        System.out.println("Login complete");
        
	}

	public void triggerJobs() throws IOException {
		
		// Perform Login.
		performLogin();
		
		Calendar today = Calendar.getInstance();
		
		// Check for 1st Day
		if(today.get(Calendar.DATE) == 1) {
			
			// Create Maintenance Invoices
			createMaintenanceInvoices();
			
			// Create Expense Invoices
			createExpenseInvoices();
			
			// Send Monthly reports - This will send for last month
			sendMonthlyReports();
			
		}
		else {
			// For all other days... Send Payment reminders
			sendPaymentReminders();
		}
		
	}

	public void triggerJob(String jobName) throws IOException {
		
		// Perform Login.
		performLogin();
		
		if(jobName.contentEquals("Generate-Maintenance-Invoice")) {
			// Create Maintenance Invoices
			createMaintenanceInvoices();
		}
		
		if(jobName.contentEquals("Generate-Expense-Invoice")) {
			// Create Expense Invoices
			createExpenseInvoices();
		}
		
		if(jobName.contentEquals("Send-Payment-Reminders")) {
			// For all other days... Send Payment reminders
			sendPaymentReminders();
		}
		
		if(jobName.contentEquals("Send-Email-Reports")) {
			// Send Monthly reports - This will send for last month
			sendMonthlyReports();
		}
		
	}
	
	private void createMaintenanceInvoices() throws IOException {
		
		String url = appURL + "/job/generate/maintenanceInvoice";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        
        System.out.println("Maintenance invoices created");
	}

	private void createExpenseInvoices() throws IOException {
		
		String url = appURL + "/job/generate/expenseInvoice";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        
        System.out.println("Expense invoices created");
	}

	private void sendMonthlyReports() throws IOException {
		
		String url = appURL + "/job/email_reports";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        
        System.out.println("Monthly reports sent");

	}

	private void sendPaymentReminders() throws IOException {
		
		String url = appURL + "/job/payment/reminder";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        
        System.out.println("Payment Reminders sent");
		
	}

}