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

class ExpenseInvoices {

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
		double old_amount = fi_report.getDouble("total_expense");
		double old_liability = fi_report.getDouble("total_liability");
		double new_amount = 0;
		double new_liability = 0;
		
		try {
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			invoice_data = app.readJSONFile("test_invoice_data.json");
			invoice_data.put("transaction_date", sdf.format(today));
			
			society.createNewInvoice(invoice_data);
			fi_report = society.getFIReport();
			new_amount = fi_report.getDouble("total_expense");
			new_liability = fi_report.getDouble("total_liability");
			
		} catch (Exception e) {
			fail("Create Invoice fail: " + e.getMessage());			
		}
		
		assertTrue("Invoice Creation success", true);
		assertEquals(old_amount + invoice_data.getDouble("amount"), new_amount, "Expense validation");
		assertEquals(old_liability, new_liability, "Balance validation");
		
	}
	
	@Test
	void createInvoiceWithoutPaymentTest() {
		
		JSONObject invoice_data = new JSONObject();
		double old_amount = fi_report.getDouble("total_expense");
		double old_liability = fi_report.getDouble("total_liability");
		double new_amount = 0;
		double new_liability = 0;
		
		try {
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			invoice_data = app.readJSONFile("test_invoice_data.json");
			invoice_data.put("transaction_date", sdf.format(today));
			invoice_data.put("payment_method", "unpaid");
			invoice_data.put("inv_type", "water-bill");
			
			society.createNewInvoice(invoice_data);
			fi_report = society.getFIReport();
			new_amount = fi_report.getDouble("total_expense");
			new_liability = fi_report.getDouble("total_liability");
			
		} catch (Exception e) {
			fail("Create Invoice fail: " + e.getMessage());			
		}
		
		assertTrue("Invoice Creation success", true);
		assertEquals(old_amount + invoice_data.getDouble("amount"), new_amount, "Expense validation");
		assertEquals(old_liability + invoice_data.getDouble("amount"), new_liability, "Balance validation");
		
	}
	
	@Test
	void makeExpenseInvoicePayment() {
		
		JSONObject pending_invoice = null; 
		double old_amount = fi_report.getDouble("total_expense");
		double old_liability = fi_report.getDouble("total_liability");
		double new_amount = 0;
		double new_liability = 0;
		
		try {
			
			JSONArray pending_invoices = society.getPendingInvoices();
			pending_invoice = null; 
			
			for(int i=0; i<pending_invoices.length(); i++) {
				
				pending_invoice = pending_invoices.getJSONObject(i);
				String tr_type = pending_invoice.getJSONObject("transaction_config").getString("transaction_type");
				if(tr_type.contentEquals("expense")) {
					break;
				}
				pending_invoice = null;
			}
			
			if(pending_invoice == null) {
				fail("No invoices pending for payment");
			}
			
			pending_invoice.put("payment_method", "cash");
			society.paymentForInvoice(pending_invoice);
			
			fi_report = society.getFIReport();
			new_amount = fi_report.getDouble("total_expense");
			new_liability = fi_report.getDouble("total_liability");
			
		}
		catch (Exception e) {
			fail("Payment collection fail: " + e.getMessage());
		}
		
		assertTrue("Payment success", true);
		assertEquals(old_amount, new_amount, "Expenses remain same");
		assertEquals(old_liability -  pending_invoice.getDouble("amount"), new_liability, "Total liability will reduce");
		
	}

}
