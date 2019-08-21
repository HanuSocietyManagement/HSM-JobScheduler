package com.ayansh.hsm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayansh.hsm.test.common.TestApplication;
import com.ayansh.hsm.test.common.TestSociety;

class Onboarding {
	
	protected TestApplication app;

	@BeforeEach
	void setUp() throws Exception {
		
		// Initialize application
		app = TestApplication.getInstance();
		
		// Delete the existing data of society.
		deleteSocietyData();
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void onboardNewSociety() {
		
		JSONObject config_data = app.getConfigData();
		
		JSONObject soc_data = new JSONObject();
		soc_data.put("name", "");
		soc_data.put("slug", "");
		soc_data.put("username", config_data.getString("admin_user"));
		soc_data.put("password", config_data.getString("admin_pwd"));
		
		try {
			
			TestSociety soc = new TestSociety(soc_data);
			soc.performLogin();
			soc.onboardNewSociety();
			
		} catch (Exception e) {
			fail(e.getMessage());			
		}
		
		int status_code = 0;
		assertEquals("Dummy Test ",0,status_code);
		
	}
	
	private void deleteSocietyData() {
		
		try {
			
			String soc_name = "test-society";
			Connection con = app.getMySQLConnection();
			Statement st = (Statement) con.createStatement();
			
			// Read Society
			ResultSet result = st.executeQuery("SELECT id, address_id FROM hs_society where slug = '" + soc_name + "'");
			result.next();
			int soc_id = result.getInt(1);
			int add_id = result.getInt(2);
						
			//st.executeUpdate("DELETE FROM hsc_notification_log"); -- Can't delete
			
			st.executeUpdate("DELETE FROM hsf_accounting_doc_i where doc_id in (select id from hsf_accounting_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsf_accounting_doc_h where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsi_business_partner where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsi_invoice_payments where invoice_id in (select id from hsi_invoice_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsi_invoice_doc_i where doc_id in (select id from hsi_invoice_doc_h where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hsi_invoice_doc_h where society_id = " + soc_id);
			
			st.executeUpdate("DELETE FROM hs_app_tokens where user_id in (select user_id from hs_soc_member_details where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hs_house_members where house_id in (select id from hs_house where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hs_house_metadata where house_id in (select id from hs_house where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hs_house where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hs_member_invitations where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hs_member_preferences where member_id in (select id from hs_soc_member_details where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hs_society_members where soceity_id = " + soc_id);
			st.executeUpdate("DELETE FROM hs_members where member_id in (select id from hs_soc_member_details where society_id = " + soc_id + ")");
			st.executeUpdate("DELETE FROM hs_soc_config_settings where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsf_transaction_posting_config where society_id = " + soc_id);
			st.executeUpdate("DELETE FROM hsf_society_transactions where society_id = " + soc_id);
			
			st.executeUpdate("DELETE FROM hs_address where id = " + add_id);
			st.executeUpdate("DELETE FROM hs_society where id = " + soc_id);
			
			
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
	}

}