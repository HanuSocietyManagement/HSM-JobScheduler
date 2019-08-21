/**
 * 
 */
package com.ayansh.hsm.test.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONObject;

/**
 * @author Ayansh TechnoSoft
 *
 */
public class TestApplication {

	protected Properties properties;
	private Connection mySQLConn;
	protected String appURL;
	protected JSONObject config_data;
	protected String filePath;
			
	private static TestApplication app;
	
	public static TestApplication getInstance() throws Exception {
		
		if(app == null) {
			app = new TestApplication();
			app.initializeApplication();
		}
		
		return app;
		
	}
	
	protected TestApplication(){
		
		properties = new Properties();
		
	}
	
	protected void initializeApplication() throws Exception{
		
		properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
	
		String db_host, db_user, db_pwd, db_name;
		
		filePath = properties.getProperty("file_location");
		config_data = readJSONFile("test_config.json");
		
		db_host = config_data.getString("db_host");
		db_user = config_data.getString("db_user");
		db_pwd = config_data.getString("db_pwd");
		db_name = config_data.getString("db_name");
		appURL = config_data.getString("app_url");
		
		String db_url = "jdbc:mysql://" + db_host + "/" + db_name;
		
		Class.forName("com.mysql.jdbc.Driver");
		mySQLConn = DriverManager.getConnection(db_url, db_user,db_pwd);
						
	}
	
	private JSONObject readJSONFile(String fileName) throws IOException {
		
		JSONObject file_contents = new JSONObject();
		
		BufferedReader br = new BufferedReader(new FileReader(filePath + fileName));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    
		    file_contents = new JSONObject(sb.toString());
		    
		} finally {
		    br.close();
		}
		
		return file_contents;
		
	}
	
	protected void closeApplication() throws SQLException{
		
		if (mySQLConn != null) {
			mySQLConn.close();
		}
	}
	
	public Connection getMySQLConnection() {
		return mySQLConn;
	}
	
	public void finish() throws SQLException{
		
		closeApplication();

		System.out.println("I'm Done!");
		System.exit(0);
		
	}
	
	public JSONObject getConfigData() {
		return config_data;
	}

	public String getApplicationURL() {
		return appURL;
	}
	
}