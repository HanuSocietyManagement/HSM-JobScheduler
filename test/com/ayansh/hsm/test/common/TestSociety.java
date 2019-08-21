package com.ayansh.hsm.test.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
        //response.getEntity().getContent();
        
        System.out.println("Login complete");
        
	}

	public void onboardNewSociety() throws Exception {
		
		String url = appURL + "/job/generate/maintenanceInvoice";
		
		HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Referer", refererKey);
        
        JSONObject request_body = new JSONObject();
        httpPost.setEntity(new StringEntity(request_body.toString()));
        
        HttpResponse response = httpClient.execute(httpPost);
        EntityUtils.consume(response.getEntity());
        
        System.out.println("Onboarding completed");
		
	}

}