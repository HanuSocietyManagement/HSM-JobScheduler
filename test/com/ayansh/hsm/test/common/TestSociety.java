package com.ayansh.hsm.test.common;

import java.io.IOException;
import java.util.ArrayList;
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
		
		httpClient = new DefaultHttpClient();  
        HttpGet httpGet = new HttpGet(logout_url);
        		      
        HttpResponse response = httpClient.execute(httpGet);
        EntityUtils.consume(response.getEntity());
		
	}

	public void createNewInvoice(JSONObject invoice_data) {
		// TODO Auto-generated method stub
		
	}

}