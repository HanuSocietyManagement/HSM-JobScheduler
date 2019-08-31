package com.ayansh.hsm.test.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TestSociety {
	
	private HttpClient httpClient;
	private String name, slug, username, password;
	private String appURL;
	private String refererKey;
	
	public TestSociety(JSONObject society_data) throws Exception {
		
		appURL = TestApplication.getInstance().getApplicationURL();
		
		name = society_data.getString("name");
		slug = society_data.getString("slug");
		username = society_data.getString("username");
		password = society_data.getString("password");
		
		refererKey = "HSM-JobScheduler";
		
	}
	
	public void performLogin() throws IOException {
		
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
                
	}

	public void onboardNewSociety(JSONObject soc_data) throws Exception {
		
		String url = appURL + "/api/soc/onboard";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        httpPost.setHeader("Content-Type", "application/json");
        
        httpPost.setEntity(new StringEntity(soc_data.toString()));
        
        HttpResponse response = httpClient.execute(httpPost);
        
        int response_code = response.getStatusLine().getStatusCode(); 
        if(response_code != 200) {
        	throw new Exception("Error code in onboarding. Response code: " + response_code);
        }
        
        EntityUtils.consume(response.getEntity());
		
	}

	public void doLogout() throws Exception{
		
		String logout_url = appURL + "/account/logout";
		
        HttpGet httpGet = new HttpGet(logout_url);        		      
        HttpResponse response = httpClient.execute(httpGet);
        EntityUtils.consume(response.getEntity());
		
	}
	
	public void generateMaintenanceInvoices() throws Exception {
		
		String url = appURL + "/job/generate/maintenanceInvoice";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        int response_code = response.getStatusLine().getStatusCode();
        EntityUtils.consume(response.getEntity());
        
        if(response_code != 200) {
        	throw new Exception("Error in generating maint invoices. Response code: " + response_code);
        }
        
	}
	
	public void generateExpenseInvoices() throws Exception {
		
		String url = appURL + "/job/generate/expenseInvoice";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        HttpResponse response = httpClient.execute(httpPost);
        int response_code = response.getStatusLine().getStatusCode();
        EntityUtils.consume(response.getEntity());
        
        if(response_code != 200) {
        	throw new Exception("Error in generating expense invoices. Response code: " + response_code);
        }
        
	}

	public void createNewInvoice(JSONObject invoice_data) throws Exception{
		
		String url = appURL + "/app/api/v1/invoice/" + invoice_data.getString("inv_type") + "/create";
		
		HttpPost httpPost = new HttpPost(url);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("transaction_date", invoice_data.getString("transaction_date")));
		nameValuePairs.add(new BasicNameValuePair("amount",String.valueOf(invoice_data.getDouble("amount"))));
		nameValuePairs.add(new BasicNameValuePair("payment_method",invoice_data.getString("payment_method")));
		nameValuePairs.add(new BasicNameValuePair("description",invoice_data.getString("description")));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));        
        HttpResponse response = httpClient.execute(httpPost);
        
        int response_code = response.getStatusLine().getStatusCode(); 
        if(response_code != 200) {
        	throw new Exception("Error code in onboarding. Response code: " + response_code);
        }
        
        EntityUtils.consume(response.getEntity());
        
	}

	public JSONObject getFIReport() throws Exception{
		
		String url = appURL + "/app/api/v1/current_statement";
		
        HttpGet httpGet = new HttpGet(url);        		      
        HttpResponse response = httpClient.execute(httpGet);
        int response_code = response.getStatusLine().getStatusCode(); 
        if(response_code != 200) {
        	throw new Exception("Error code in reading balances: " + response_code);
        }
        
        InputStream in = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(isr);
		
		StringBuilder sbuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sbuilder.append(line);
		}
		
		JSONObject fi_data = new JSONObject(sbuilder.toString());
		
		double total_expense = 0;
		double total_income = 0;
		double total_assets = 0;
		double total_liability = 0;
		JSONObject amount;
		
		JSONArray statement_data = fi_data.getJSONArray("report_data");
		JSONObject amounts;
		
		if(statement_data.length() > 0) {
			
			amounts = statement_data.getJSONObject(0).getJSONObject("amounts");
			
			if(amounts.has("operating-expenses")) {
				
				JSONArray expense_amounts = amounts.getJSONArray("operating-expenses");
				for(int i=0; i< expense_amounts.length(); i++) {
					
					amount = expense_amounts.getJSONObject(i);
					total_expense += amount.getDouble("amount");
					
				}
				
			}
			
			if(amounts.has("operating-revenue")) {
				
				JSONArray expense_amounts = amounts.getJSONArray("operating-revenue");
				for(int i=0; i< expense_amounts.length(); i++) {
					
					amount = expense_amounts.getJSONObject(i);
					total_income += amount.getDouble("amount");
					
				}
				
			}
		}		
		
		statement_data = fi_data.getJSONArray("asset_breakup");
		if(statement_data.length() > 0) {
			
			amounts = statement_data.getJSONObject(0).getJSONObject("amounts");
			if(amounts.has("current-assets")) {
				
				JSONArray asset_amounts = amounts.getJSONArray("current-assets");
				for(int i=0; i< asset_amounts.length(); i++) {
					
					amount = asset_amounts.getJSONObject(i);
					total_assets += amount.getDouble("amount");
					
				}
				
			}
			
			amounts = statement_data.getJSONObject(1).getJSONObject("amounts");
			if(amounts.has("current-liabilities")) {
				
				JSONArray liabilities_amounts = amounts.getJSONArray("current-liabilities");
				for(int i=0; i< liabilities_amounts.length(); i++) {
					
					amount = liabilities_amounts.getJSONObject(i);
					total_liability += amount.getDouble("amount");
					
				}
				
			}
		}
		
		JSONObject fi_report = new JSONObject();
		fi_report.put("total_expense", total_expense);
		fi_report.put("total_income", total_income);
		fi_report.put("total_assets", total_assets);
		fi_report.put("total_liability", total_liability);
		
		return fi_report;
	}

	public JSONArray getPendingInvoices() throws Exception{
		
		String url = appURL + "/app/api/v1/invoice/pending/all";
		
        HttpGet httpGet = new HttpGet(url);        		      
        HttpResponse response = httpClient.execute(httpGet);
        int response_code = response.getStatusLine().getStatusCode(); 
        if(response_code != 200) {
        	throw new Exception("Error code in getting pending invoices: " + response_code);
        }
        
        InputStream in = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(isr);
		
		StringBuilder sbuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sbuilder.append(line);
		}
		
		JSONObject result = new JSONObject(sbuilder.toString());
		JSONArray inv_list = result.getJSONArray("rows");
		return inv_list;
		
	}

	public void paymentForInvoice(JSONObject invoice_data) throws Exception{
		
		String url = appURL + "/app/api/v1/invoice/" + invoice_data.getInt("id") + "/payment";
		
		HttpPost httpPost = new HttpPost(url);
		
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("payment_date", sdf.format(today)));
		nameValuePairs.add(new BasicNameValuePair("amount",String.valueOf(invoice_data.getDouble("amount"))));
		nameValuePairs.add(new BasicNameValuePair("payment_method",invoice_data.getString("payment_method")));
		nameValuePairs.add(new BasicNameValuePair("description",invoice_data.getString("description")));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));        
        HttpResponse response = httpClient.execute(httpPost);
        
        int response_code = response.getStatusLine().getStatusCode();
        EntityUtils.consume(response.getEntity());
        if(response_code != 200) {
        	throw new Exception("Error code in payment collection. Response code: " + response_code);
        }
		
	}
}