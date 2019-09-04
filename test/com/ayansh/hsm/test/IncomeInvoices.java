package com.ayansh.hsm.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayansh.hsm.test.common.TestApplication;
import com.ayansh.hsm.test.common.TestSociety;

class IncomeInvoices {

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
		fi_report = society.getFIReport();		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void createInvoiceTest() {
		
		JSONObject invoice_data = new JSONObject();
		double old_income = fi_report.getDouble("total_income");
		double old_assets = fi_report.getDouble("total_assets");
		double new_income = 0;
		double new_assets = 0;
		
		try {
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			invoice_data.put("transaction_date", sdf.format(today));
			invoice_data.put("payment_method", "cash");
			invoice_data.put("amount", 500.0);
			invoice_data.put("inv_type", "collect-maintenance");
			invoice_data.put("description", "Generic maint collection");
			
			society.createNewInvoice(invoice_data);
			fi_report = society.getFIReport();
			new_income = fi_report.getDouble("total_income");
			new_assets = fi_report.getDouble("total_assets");
			
		} catch (Exception e) {
			fail("Create Invoice fail: " + e.getMessage());			
		}
		
		assertTrue("Invoice Creation success", true);
		assertEquals(old_income + invoice_data.getDouble("amount"), new_income, "Income validation");
		assertEquals(old_assets + invoice_data.getDouble("amount"), new_assets, "Balance validation");
		
	}
	
	@Test
	void collectMaintenanceInvoicePayment() {
		
		JSONObject pending_invoice = null; 
		double old_income = fi_report.getDouble("total_income");
		double old_assets = fi_report.getDouble("total_assets");
		double new_income = 0;
		double new_assets = 0;
		
		try {
			
			JSONArray pending_invoices = society.getPendingInvoices();
			pending_invoice = null; 
			
			for(int i=0; i<pending_invoices.length(); i++) {
				
				pending_invoice = pending_invoices.getJSONObject(i);
				String tr_type = pending_invoice.getJSONObject("transaction_config").getString("transaction_type");
				if(tr_type.contentEquals("revenue")) {
					break;
				}
				pending_invoice = null;
			}
			
			if(pending_invoice == null) {
				fail("No invoices pending for collection");
			}
			
			pending_invoice.put("payment_method", "cash");
			society.paymentForInvoice(pending_invoice);
			
			fi_report = society.getFIReport();
			new_income = fi_report.getDouble("total_income");
			new_assets = fi_report.getDouble("total_assets");
			
		}
		catch (Exception e) {
			fail("Payment collection fail: " + e.getMessage());
		}
		
		assertTrue("Payment success", true);
		assertEquals(old_income + pending_invoice.getDouble("amount"), new_income, "Income validation");
		assertEquals(old_assets, new_assets, "Total assets should remain same");
		
	}
	
	@Test
	void collectPartialInvoicePayment() {
		
		JSONObject pending_invoice = null; 
		JSONObject invoice_data = null;
		double old_income = fi_report.getDouble("total_income");
		double old_assets = fi_report.getDouble("total_assets");
		double new_income = 0;
		double new_assets = 0;
		double invoice_amount = 0;
		
		try {
			
			JSONArray pending_invoices = society.getPendingInvoices();
			pending_invoice = null; 
			
			for(int i=0; i<pending_invoices.length(); i++) {
				
				pending_invoice = pending_invoices.getJSONObject(i);
				String tr_type = pending_invoice.getJSONObject("transaction_config").getString("transaction_type");
				if(tr_type.contentEquals("revenue")) {
					break;
				}
				pending_invoice = null;
			}
			
			if(pending_invoice == null) {
				fail("No invoices pending for collection");
			}
			
			invoice_amount = pending_invoice.getDouble("amount");
			pending_invoice.put("amount", 100.0);	// Pay only 100
			
			pending_invoice.put("payment_method", "cash");
			society.paymentForInvoice(pending_invoice);
			
			invoice_data = society.getInvoiceDetails(pending_invoice.getInt("id"));
			fi_report = society.getFIReport();
			new_income = fi_report.getDouble("total_income");
			new_assets = fi_report.getDouble("total_assets");
			
		}
		catch (Exception e) {
			fail("Payment collection fail: " + e.getMessage());
		}
		
		assertTrue("Payment success", true);
		
		assertEquals(invoice_data.getString("payment_status"), "partially-paid", "Status is partially paid");
		
		assertEquals(old_income + pending_invoice.getDouble("amount"), new_income, "Income validation");
		assertEquals(old_assets, new_assets, "Total assets should remain same");
		
	}

}
