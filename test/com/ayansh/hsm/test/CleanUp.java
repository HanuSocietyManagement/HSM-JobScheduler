package com.ayansh.hsm.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayansh.hsm.test.common.TestApplication;
import com.ayansh.hsm.test.common.TestSociety;

class CleanUp {

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
		soc_data.put("username", config_data.getString("sec_user"));
		soc_data.put("password", config_data.getString("sec_password"));
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
	void cleanUpFinanceTest() {
			
		try {
			
			cleanUpData();
			fi_report = society.getFIReport();
			
		} catch (Exception e) {
			fail("Error in cleanup: " + e.getMessage());			
		}
		
		assertTrue("Clean up success", true);
		assertEquals(0, fi_report.getDouble("total_income"), "Income Validation");
		assertEquals(0, fi_report.getDouble("total_assets"), "Asset Validation");
		assertEquals(0, fi_report.getDouble("total_expense"), "Expense Validation");
		assertEquals(0, fi_report.getDouble("total_liability"), "Liability Validation");
		
	}
	
	private void cleanUpData() {
		
		try {
			
			String soc_name = "test-soc";
			Connection con = app.getMySQLConnection();
			Statement st = (Statement) con.createStatement();
			
			// Read Society
			ResultSet result = st.executeQuery("SELECT id, address_id FROM hs_society where slug = '" + soc_name + "'");
			result.next();
			int soc_id = result.getInt(1);
						
			st.executeUpdate("DELETE FROM hsf_accounting_doc_i where doc_id in (select id from hsf_accounting_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsf_accounting_doc_h where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsi_business_partner where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsi_invoice_payments where invoice_id in (select id from hsi_invoice_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsi_invoice_doc_i where doc_id in (select id from hsi_invoice_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsi_invoice_doc_h where society_id = " + soc_id);
									
		} catch (SQLException e) {
			fail("Error in cleanup " + e.getMessage());
		}
	}

}
