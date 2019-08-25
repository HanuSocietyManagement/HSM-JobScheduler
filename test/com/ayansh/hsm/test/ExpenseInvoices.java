package com.ayansh.hsm.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayansh.hsm.test.common.TestApplication;
import com.ayansh.hsm.test.common.TestSociety;

class ExpenseInvoices {

	protected TestApplication app;
	protected TestSociety society;
	
	@BeforeEach
	void setUp() throws Exception {
		
		// Initialize application
		app = TestApplication.getInstance();
		
		JSONObject config_data = app.getConfigData();
		JSONObject soc_data = new JSONObject();
		soc_data.put("name", config_data.getString("soc_slug"));
		soc_data.put("slug", "");
		soc_data.put("username", config_data.getString("sec_user"));
		soc_data.put("password", config_data.getString("sec_password"));
		society = new TestSociety(soc_data);
		society.performLogin();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void createInvoiceTest() {
		
		try {
			
			JSONObject invoice_data = app.readJSONFile("test_soc_onboard.json");
			society.createNewInvoice(invoice_data);
			
			
		} catch (Exception e) {
			fail("Create Invoice fail: " + e.getMessage());			
		}
		
		assertTrue("Invoice Creation success", true);
		
	}

}
