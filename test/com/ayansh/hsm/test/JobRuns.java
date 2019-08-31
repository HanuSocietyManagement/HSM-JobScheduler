package com.ayansh.hsm.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayansh.hsm.test.common.TestApplication;
import com.ayansh.hsm.test.common.TestSociety;

class JobRuns {

	protected static TestApplication app;
	protected static TestSociety society;
	protected JSONObject fi_report;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		// Initialize application
		app = TestApplication.getInstance();
		
		JSONObject config_data = app.getConfigData();
		JSONObject soc_data = new JSONObject();
		soc_data.put("name", config_data.getString("soc_slug"));
		soc_data.put("slug", "");
		soc_data.put("username", config_data.getString("tech_user"));
		soc_data.put("password", config_data.getString("tech_password"));
		society = new TestSociety(soc_data);
		society.performLogin();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		society.doLogout();
	}
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void generateMaintenanceInvoices() {
			
		try {
			
			society.generateMaintenanceInvoices();
			
		} catch (Exception e) {
			fail("Error in generating maintenance invoices: " + e.getMessage());			
		}
		
		assertTrue("Maintenance invoices generated", true);
		
	}
	
	@Test
	void generateExpenseInvoices() {
			
		try {
			
			society.generateExpenseInvoices();
			
		} catch (Exception e) {
			fail("Error in expense invoice generation: " + e.getMessage());			
		}
		
		assertTrue("Expenses invoices generated", true);
		
	}

}
